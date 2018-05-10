package com.actionteam.geometryadventures.systems;

import com.actionteam.geometryadventures.components.CollisionComponent;
import com.actionteam.geometryadventures.components.Components;
import com.actionteam.geometryadventures.components.PhysicsComponent;
import com.actionteam.geometryadventures.components.PortalComponent;
import com.actionteam.geometryadventures.ecs.ECSEventListener;
import com.actionteam.geometryadventures.ecs.System;
import com.actionteam.geometryadventures.events.ECSEvents;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.Iterator;

/**
 * Created by theartful on 3/27/18.
 * edited by Omnia on 3/29/18
 * edited by Omnia on 5/2/18
 */

public class PhysicsSystem extends System implements ECSEventListener {

    private boolean didCollide;
    private Vector3 movedToAPortal;

    public PhysicsSystem() {
        super(Components.PHYSICS_COMPONENT_CODE);
        movedToAPortal = null;
    }

    @Override
    protected void ecsManagerAttached() {
        ecsManager.subscribe(ECSEvents.COLLISION_EVENT, this);
        ecsManager.subscribe(ECSEvents.MOVED_TO_A_PORTAL_EVENT, this);
        ecsManager.subscribe(ECSEvents.FREEZE_EVENT, this);
        ecsManager.subscribe(ECSEvents.UNFREEZE_EVENT, this);
    }

    /**
     * Iterates over all entities and updates their positions and velocities
     *
     * @param dt delta time from last update
     */
    @Override
    public void update(float dt) {
        for (int entity : entities) {
            PhysicsComponent physicsComponent = (PhysicsComponent) ecsManager.getComponent(entity,
                    Components.PHYSICS_COMPONENT_CODE);
            // if entity is freezed, then do nothing
            if (!physicsComponent.isFreezed)
                update(physicsComponent, dt, entity);
        }
    }

    /**
     * Updates the position of physics component
     *
     * @param physicsComponent physics component to be update
     * @param dt               delta time from last update
     * @param entityID         entity id of the owner of the physics component
     */
    private void update(PhysicsComponent physicsComponent, float dt, int entityID) {
        // update velocity
        physicsComponent.velocity.add(physicsComponent.acceleration.scl(dt))
                .add(physicsComponent.angularAcceleration.scl(dt));

        if (physicsComponent.velocity.isZero())
            return;

        // handle angular acceleration
        if (!physicsComponent.angularAcceleration.isZero()) {
            // relative position vector
            Vector2 relPos = physicsComponent.centerOfRotation.cpy().sub(physicsComponent.position);
            physicsComponent.angularAcceleration = relPos.limit(1.0f).scl(
                    physicsComponent.velocity.len2() / relPos.len());
        }

        // position of the entity at previous frame
        float beginX = physicsComponent.position.x;
        float beginY = physicsComponent.position.y;
        // next position of the entity
        float endX = beginX + dt * physicsComponent.velocity.x;
        float endY = beginY + dt * physicsComponent.velocity.y;

        CollisionComponent col = (CollisionComponent) ecsManager.
                getComponent(entityID, Components.COLLISION_COMPONENT_CODE);

        // if there is no collision component, then simply update the position
        if (col == null) {
            physicsComponent.position.set(endX, endY);
            return;
        }

        // else check for collision
        didCollide = false;
        // first move in x direction
        ecsManager.fireEvent(ECSEvents.collidableMovedEvent(beginX, beginY, endX, beginY, entityID));
        if (!didCollide) {
            beginX = endX;
        } else {
            endX = beginX;
        }
        // then move in y direction
        ecsManager.fireEvent(ECSEvents.collidableMovedEvent(beginX, beginY, endX, endY, entityID));
        if (didCollide) {
            endY = beginY;
        }

        // handle portals
        if (movedToAPortal != null) {
            PhysicsComponent pc = (PhysicsComponent) ecsManager.getComponent((int) movedToAPortal.z,
                    Components.PHYSICS_COMPONENT_CODE);
            pc.position.x = movedToAPortal.x;
            pc.position.y = movedToAPortal.y;
            movedToAPortal = null;
        } else {
            physicsComponent.position.x = endX;
            physicsComponent.position.y = endY;
        }
    }

    @Override
    public boolean handle(int eventCode, Object message) {
        switch (eventCode) {
            case ECSEvents.COLLISION_EVENT:
                didCollide = (Boolean) message;
                break;
            case ECSEvents.MOVED_TO_A_PORTAL_EVENT:
                movedToAPortal = (Vector3) message;
                break;
            case ECSEvents.FREEZE_EVENT:
                PhysicsComponent pc = (PhysicsComponent) ecsManager.getComponent((Integer) message,
                        Components.PHYSICS_COMPONENT_CODE);
                pc.isFreezed = true;
                break;
            case ECSEvents.UNFREEZE_EVENT:
                PhysicsComponent pc1 = (PhysicsComponent) ecsManager.getComponent((Integer) message,
                        Components.PHYSICS_COMPONENT_CODE);
                pc1.isFreezed = false;
                break;
        }
        return false;
    }
}
