package com.actionteam.geometryadventures.systems;

import com.actionteam.geometryadventures.components.Components;
import com.actionteam.geometryadventures.components.ControlComponent;
import com.actionteam.geometryadventures.components.GraphicsComponent;
import com.actionteam.geometryadventures.components.PhysicsComponent;
import com.actionteam.geometryadventures.ecs.Component;
import com.actionteam.geometryadventures.ecs.ECSEventListener;
import com.actionteam.geometryadventures.ecs.System;
import com.actionteam.geometryadventures.events.ECSEvents;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by theartful on 3/27/18.
 */

public class ControlSystem extends System implements InputProcessor, ECSEventListener {

    private GraphicsComponent graphicsComponent;
    private PhysicsComponent physicsComponent;
    private ControlComponent controlComponent;

    private int leftPointer;
    private int rightPointer;
    private int entityId;

    private float prevMouseX;
    private float prevMouseY;

    public ControlSystem() {
        super(Components.PHYSICS_COMPONENT_CODE, Components.CONTROL_COMPONENT_CODE,
                Components.GRAPHICS_COMPONENT_CODE);
        leftPointer = -1;
        rightPointer = -1;
        prevMouseX = 0;
        prevMouseY = 0;
    }

    @Override
    protected void ecsManagerAttached() {
        ecsManager.subscribe(ECSEvents.RESIZE_EVENT, this);
    }

    @Override
    protected void entityAdded(int entityId) {
        graphicsComponent = (GraphicsComponent)
                ecsManager.getComponent(entityId, Components.GRAPHICS_COMPONENT_CODE);
        physicsComponent = (PhysicsComponent)
                ecsManager.getComponent(entityId, Components.PHYSICS_COMPONENT_CODE);
        controlComponent = (ControlComponent)
                ecsManager.getComponent(entityId, Components.CONTROL_COMPONENT_CODE);
        controlComponent.leftBigCircleRadius = (float) (0.04 * Math.sqrt(Gdx.graphics.getWidth() *
                Gdx.graphics.getWidth() + Gdx.graphics.getHeight() * Gdx.graphics.getHeight()));
        controlComponent.rightBigCircleRadius = (float) (0.18 * Math.sqrt(Gdx.graphics.getWidth() *
                Gdx.graphics.getWidth() + Gdx.graphics.getHeight() * Gdx.graphics.getHeight()));
        controlComponent.maximumSpeed = 3.5f;
        this.entityId = entityId;
    }

    @Override
    public void update(float dt) {
        ecsManager.fireEvent(ECSEvents.playerMovedEvent(physicsComponent.position.x,
                physicsComponent.position.y));
        if (controlComponent.isRightTouchDown)
            rightTouchDragged((int) controlComponent.rightX, (int) controlComponent.rightY);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(screenX < Gdx.graphics.getWidth() / 2) {
            Gdx.app.log("ControlSystem", "left touch");
            if(controlComponent.isLeftTouchDown) return true;
            controlComponent.isLeftTouchDown = true;
            controlComponent.leftInitialX = screenX;
            controlComponent.leftInitialY = screenY;
            controlComponent.leftX = screenX;
            controlComponent.leftY = screenY;
            leftPointer = pointer;
        } else {
            Gdx.app.log("ControlSystem", "right touch");
            if(controlComponent.isRightTouchDown) return true;
            controlComponent.isRightTouchDown = true;
            controlComponent.rightInitialX = screenX;
            controlComponent.rightInitialY = screenY;
            controlComponent.rightX = screenX;
            controlComponent.rightY = screenY;
            rightPointer = pointer;
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(pointer == leftPointer) {
            controlComponent.isLeftTouchDown = false;
            physicsComponent.velocity.x = 0;
            physicsComponent.velocity.y = 0;
            leftPointer = -1;
        }
        else if(pointer == rightPointer) {
            controlComponent.isRightTouchDown = false;
            rightPointer = -1;
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(pointer == leftPointer){
            leftTouchDragged(screenX, screenY);
        } else {
            rightTouchDragged(screenX, screenY);
        }
        return true;
    }

    private void leftTouchDragged(int screenX, int screenY) {
        controlComponent.leftX = screenX;
        controlComponent.leftY = screenY;
        float deltaX = screenX - controlComponent.leftInitialX;
        float deltaY = screenY - controlComponent.leftInitialY;
        float speed = Math.min((deltaX * deltaX + deltaY * deltaY) /
                (controlComponent.leftBigCircleRadius * controlComponent.leftBigCircleRadius), 1 ) *
                controlComponent.maximumSpeed;
        float angle = (float)Math.atan2(deltaY, deltaX);
        physicsComponent.velocity.x = (float) (speed * Math.cos(angle));
        physicsComponent.velocity.y = (float) (-speed * Math.sin(angle));

        if (!controlComponent.isRightTouchDown){
            physicsComponent.rotationAngle = 360 - (float)Math.toDegrees(angle);
        }
    }

    private void rightTouchDragged(int screenX, int screenY) {
        controlComponent.rightX = screenX;
        controlComponent.rightY = screenY;
        float deltaX = screenX - controlComponent.rightInitialX;
        float deltaY = screenY - controlComponent.rightInitialY;
        float angle = (float)Math.atan2(deltaY, deltaX);
        physicsComponent.rotationAngle = 360 - (float)Math.toDegrees(angle);
        if (deltaX * deltaX + deltaY * deltaY >=
                controlComponent.rightBigCircleRadius * controlComponent.rightBigCircleRadius) {
            Component weaponComponent = ecsManager.getComponent(entityId, Components.WEAPON_COMPONENT_CODE);
            if (weaponComponent != null) {
                // Gdx.app.log("weapon", "fired");
                Vector2 position = physicsComponent.position;
                ecsManager.fireEvent(ECSEvents.attackEvent
                        (position.x, position.y, angle, weaponComponent.getId(), entityId));
            }
        }
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) { return false; }

    @Override
    public boolean scrolled(int amount) { return false; }


    @Override
    public boolean keyDown(int keycode) { return false; }

    @Override
    public boolean keyUp(int keycode) { return false; }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean handle(int eventCode, Object message) {
        switch (eventCode) {
            case ECSEvents.RESIZE_EVENT:
                int[] size = (int[]) message;
                resize(size[0], size[1]);
                break;
        }
        return false;
    }

    private void resize(int width, int height) {
        if(controlComponent != null) {
            controlComponent.leftBigCircleRadius =
                    (float) (0.04 * Math.sqrt(width * width + height * height));
            controlComponent.rightBigCircleRadius =
                    (float) (0.08 * Math.sqrt(width * width + height * height));
        }
    }
}
