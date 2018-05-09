package com.actionteam.geometryadventures.systems;

import com.actionteam.geometryadventures.components.Components;
import com.actionteam.geometryadventures.components.LifetimeComponent;
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
                    if (coin) createCoin();
                }
                ecsManager.removeEntity(entity);
            }
        }
    }

    private void createCoin() {
        int entity = ecsManager.createEntity();

    }
}
