package com.actionteam.geometryadventures.systems;

import com.actionteam.geometryadventures.components.Components;
import com.actionteam.geometryadventures.components.PhysicsComponent;
import com.actionteam.geometryadventures.ecs.System;

/**
 * Created by theartful on 3/27/18.
 */

public class PhysicsSystem extends System {

    public PhysicsSystem(){
        super(Components.PHYSICS_COMPONENT_CODE);
    }

    @Override
    protected void ecsManagerAttached() {

    }

    @Override
    public void update(float dt) {
        for(int entity : entities){
            PhysicsComponent physicsComponent = (PhysicsComponent)ecsManager.getComponent(entity,
                    Components.PHYSICS_COMPONENT_CODE);
            update(physicsComponent, dt);
        }
    }

    private void update(PhysicsComponent physicsComponent, float dt) {
        physicsComponent.velocity.x += dt * physicsComponent.acceleration.x;
        physicsComponent.velocity.y += dt * physicsComponent.acceleration.y;
        physicsComponent.position.x += dt * physicsComponent.velocity.x;
        physicsComponent.position.y += dt * physicsComponent.velocity.y;
    }
}
