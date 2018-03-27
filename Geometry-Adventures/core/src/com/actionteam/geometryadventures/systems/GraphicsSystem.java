package com.actionteam.geometryadventures.systems;

import com.actionteam.geometryadventures.components.Components;
import com.actionteam.geometryadventures.components.GraphicsComponent;
import com.actionteam.geometryadventures.components.PhysicsComponent;
import com.actionteam.geometryadventures.ecs.ECSEventListener;
import com.actionteam.geometryadventures.ecs.System;
import com.actionteam.geometryadventures.events.ECSEvents;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.viewport.FillViewport;

/**
 * Created by theartful on 3/27/18.
 */

public class GraphicsSystem extends System implements ECSEventListener {

    private FillViewport viewport;
    private SpriteBatch batch;
    private TextureAtlas textureAtlas;

    public GraphicsSystem() {
        super(Components.GRAPHICS_COMPONENT_CODE, Components.PHYSICS_COMPONENT_CODE);
        viewport = new FillViewport(15,15);
        batch = new SpriteBatch();
        textureAtlas = new TextureAtlas(Gdx.files.internal("textureatlas/textures.atlas"));
    }

    @Override
    protected void ecsManagerAttached() {
        // subscribe to events
        ecsManager.subscribe(ECSEvents.RESIZE_EVENT_CODE, this);
    }

    @Override
    public void update(float dt) {
        viewport.apply();
        batch.begin();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        for(int entity : entities){
            GraphicsComponent graphicsComponent = (GraphicsComponent)ecsManager.getComponent(entity,
                    Components.GRAPHICS_COMPONENT_CODE);
            PhysicsComponent physicsComponent = (PhysicsComponent)ecsManager.getComponent(entity,
                    Components.PHYSICS_COMPONENT_CODE);
            draw(graphicsComponent, physicsComponent);
        }
        batch.end();
    }

    private void draw(GraphicsComponent graphicsComponent, PhysicsComponent physicsComponent) {
        Gdx.app.log("GraphcisSystem", "Drawing " + graphicsComponent);
        batch.draw(textureAtlas.findRegion(graphicsComponent.textureName,
                graphicsComponent.textureIndex), physicsComponent.position.x,
                physicsComponent.position.y, graphicsComponent.width, graphicsComponent.height);
    }

    @Override
    public boolean handle(int eventCode, Object message) {
        switch(eventCode){
            case ECSEvents.RESIZE_EVENT_CODE:
                int[] size = (int[])message;
                resize(size[0], size[1]);
                break;
        }
        return false;
    }

    private void resize(int width, int height) {
        viewport.update(width, height);
    }
}
