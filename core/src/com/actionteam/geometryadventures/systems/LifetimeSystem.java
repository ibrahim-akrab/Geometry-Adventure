package com.actionteam.geometryadventures.systems;

import com.actionteam.geometryadventures.components.Components;
import com.actionteam.geometryadventures.components.LifetimeComponent;
import com.actionteam.geometryadventures.ecs.Component;
import com.actionteam.geometryadventures.ecs.System;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ibrahim M. Akrab on 4/3/18.
 * ibrahim.m.akrab@gmail.com
 */
public class LifetimeSystem extends System {
    public LifetimeSystem() {
        super((Components.LIFETIME_COMPONENT_CODE));
    }

    /**
     * delete entities that needs to be deleted (its lifetime is over)
     * @param dt
     */
    @Override
    public void update(float dt) {
        for (int entity :
                entities) {
            LifetimeComponent lifetimeComponent = (LifetimeComponent)
                    ecsManager.getComponent(entity, Components.LIFETIME_COMPONENT_CODE);
            if (TimeUtils.timeSinceMillis(lifetimeComponent.timeOfCreation)
                    > lifetimeComponent.lifetime) {
                ecsManager.removeEntity(entity);
            }
        }
    }
}
