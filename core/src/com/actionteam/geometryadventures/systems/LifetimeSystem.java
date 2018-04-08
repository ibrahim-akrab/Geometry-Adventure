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
    public LifetimeSystem(){ super((Components.LIFETIME_COMPONENT_CODE));}

    @Override
    public void update(float dt) {
        List<Integer> entitiesToBeRemoved = new ArrayList<Integer>();
        for (int entity :
                entities) {
//            Gdx.app.log("Lifetime system", "updated");
            LifetimeComponent lifetimeComponent = (LifetimeComponent)
                    ecsManager.getComponent(entity, Components.LIFETIME_COMPONENT_CODE);
            if (TimeUtils.timeSinceMillis(lifetimeComponent.timeOfCreation)
                    > lifetimeComponent.lifetime){
//                boolean returnValue = ecsManager.removeEntity(entity);
                entitiesToBeRemoved.add(entity);
            }
        }
        for (Integer entityId :
                entitiesToBeRemoved) {
//            Gdx.app.log("lifetime system", "removing entity");
            boolean returnValue = ecsManager.removeEntity(entityId);
//            if (returnValue) Gdx.app.log("lifetime system", "removed entity successfully");
        }
        // TODO ask whether the list of entities IDs needs to be deleted or set to null to be deleted
    }
}
