package com.actionteam.geometryadventures.systems;

import com.actionteam.geometryadventures.components.CollisionComponent;
import com.actionteam.geometryadventures.components.Components;
import com.actionteam.geometryadventures.components.PhysicsComponent;
import com.actionteam.geometryadventures.ecs.ECSEvent;
import com.actionteam.geometryadventures.ecs.ECSEventListener;
import com.actionteam.geometryadventures.ecs.System;
import com.actionteam.geometryadventures.events.ECSEvents;
import com.badlogic.gdx.Gdx;

/**
 * Created by theartful on 3/27/18.
 * edited by Omnia on 3/29/18
 */

public class PhysicsSystem extends System implements ECSEventListener {

    private boolean didCollide;

    public PhysicsSystem() {
        super(Components.PHYSICS_COMPONENT_CODE);
    }

    @Override
    protected void ecsManagerAttached() {
        ecsManager.subscribe(ECSEvents.COLLISION_EVENT, this);
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

        physicsComponent.velocity.x += dt * physicsComponent.acceleration.x;
        physicsComponent.velocity.y += dt * physicsComponent.acceleration.y;

        float beginX = physicsComponent.position.x;
        float beginY = physicsComponent.position.y;
        float endX = beginX + dt * physicsComponent.velocity.x;
        float endY = beginY + dt * physicsComponent.velocity.y;

        CollisionComponent col = (CollisionComponent) ecsManager.
                getComponent(entityID, Components.COLLISION_COMPONENT_CODE);

        didCollide = false;
        if (col != null) {
            ecsManager.fireEvent(ECSEvents.collidableMovedEvent
                    (beginX, beginY, endX, beginY, entityID));
            if (!didCollide) {
                beginX = endX;
            } else {
                endX = beginX;
            }
            ecsManager.fireEvent(ECSEvents.collidableMovedEvent(beginX, beginY, endX, endY, entityID));
            if (didCollide) {
                endY = beginY;
            }
        }
        physicsComponent.position.x = endX;
        physicsComponent.position.y = endY;
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
