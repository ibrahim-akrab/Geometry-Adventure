package com.actionteam.geometryadventures.systems;

import com.actionteam.geometryadventures.GameUtils;
import com.actionteam.geometryadventures.components.Components;
import com.actionteam.geometryadventures.components.LifetimeComponent;
import com.actionteam.geometryadventures.components.PhysicsComponent;
import com.actionteam.geometryadventures.ecs.Component;
import com.actionteam.geometryadventures.ecs.System;

import java.util.Random;

/**
 * Created by Ibrahim M. Akrab on 4/3/18.
 * ibrahim.m.akrab@gmail.com
 */
public class LifetimeSystem extends System {

    private Random rand;

    public LifetimeSystem() {
        super((Components.LIFETIME_COMPONENT_CODE));
        rand = new Random();
    }

    @Override
    protected void ecsManagerAttached() {

    }

    /**
     * delete entities that needs to be deleted (its lifetime is over)
     *
     * @param dt
     */
    @Override
    public void update(float dt) {
        for (int entity : entities) {
            LifetimeComponent lifetimeComponent = (LifetimeComponent)
                    ecsManager.getComponent(entity, Components.LIFETIME_COMPONENT_CODE);
            if (ClockSystem.timeSinceMillis(lifetimeComponent.timeOfCreation)
                    > lifetimeComponent.lifetime) {
                if (ecsManager.entityHasComponent(entity, Components.ENEMY_COMPONENT_CODE)) {
                    boolean coin = rand.nextBoolean();
                    if (coin) {
                        PhysicsComponent physicsComponent = (PhysicsComponent)
                                ecsManager.getComponent(entity, Components.PHYSICS_COMPONENT_CODE);
                        GameUtils.createStandardCoin(physicsComponent.position.x,
                                physicsComponent.position.y);
                    }
                }
                ecsManager.removeEntity(entity);
            }
        }
    }
}
