package com.actionteam.geometryadventures.systems;

import com.actionteam.geometryadventures.GameUtils;
import com.actionteam.geometryadventures.components.CacheComponent;
import com.actionteam.geometryadventures.components.Components;
import com.actionteam.geometryadventures.components.GraphicsComponent;
import com.actionteam.geometryadventures.components.PhysicsComponent;
import com.actionteam.geometryadventures.ecs.ECSEventListener;
import com.actionteam.geometryadventures.ecs.System;
import com.actionteam.geometryadventures.events.ECSEvents;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.List;

/**
 * This system is responsible for displaying all of the in game graphics
 * <p>
 * Created by theartful on 3/27/18.
 */

public class GraphicsSystem extends System implements ECSEventListener {

    private static final int[] MOVING_LEFT = new int[]{1, 5, 9, 13};
    private static final int[] MOVING_RIGHT = new int[]{3, 7, 11, 15};
    private static final int[] MOVING_UP = new int[]{0, 4, 8, 12};
    private static final int[] MOVING_DOWN = new int[]{6, 10, 14, 18};
    public static final int[] BULLET_ANIMATION = new int[]{0, 1, 2, 3, 4, 5, 6, 7};

    private ScreenViewport viewport;
    private SpriteBatch batch;
    private TextureAtlas textureAtlas;
    private boolean flag = true;
    private List<CompEnt> entityList;
    private CompEnt player;
    private List<CompEnt> enemies;
    private LightSystem lightSystem;
    private ShaderProgram shader;

    private class CompEnt {
        GraphicsComponent gc;
        PhysicsComponent pc;
        CacheComponent cc;
        int entity;

        CompEnt(GraphicsComponent gc, PhysicsComponent pc, CacheComponent cc,
                int entity) {
            this.gc = gc;
            this.pc = pc;
            this.cc = cc;
            this.entity = entity;
        }
    }

    public GraphicsSystem(GameUtils gameUtils) {
        super(Components.GRAPHICS_COMPONENT_CODE, Components.PHYSICS_COMPONENT_CODE,
                Components.CACHE_COMPONENT_CODE);
        viewport = new ScreenViewport();
        batch = new SpriteBatch();
        textureAtlas = new TextureAtlas(gameUtils.
                getFile("env_packed/envTextureAtlas.atlas").getPath());
        entityList = new ArrayList<CompEnt>();
        enemies = new ArrayList<CompEnt>();
    }

    @Override
    protected void ecsManagerAttached() {
        // subscribe to events
        ecsManager.subscribe(ECSEvents.RESIZE_EVENT, this);
        ecsManager.subscribe(ECSEvents.PLAYER_MOVED_EVENT, this);
    }

    /**
     * Stores the graphics component and physics component for further usage instead of querying the
     * ecsManager each time they are needed
     */
    @Override
    protected void entityAdded(int entityId) {
        GraphicsComponent gc = (GraphicsComponent) ecsManager.getComponent(entityId,
                Components.GRAPHICS_COMPONENT_CODE);
        gc.regions = textureAtlas.findRegions(gc.textureName);
        PhysicsComponent pc = (PhysicsComponent) ecsManager.getComponent(entityId,
                Components.PHYSICS_COMPONENT_CODE);
        CacheComponent cc = (CacheComponent) ecsManager.getComponent(entityId,
                Components.CACHE_COMPONENT_CODE);
        CompEnt ent = new CompEnt(gc, pc, cc, entityId);
        if (ecsManager.entityHasComponent(entityId, Components.CONTROL_COMPONENT_CODE)) {
            player = ent;
        } else if (ecsManager.entityHasComponent(entityId, Components.ENEMY_COMPONENT_CODE)) {
            enemies.add(ent);
        }
        entityList.add(ent);
    }

    /**
     * Removes the graphics component and physics component which were stored
     */
    @Override
    protected void entityRemoved(int entityId, int index) {
        entityList.remove(index);
        if (!ecsManager.entityHasComponent(entityId, Components.ENEMY_COMPONENT_CODE))
            return;
        for (CompEnt enemy : enemies) {
            if (enemy.entity == entityId) {
                enemies.remove(enemy);
                break;
            }
        }
    }


    /**
     * Draws all entities possessing graphics and physics components
     *
     * @param dt Time difference from last update
     */
    @Override
    public void update(float dt) {
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        for (CompEnt enemy : enemies) {
            if (enemy.cc.isCached)
                updateSprite(enemy);
        }
        updateSprite(player);
        for (CompEnt e : entityList) {
            if (e.cc.isCached)
                draw(e.gc, e.pc);
        }
        batch.end();

    }

    private void updateSprite(CompEnt ent) {
        float angle = ent.pc.rotationAngle;
        if (ent.pc.velocity.x == 0 && ent.pc.velocity.y == 0) {
            ent.gc.isAnimated = false;
        } else {
            ent.gc.isAnimated = true;
        }
        if ((angle <= 45 && angle >= 0) || (angle >= 360 - 45 && angle <= 360)) {
            ent.gc.animationSequence = MOVING_RIGHT;
            ent.gc.frames = MOVING_RIGHT.length;
        } else if (angle >= 180 - 45 && angle <= 180 + 45) {
            ent.gc.animationSequence = MOVING_LEFT;
            ent.gc.frames = MOVING_LEFT.length;
        } else if (angle >= 45 && angle <= 90 + 45) {
            ent.gc.animationSequence = MOVING_UP;
            ent.gc.frames = MOVING_UP.length;
        } else if (angle >= 270 - 45 && angle <= 270 + 45) {
            ent.gc.animationSequence = MOVING_DOWN;
            ent.gc.frames = MOVING_DOWN.length;
        } else {
            ent.gc.isAnimated = false;
        }
        ent.gc.textureIndex = ent.gc.animationSequence[0];
    }

    /**
     * Draws a specific entity
     *
     * @param graphicsComponent The graphics component of that entity
     * @param physicsComponent  The physics component of that entity
     */
    private void draw(GraphicsComponent graphicsComponent, PhysicsComponent physicsComponent) {
        if (graphicsComponent.isAnimated) {
            int index = (ClockSystem.clock / graphicsComponent.interval) %
                    graphicsComponent.frames;
            if (graphicsComponent.animationSequence == null)
                graphicsComponent.textureIndex = index;
            else
                graphicsComponent.textureIndex = graphicsComponent.animationSequence[index];
        }
        if (graphicsComponent.rotatable) {
            batch.draw(graphicsComponent.regions.get(graphicsComponent.textureIndex),
                    physicsComponent.position.x + graphicsComponent.offsetX,
                    physicsComponent.position.y + graphicsComponent.offsetY,
                    (graphicsComponent.width + graphicsComponent.offsetX) / 2f,
                    (graphicsComponent.height + graphicsComponent.offsetY) / 2f,
                    graphicsComponent.width, graphicsComponent.height,
                    1, 1,
                    180 - (float) Math.toDegrees(physicsComponent.rotationAngle));
        } else {
            batch.draw(graphicsComponent.regions.get(graphicsComponent.textureIndex),
                    physicsComponent.position.x + graphicsComponent.offsetX,
                    physicsComponent.position.y + graphicsComponent.offsetY,
                    graphicsComponent.width, graphicsComponent.height);
        }
    }

    @Override
    public boolean handle(int eventCode, Object message) {
        switch (eventCode) {
            case ECSEvents.RESIZE_EVENT:
                int[] size = (int[]) message;
                resize(size[0], size[1]);
                break;
            case ECSEvents.PLAYER_MOVED_EVENT:
                float[] position = (float[]) message;
                updateCameraPosition(position[0], position[1]);
        }
        return false;
    }

    private void updateCameraPosition(float x, float y) {
        viewport.getCamera().position.set(x, y, 0);
    }

    private void resize(int width, int height) {
        if (flag) {
            viewport.setUnitsPerPixel(20.f / width);
            flag = false;
        }
        viewport.update(width, height, true);
    }

    public void setLightSystem(LightSystem lightSystem) {
        this.lightSystem = lightSystem;
        this.shader = lightSystem.getShaderProgram();
        batch.setShader(shader);
    }

    public TextureAtlas getTextureAtlas() {
        return textureAtlas;
    }
}