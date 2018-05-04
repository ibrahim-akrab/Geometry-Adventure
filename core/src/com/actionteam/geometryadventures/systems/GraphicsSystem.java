package com.actionteam.geometryadventures.systems;

import com.actionteam.geometryadventures.Clock;
import com.actionteam.geometryadventures.GameUtils;
import com.actionteam.geometryadventures.components.Components;
import com.actionteam.geometryadventures.components.GraphicsComponent;
import com.actionteam.geometryadventures.components.PhysicsComponent;
import com.actionteam.geometryadventures.ecs.ECSEventListener;
import com.actionteam.geometryadventures.ecs.System;
import com.actionteam.geometryadventures.events.ECSEvents;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.List;

import box2dLight.PointLight;
import box2dLight.RayHandler;

/**
 * This system is responsible for displaying all of the in game graphics
 * <p>
 * Created by theartful on 3/27/18.
 */

public class GraphicsSystem extends System implements ECSEventListener {

    private ScreenViewport viewport;
    private SpriteBatch batch;
    private TextureAtlas textureAtlas;
    private boolean flag = true;
    private List<GraphicsComponent> graphicsComponentList;
    private List<PhysicsComponent> physicsComponentList;

    private World world;
    private RayHandler rayHandler;

    public GraphicsSystem(GameUtils gameUtils) {
        super(Components.GRAPHICS_COMPONENT_CODE, Components.PHYSICS_COMPONENT_CODE);
        viewport = new ScreenViewport();
        batch = new SpriteBatch();
        textureAtlas = new TextureAtlas(gameUtils.
                getFile("textureatlas/textureatlas.atlas").getPath());
        graphicsComponentList = new ArrayList<GraphicsComponent>();
        physicsComponentList = new ArrayList<PhysicsComponent>();

        world = new World(new Vector2(0, 0), false);
        rayHandler = new RayHandler(world);
        rayHandler.setShadows(true);
        rayHandler.setAmbientLight(1, 1, 1, 0.1f);
        rayHandler.setBlurNum(3);
        new PointLight(rayHandler, 40, new Color(1, 1, 1, 0.7f), 3, 0, 0);

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
        if (gc.isAnimated) {
            gc.region = new TextureRegion[gc.frames];
            for (int i = 0; i < gc.frames; i++) {
                if (gc.indices == null)
                    gc.region[i] = textureAtlas.findRegion(gc.textureName, i);
                else
                    gc.region[i] = textureAtlas.findRegion(gc.textureName, gc.indices[i]);
            }
        } else
            gc.region = new TextureRegion[]{
                    textureAtlas.findRegion(gc.textureName, gc.textureIndex)};
        graphicsComponentList.add(gc);
        PhysicsComponent physicsComponent = (PhysicsComponent) ecsManager.getComponent(entityId,
                Components.PHYSICS_COMPONENT_CODE);
        physicsComponentList.add(physicsComponent);
    }

    /**
     * Removes the graphics component and physics component which were stored
     */
    @Override
    protected void entityRemoved(int entityId, int index) {
        graphicsComponentList.remove(index);
        physicsComponentList.remove(index);
    }

    /**
     * Draws all entities possessing graphics and physics components
     *
     * @param dt Time difference from last update
     */
    @Override
    public void update(float dt) {
        viewport.apply();
        batch.begin();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        int length = graphicsComponentList.size();
        for (int i = 0; i < length; i++) {
            draw(graphicsComponentList.get(i), physicsComponentList.get(i));
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
        int index = 0;
        if (graphicsComponent.isAnimated) {
            index = (int) (Clock.clock / graphicsComponent.animationSpeed) %
                    graphicsComponent.frames;
        }
        if (physicsComponent.rotationAngle != 0) {
            batch.draw(graphicsComponent.region[index], physicsComponent.position.x,
                    physicsComponent.position.y,
                    graphicsComponent.width / 2f,
                    +graphicsComponent.height / 2f,
                    graphicsComponent.width, graphicsComponent.height,
                    1, 1, physicsComponent.rotationAngle);
        } else {
            batch.draw(graphicsComponent.region[index], physicsComponent.position.x,
                    physicsComponent.position.y, graphicsComponent.width, graphicsComponent.height);
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
}