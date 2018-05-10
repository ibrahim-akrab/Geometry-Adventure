package com.actionteam.geometryadventures.systems;

import com.actionteam.geometryadventures.components.Components;
import com.actionteam.geometryadventures.components.ControlComponent;
import com.actionteam.geometryadventures.components.PhysicsComponent;
import com.actionteam.geometryadventures.ecs.ECSEvent;
import com.actionteam.geometryadventures.ecs.ECSEventListener;
import com.actionteam.geometryadventures.ecs.System;
import com.actionteam.geometryadventures.events.ECSEvents;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * This system is the interface between the user and the game character
 * It enables the player to take whatever action avaliable, which currently includes:
 * - Moving the character
 * - Attacking
 * Screen is divided into two areas, left side and right side
 * Dragging on the left side causes the character to move, and dragging on the right side
 * causes the character to attack
 * <p>
 * Created by theartful on 3/27/18.
 */

public class ControlSystem extends System implements InputProcessor, ECSEventListener {

    // player's information
    private PhysicsComponent physicsComponent;
    private ControlComponent controlComponent;

    // for multitouch
    private int leftPointer;
    private int rightPointer;
    private int entityId;

    // are w,a,s,d keys down
    // used for keyboard
    private boolean wDown, aDown, sDown, dDown;

    public ControlSystem() {
        super(Components.PHYSICS_COMPONENT_CODE, Components.CONTROL_COMPONENT_CODE,
                Components.GRAPHICS_COMPONENT_CODE);
        leftPointer = -1;
        rightPointer = -1;
        wDown = false;
        aDown = false;
        sDown = false;
        dDown = false;
    }

    @Override
    protected void ecsManagerAttached() {
        // subscribe to events
        ecsManager.subscribe(ECSEvents.RESIZE_EVENT, this);
    }

    /**
     * Saves player's information for further usage
     */
    @Override
    protected void entityAdded(int entityId) {
        physicsComponent = (PhysicsComponent)
                ecsManager.getComponent(entityId, Components.PHYSICS_COMPONENT_CODE);
        controlComponent = (ControlComponent)
                ecsManager.getComponent(entityId, Components.CONTROL_COMPONENT_CODE);
        this.entityId = entityId;
    }

    /**
     * Fires an event with the position of the player
     * This event is used by enemies to know the position of the player
     */
    @Override
    public void update(float dt) {
        ecsManager.fireEvent(ECSEvents.playerMovedEvent(physicsComponent.position.x,
                physicsComponent.position.y));
        if (controlComponent.isRightTouchDown)
            rightTouchDragged((int) controlComponent.rightX, (int) controlComponent.rightY);
    }

    /**
     * Stores the data of initial touch in the control component of the player
     */
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (screenX < Gdx.graphics.getWidth() / 2) {
            handleLeftTouch(screenX, screenY, pointer);
            Gdx.app.log("ControlSystem", "left touch");
        } else {
            handleRightTouch(screenX, screenY, pointer);
            Gdx.app.log("ControlSystem", "right touch");
        }
        return true;
    }

    /**
     * Updates control component of the touch on the right side
     * @param screenX x coordinate of the screen
     * @param screenY y coordinate of the screen
     * @param pointer touch pointer
     */
    private void handleRightTouch(int screenX, int screenY, int pointer) {
        if (controlComponent.isRightTouchDown) return;
        controlComponent.isRightTouchDown = true;
        controlComponent.rightInitialX = screenX;
        controlComponent.rightInitialY = screenY;
        controlComponent.rightX = screenX;
        controlComponent.rightY = screenY;
        rightPointer = pointer;
    }

    /**
     * Updates control component of the touch on the left side
     * @param screenX x coordinate of the screen
     * @param screenY y coordinate of the screen
     * @param pointer touch pointer
     */
    private void handleLeftTouch(int screenX, int screenY, int pointer) {
        if (controlComponent.isLeftTouchDown) return;
        controlComponent.isLeftTouchDown = true;
        controlComponent.leftInitialX = screenX;
        controlComponent.leftInitialY = screenY;
        controlComponent.leftX = screenX;
        controlComponent.leftY = screenY;
        leftPointer = pointer;
    }

    /**
     * Stops the player from moving if the movement controlling finger caused the touchUp
     */
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (pointer == leftPointer) {
            controlComponent.isLeftTouchDown = false;
            physicsComponent.velocity.x = 0;
            physicsComponent.velocity.y = 0;
            leftPointer = -1;
        } else if (pointer == rightPointer) {
            controlComponent.isRightTouchDown = false;
            rightPointer = -1;
        }
        return true;
    }

    /**
     * If touch dragged from the left side, the method moves the player with a velocity proportional
     * to the distance dragged
     * If touch dragged from the right side with appropriate distance, it initiates attack
     */
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (pointer == leftPointer) {
            leftTouchDragged(screenX, screenY);
        } else {
            rightTouchDragged(screenX, screenY);
        }
        return true;
    }

    /**
     * Moves the player with a speed proportional to the distance dragged
     */
    private void leftTouchDragged(int screenX, int screenY) {
        // update data
        controlComponent.leftX = screenX;
        controlComponent.leftY = screenY;
        float deltaX = screenX - controlComponent.leftInitialX;
        float deltaY = screenY - controlComponent.leftInitialY;
        // speed is calculated proportionally to the distance dragged
        float speed = Math.min((deltaX * deltaX + deltaY * deltaY) /
                (controlComponent.leftBigCircleRadius * controlComponent.leftBigCircleRadius), 1) *
                controlComponent.maximumSpeed;
        // calculate angle and set the velocity
        float angle = (float) Math.atan2(deltaY, deltaX);
        physicsComponent.velocity.x = (float) (speed * Math.cos(angle));
        physicsComponent.velocity.y = (float) (-speed * Math.sin(angle));
        physicsComponent.rotationAngle = physicsComponent.velocity.angle();
    }

    private void rightTouchDragged(int screenX, int screenY) {
        // update data
        controlComponent.rightX = screenX;
        controlComponent.rightY = screenY;
        float deltaX = screenX - controlComponent.rightInitialX;
        float deltaY = screenY - controlComponent.rightInitialY;
        float angle = (float) Math.atan2(deltaY, deltaX);
        // checks if the distance dragged is enough to initiate attack
        if (deltaX * deltaX + deltaY * deltaY >=
                controlComponent.rightBigCircleRadius * controlComponent.rightBigCircleRadius) {
            // check that the player has a weapon
            if (ecsManager.entityHasComponent(entityId, Components.WEAPON_COMPONENT_CODE)) {
                Vector2 position = physicsComponent.position;
                ECSEvent castEvent = ECSEvents.castEvent
                        (position.x, position.y, angle, entityId, true);
                ecsManager.fireEvent(castEvent);
            }
        }
        // update rotation angle
        physicsComponent.rotationAngle = -angle * MathUtils.radiansToDegrees;
        if (physicsComponent.rotationAngle < 0) physicsComponent.rotationAngle += 360;
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
        if (controlComponent != null) {
            controlComponent.leftBigCircleRadius =
                    (float) (0.04 * Math.sqrt(width * width + height * height));
            controlComponent.rightBigCircleRadius =
                    (float) (0.08 * Math.sqrt(width * width + height * height));
        }
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
        boolean flag = false;
        switch (keycode) {
            case Input.Keys.W:
                wDown = true;
                flag = true;
                break;
            case Input.Keys.A:
                aDown = true;
                flag = true;
                break;
            case Input.Keys.S:
                sDown = true;
                flag = true;
                break;
            case Input.Keys.D:
                dDown = true;
                flag = true;
                break;
        }
        if (!flag) return false;
        updateMovementDirection();
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        boolean flag = false;
        switch (keycode) {
            case Input.Keys.W:
                wDown = false;
                flag = true;
                break;
            case Input.Keys.A:
                aDown = false;
                flag = true;
                break;
            case Input.Keys.S:
                sDown = false;
                flag = true;
                break;
            case Input.Keys.D:
                dDown = false;
                flag = true;
                break;
        }
        if (!flag) return false;
        updateMovementDirection();
        return false;
    }

    /**
     * update player speed based on pressed keys
     */
    private void updateMovementDirection() {
        if (!wDown && !dDown && !aDown && !sDown) {
            physicsComponent.velocity.setZero();
            return;
        }
        float angle = 0;
        if (wDown)
            angle = (float) Math.PI / 2;
        if (sDown)
            angle = (angle == 0) ? (float) Math.PI * 3 / 2 : (angle + 3 * (float) Math.PI / 2) / 2;
        if (dDown)
            angle /= 2;
        if (aDown)
            angle = angle == 0 ? (float) Math.PI : (angle + (float) Math.PI) / 2;
        if (sDown && dDown) angle = 7 * (float) Math.PI / 4;
        physicsComponent.velocity.x = (float) (controlComponent.maximumSpeed * Math.cos(angle));
        physicsComponent.velocity.y = (float) (controlComponent.maximumSpeed * Math.sin(angle));
        physicsComponent.rotationAngle = (float) Math.toDegrees(angle);
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }
}
