package com.actionteam.geometryadventures.systems;

import com.actionteam.geometryadventures.Clock;
import com.actionteam.geometryadventures.GameUtils;
import com.actionteam.geometryadventures.components.Components;
import com.actionteam.geometryadventures.components.GraphicsComponent;
import com.actionteam.geometryadventures.components.PhysicsComponent;
import com.actionteam.geometryadventures.ecs.ECSEventListener;
import com.actionteam.geometryadventures.ecs.System;
import com.actionteam.geometryadventures.events.ECSEvents;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

    private static final int[] MOVING_LEFT = new int[]{2, 6, 10, 14};
    private static final int[] MOVING_RIGHT = new int[]{4, 8, 12, 16};
    private static final int[] MOVING_UP = new int[]{1, 5, 9, 13};
    private static final int[] MOVING_DOWN = new int[]{7, 11, 15, 19};

    private ScreenViewport viewport;
    private SpriteBatch batch;
    private TextureAtlas textureAtlas;
    private boolean flag = true;
    private List<CompEnt> entityList;
    CompEnt player;
    private LightSystem lightSystem;
    private ShaderProgram shader;

    private class CompEnt {
        GraphicsComponent gc;
        PhysicsComponent pc;
        int entity;

        CompEnt(GraphicsComponent gc, PhysicsComponent pc, int entity) {
            this.gc = gc;
            this.pc = pc;
            this.entity = entity;
        }
    }

    public GraphicsSystem(GameUtils gameUtils) {
        super(Components.GRAPHICS_COMPONENT_CODE, Components.PHYSICS_COMPONENT_CODE);
        viewport = new ScreenViewport();
        batch = new SpriteBatch();
        textureAtlas = new TextureAtlas(gameUtils.
                getFile("env_packed/envTextureAtlas.atlas").getPath());
        entityList = new ArrayList<CompEnt>();
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
        CompEnt ent = new CompEnt(gc, pc, entityId);
        if (ecsManager.entityHasComponent(entityId, Components.CONTROL_COMPONENT_CODE)) {
            player = ent;
        }
        entityList.add(ent);
    }

    /**
     * Removes the graphics component and physics component which were stored
     */
    @Override
    protected void entityRemoved(int entityId, int index) {
        entityList.remove(index);
    }

    /**
     * Draws all entities possessing graphics and physics components
     *
     * @param dt Time difference from last update
     */
    @Override
    public void update(float dt) {
        float angle = player.pc.velocity.angle();
        if (player.pc.velocity.x == 0 && player.pc.velocity.y == 0) {
            player.gc.isAnimated = false;
        } else if ((angle <= 45 && angle >= 0) || (angle >= 360 - 45 && angle <= 360)) {
            player.gc.animationSequence = MOVING_RIGHT;
            player.gc.isAnimated = true;
            player.gc.frames = MOVING_RIGHT.length;
        } else if (angle >= 180 - 45 && angle <= 180 + 45) {
            player.gc.animationSequence = MOVING_LEFT;
            player.gc.isAnimated = true;
            player.gc.frames = MOVING_LEFT.length;
        } else if (angle >= 45 && angle <= 90 + 45) {
            player.gc.animationSequence = MOVING_UP;
            player.gc.isAnimated = true;
            player.gc.frames = MOVING_UP.length;
        } else if (angle >= 270 - 45 && angle <= 270 + 45) {
            player.gc.animationSequence = MOVING_DOWN;
            player.gc.isAnimated = true;
            player.gc.frames = MOVING_DOWN.length;
        } else {
            player.gc.isAnimated = false;
        }

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        batch.setShader(shader);
        for (CompEnt e : entityList) {
            draw(e.gc, e.pc);
        }
        batch.end();
    }

    /**
     * Draws a specific entity
     *
     * @param graphicsComponent The graphics component of that entity
     * @param physicsComponent  The physics component of that entity
     */
    private void draw(GraphicsComponent graphicsComponent, PhysicsComponent physicsComponent) {
        if (graphicsComponent.isAnimated) {
            int index = (Clock.clock / graphicsComponent.interval) %
                    graphicsComponent.frames;
            graphicsComponent.textureIndex = graphicsComponent.animationSequence[index] - 1;
        }
        if (graphicsComponent.rotationAngle != 0) {
            batch.draw(graphicsComponent.regions.get(graphicsComponent.textureIndex),
                    physicsComponent.position.x + graphicsComponent.offsetX,
                    physicsComponent.position.y + graphicsComponent.offsetY,
                    graphicsComponent.width / 2f,
                    graphicsComponent.height / 2f,
                    graphicsComponent.width, graphicsComponent.height,
                    1, 1, graphicsComponent.rotationAngle);
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
            viewport.setUnitsPerPixel(15.f / width);
            flag = false;
        }
        viewport.update(width, height, true);
    }

    public void setLightSystem(LightSystem lightSystem) {
        this.lightSystem = lightSystem;
        this.shader = lightSystem.getShaderProgram();
    }
}