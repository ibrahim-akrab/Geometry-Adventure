package com.actionteam.geometryadventures.systems;

import com.actionteam.geometryadventures.components.CollisionComponent;
import com.actionteam.geometryadventures.components.Components;
import com.actionteam.geometryadventures.components.PhysicsComponent;
import com.actionteam.geometryadventures.components.PortalComponent;
import com.actionteam.geometryadventures.ecs.ECSEventListener;
import com.actionteam.geometryadventures.ecs.System;
import com.actionteam.geometryadventures.events.ECSEvents;
import com.badlogic.gdx.math.Vector2;


/**
 * Created by theartful on 3/27/18.
 * edited by Omnia on 3/29/18
 * edited by Omnia on 5/2/18
 */

public class PhysicsSystem extends System implements ECSEventListener {

    private boolean didCollide;

    public PhysicsSystem() {
        super(Components.PHYSICS_COMPONENT_CODE);
    }

    @Override
    protected void ecsManagerAttached() {
        ecsManager.subscribe(ECSEvents.COLLISION_EVENT, this);
        ecsManager.subscribe(ECSEvents.MOVED_TO_A_PORTAL_EVENT,this);
    }

    @Override
    public void update(float dt) {
            for (int entity : entities) {
                PhysicsComponent physicsComponent = (PhysicsComponent) ecsManager.getComponent(entity,
                        Components.PHYSICS_COMPONENT_CODE);
                update(physicsComponent, dt, entity);
            }
    }

    private void update(PhysicsComponent physicsComponent, float dt, int entityID) {
        physicsComponent.velocity
                .add(physicsComponent.acceleration.scl(dt))
                .add(physicsComponent.angularAcceleration.scl(dt));
        if (physicsComponent.velocity.isZero())
            return;

        float beginX = physicsComponent.position.x;
        float beginY = physicsComponent.position.y;
        float endX = beginX + dt * physicsComponent.velocity.x;
        float endY = beginY + dt * physicsComponent.velocity.y;

        CollisionComponent col = (CollisionComponent) ecsManager.
                    getComponent(entityID, Components.COLLISION_COMPONENT_CODE);
        PortalComponent por =  (PortalComponent) ecsManager.
                getComponent(entityID,Components.PORTAL_COMPONENT_CODE);

        didCollide = false;
        if (col != null) {
            ecsManager.fireEvent(ECSEvents.collidableMovedEvent
                        (beginX, beginY, endX, beginY, entityID));
            if (!didCollide) {
                beginX = endX;
            } else {
//                Gdx.app.log("PhysicsSystem", "(" + physicsComponent.velocity.x + ", "
//                            + physicsComponent.velocity.y + ").");
                endX = beginX;
            }
            ecsManager.fireEvent(ECSEvents.collidableMovedEvent(beginX, beginY, endX, endY, entityID));
            if (didCollide) {
                endY = beginY;
            }
            physicsComponent.position.x = endX;
            physicsComponent.position.y = endY;
        }
        else if( por != null) {
            physicsComponent.position.x = por.position.x;
            physicsComponent.position.y = por.position.y;
        }
        else {
            physicsComponent.position.x = endX;
            physicsComponent.position.y = endY;
        }
        if(!physicsComponent.angularAcceleration.isZero()) {
            Vector2 relativePositionVector = physicsComponent.centerOfRotation.cpy().sub(physicsComponent.position);
            physicsComponent.angularAcceleration =
                    relativePositionVector.limit(1.0f).scl(
                            physicsComponent.velocity.len2()/
                                    relativePositionVector.len()
                    );
        }



    }

    @Override
    public boolean handle(int eventCode, Object message) {
        switch (eventCode) {
            case ECSEvents.COLLISION_EVENT:
                didCollide = (Boolean) message;
        }
        return false;
    }
}
