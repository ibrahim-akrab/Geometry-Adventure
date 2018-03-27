package com.actionteam.geometryadventures.systems;

import com.actionteam.geometryadventures.components.Components;
import com.actionteam.geometryadventures.components.GraphicsComponent;
import com.actionteam.geometryadventures.components.PhysicsComponent;
import com.actionteam.geometryadventures.ecs.System;
import com.badlogic.gdx.InputProcessor;

/**
 * Created by theartful on 3/27/18.
 */

public class ControlSystem extends System implements InputProcessor {

    private float initialX;
    private float initialY;
    private boolean isTouchDown;
    private int speed;

    private GraphicsComponent graphicsComponent;
    private PhysicsComponent physicsComponent;

    public ControlSystem() {
        super(Components.PHYSICS_COMPONENT_CODE, Components.CONTROL_COMPONENT_CODE,
                Components.GRAPHICS_COMPONENT_CODE);
        isTouchDown = false;
        speed = 3;
    }

    @Override
    protected void entityAdded(int entityId) {
        graphicsComponent = (GraphicsComponent)
                ecsManager.getComponent(entityId, Components.GRAPHICS_COMPONENT_CODE);
        physicsComponent = (PhysicsComponent)
                ecsManager.getComponent(entityId, Components.PHYSICS_COMPONENT_CODE);
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        isTouchDown = true;
        initialX = screenX;
        initialY = screenY;
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        isTouchDown = false;
        physicsComponent.velocity.x = 0;
        physicsComponent.velocity.y = 0;
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(!isTouchDown) return false;
        float angle = (float)Math.atan2(screenY - initialY, screenX - initialX);
        physicsComponent.velocity.x = (float) (speed * Math.cos(angle));
        physicsComponent.velocity.y = (float) (-speed * Math.sin(angle));
        graphicsComponent.rotationAngle = 360 - (float)Math.toDegrees(angle);
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }


    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

}
