package com.actionteam.geometryadventures.systems;

import com.actionteam.geometryadventures.components.CollectibleComponent;
import com.actionteam.geometryadventures.components.CollectorComponent;
import com.actionteam.geometryadventures.components.Components;
import com.actionteam.geometryadventures.ecs.ECSEventListener;
import com.actionteam.geometryadventures.ecs.System;
import com.actionteam.geometryadventures.events.ECSEvents;
import com.badlogic.gdx.Gdx;

/**
 * Created by Ibrahim M. Akrab on 5/3/18.
 * ibrahim.m.akrab@gmail.com
 */
public class CollectionSystem extends System implements ECSEventListener{
    @Override
    public boolean handle(int eventCode, Object message) {
        switch (eventCode){
            case ECSEvents.COLLECTIBLE_COLLIDED_EVENT:
                int[] collectibleData = (int[]) message;
                int collectibleId = collectibleData[0];
                int collectorId = collectibleData[1];
                collect(collectibleId, collectorId);
                removeCollectible(collectibleId);
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void ecsManagerAttached() {
        ecsManager.subscribe(ECSEvents.COLLECTIBLE_COLLIDED_EVENT, this);
    }

    @Override
    public void update(float dt) {

    }

    private void collect(int collectibleId, int collectorId){
        CollectibleComponent collectibleComponent = (CollectibleComponent)
                ecsManager.getComponent(collectibleId, Components.COLLECTIBLE_COMPONENT_CODE);
        CollectorComponent collectorComponent = (CollectorComponent)
                ecsManager.getComponent(collectorId, Components.COLLECTOR_COMPONENT_CODE);
        collectorComponent.coins += collectibleComponent.coin;
        Gdx.app.log("Coins", String.valueOf(collectorComponent.coins));
    }

    private void removeCollectible(int collectibleId){
        ecsManager.removeEntity(collectibleId);
    }
}
