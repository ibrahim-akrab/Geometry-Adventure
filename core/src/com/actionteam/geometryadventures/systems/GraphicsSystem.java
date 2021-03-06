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

    // texture indices of sprite animations
    private static final int[] MOVING_LEFT = new int[]{1, 5, 9, 13, 17, 21, 25, 29, 33};
    private static final int[] MOVING_RIGHT = new int[]{3, 7, 11, 15, 19, 23, 27, 31, 35};
    private static final int[] MOVING_UP = new int[]{0, 4, 8, 12, 16, 20, 24, 28, 32};
    private static final int[] MOVING_DOWN = new int[]{2, 6, 10, 14, 18, 22, 26, 30, 34};
    private static final int[] ANIM_DYING = new int[]{36, 37, 38, 39, 40, 41};
    private static final int[] ATTACK_UP = new int[]{42, 43, 44, 45, 46, 47, 48, 49};
    private static final int[] ATTACK_LEFT = new int[]{50, 51, 52, 53, 54, 55, 56, 57};
    private static final int[] ATTACK_DOWN = new int[]{58, 59, 60, 61, 62, 63, 64, 65};
    private static final int[] ATTACK_RIGHT = new int[]{66, 67, 68, 69, 70, 71, 72, 73};
    public static final int[] BULLET_ANIMATION = new int[]{0, 1, 2, 3, 4, 5, 6, 7};

    private ScreenViewport viewport;
    // used to draw textures
    private SpriteBatch batch;
    private TextureAtlas textureAtlas;
    private boolean flag = true;
    private List<GraphicEntity> entityList;
    private GraphicEntity player;
    private List<GraphicEntity> enemies;
    private ShaderProgram shader;

    private class GraphicEntity {
        GraphicsComponent gc;
        PhysicsComponent pc;
        CacheComponent cc;
        int entity;

        GraphicEntity(GraphicsComponent gc, PhysicsComponent pc, CacheComponent cc, int entity) {
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
        entityList = new ArrayList<GraphicEntity>();
        enemies = new ArrayList<GraphicEntity>();
    }

    @Override
    protected void ecsManagerAttached() {
        // subscribe to events
        ecsManager.subscribe(ECSEvents.RESIZE_EVENT, this);
        ecsManager.subscribe(ECSEvents.PLAYER_MOVED_EVENT, this);
        ecsManager.subscribe(ECSEvents.ENEMY_DEAD_EVENT, this);
        ecsManager.subscribe(ECSEvents.SUCCESSFUL_CAST_EVENT, this);
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
        GraphicEntity ent = new GraphicEntity(gc, pc, cc, entityId);
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
        for (GraphicEntity enemy : enemies) {
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
        for (GraphicEntity enemy : enemies) {
            if (enemy.cc.isCached && !enemy.gc.scripted)
                updateSprite(enemy);
        }
        if (!player.gc.scripted)
            updateSprite(player);

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        // if player is not mid animation, update animation
        for (GraphicEntity e : entityList) {
            if (e.cc.isCached)
                draw(e.gc, e.pc);
        }
        batch.end();
    }

    /**
     * update entity texture for animation
     *
     * @param ent entity to be updated
     */
    private void updateSprite(GraphicEntity ent) {
        float angle = ent.pc.rotationAngle;
        ent.gc.isAnimated = !(ent.pc.velocity.x == 0 && ent.pc.velocity.y == 0);
        if ((angle <= 45 && angle >= 0) || (angle >= 360 - 45 && angle <= 360)) {
            ent.gc.animationSequence = MOVING_RIGHT;
        } else if (angle >= 180 - 45 && angle <= 180 + 45) {
            ent.gc.animationSequence = MOVING_LEFT;
        } else if (angle >= 45 && angle <= 90 + 45) {
            ent.gc.animationSequence = MOVING_UP;
        } else if (angle >= 270 - 45 && angle <= 270 + 45) {
            ent.gc.animationSequence = MOVING_DOWN;
        } else {
            ent.gc.isAnimated = false;
        }
        ent.gc.textureIndex = ent.gc.animationSequence[0];
        ent.gc.interval = 200;
    }

    /**
     * Draws a specific entity
     *
     * @param graphicsComponent The graphics component of that entity
     * @param physicsComponent  The physics component of that entity
     */
    private void draw(GraphicsComponent graphicsComponent, PhysicsComponent physicsComponent) {
        if (graphicsComponent.isAnimated)
            updateTextureIndex(graphicsComponent, physicsComponent);
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

    private void updateTextureIndex(GraphicsComponent graphicsComponent, PhysicsComponent physicsComponent) {
        int index = (ClockSystem.millis() / graphicsComponent.interval) %
                graphicsComponent.frames();
        if (graphicsComponent.animationSequence == null)
            graphicsComponent.textureIndex = index;
        else if (!graphicsComponent.scripted)
            graphicsComponent.textureIndex = graphicsComponent.animationSequence[index];
        else {
            index = (index + graphicsComponent.indexOffset) % graphicsComponent.frames();
            while (index < 0) index += graphicsComponent.frames();
            graphicsComponent.textureIndex = graphicsComponent.animationSequence[index];
            if (index == graphicsComponent.frames() - 1) {
                graphicsComponent.isAnimated = false;
                graphicsComponent.scripted = false;
            }
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
                break;
            case ECSEvents.ENEMY_DEAD_EVENT:
                int enemyId = ((int[]) (message))[0];
                startDeathAnimation(enemyId);
                break;
            case ECSEvents.SUCCESSFUL_CAST_EVENT:
                float[] attackInfo = (float[]) message;
                int entityId = (int) attackInfo[3];
                boolean isPlayer = (attackInfo[4] == 1);
                startAttackAnimation(isPlayer ? player : getEnemy(entityId));
                break;
            case ECSEvents.DISPOSE_EVENT:
                dispose();
                break;
        }
        return false;
    }

    private void dispose() {
        batch.dispose();
        shader.dispose();
        textureAtlas.dispose();
    }

    private GraphicEntity getEnemy(int entityId) {
        for (GraphicEntity e : enemies) if (e.entity == entityId) return e;
        return null;
    }

    private void startAttackAnimation(GraphicEntity ent) {
        if (ent == null || ent.gc.scripted) return;
        ent.gc.isAnimated = true;
        ent.gc.scripted = true;
        float angle = ent.pc.rotationAngle;
        if ((angle <= 45 && angle >= 0) || (angle >= 360 - 45 && angle <= 360)) {
            ent.gc.animationSequence = ATTACK_RIGHT;
        } else if (angle >= 180 - 45 && angle <= 180 + 45) {
            ent.gc.animationSequence = ATTACK_LEFT;
        } else if (angle >= 45 && angle <= 90 + 45) {
            ent.gc.animationSequence = ATTACK_UP;
        } else if (angle >= 270 - 45 && angle <= 270 + 45) {
            ent.gc.animationSequence = ATTACK_DOWN;
        }
        ent.gc.interval = 70;
        ent.gc.indexOffset = -(ClockSystem.millis() / ent.gc.interval) % ent.gc.frames();
    }


    private void startDeathAnimation(int enemyId) {
        GraphicsComponent gc = (GraphicsComponent)
                ecsManager.getComponent(enemyId, Components.GRAPHICS_COMPONENT_CODE);
        PhysicsComponent pc = (PhysicsComponent)
                ecsManager.getComponent(enemyId, Components.PHYSICS_COMPONENT_CODE);
        gc.isAnimated = true;
        gc.scripted = true;
        gc.animationSequence = ANIM_DYING;
        gc.indexOffset = -(ClockSystem.millis() / gc.interval) % gc.frames();
        gc.rotatable = true;
        pc.rotationAngle -= (float) Math.PI / 2;
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
        this.shader = lightSystem.getShaderProgram();
        batch.setShader(shader);
    }

    public TextureAtlas getTextureAtlas() {
        return textureAtlas;
    }
}