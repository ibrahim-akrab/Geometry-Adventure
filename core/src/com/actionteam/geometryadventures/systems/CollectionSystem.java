package com.actionteam.geometryadventures.systems;

import com.actionteam.geometryadventures.components.CollectibleComponent;
import com.actionteam.geometryadventures.components.CollectorComponent;
import com.actionteam.geometryadventures.components.Components;
import com.actionteam.geometryadventures.ecs.ECSEventListener;
import com.actionteam.geometryadventures.ecs.Entity;
import com.actionteam.geometryadventures.ecs.System;
import com.actionteam.geometryadventures.events.ECSEvents;
import com.badlogic.gdx.Gdx;

/**
 * Created by Ibrahim M. Akrab on 5/3/18.
 * ibrahim.m.akrab@gmail.com
 */
public class CollectionSystem extends System implements ECSEventListener{

    public CollectionSystem(){ super(Components.COLLECTIBLE_COMPONENT_CODE);}

    /**
     * handles when an event it is subscribed to is fired
     * @param eventCode determines event's type
     * @param message event's data
     * @return true of event has been handled
     */
    @Override
    public boolean handle(int eventCode, Object message) {
        switch (eventCode){
            case ECSEvents.COLLECTIBLE_COLLIDED_EVENT:
                int[] collectibleData = (int[]) message;
                int collectibleId = collectibleData[0];
                int collectorId = collectibleData[1];
                if (collect(collectibleId, collectorId)){
                    fireEvents(collectibleId, collectorId);
                    removeCollectible(collectibleId);
                    return true;
                }
                return false;
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

    /**
     * handles giving collectible to the player
     * @param collectibleId
     * @param collectorId
     * @return true if it was collected, false otherwise
     */
    private boolean collect(int collectibleId, int collectorId){
        if (!ecsManager.entityHasComponent(collectorId, Components.COLLECTOR_COMPONENT_CODE)){
            return false;
        }
        CollectibleComponent collectibleComponent = (CollectibleComponent)
                ecsManager.getComponent(collectibleId, Components.COLLECTIBLE_COMPONENT_CODE);
        if (collectibleComponent.type == CollectibleComponent.COIN){
            CollectorComponent collectorComponent = (CollectorComponent)
                    ecsManager.getComponent(collectorId, Components.COLLECTOR_COMPONENT_CODE);
            collectorComponent.coinCount += collectibleComponent.value;
            Gdx.app.log("collection system", String.valueOf(collectorComponent.coinCount));
        }
        return true;
    }

    /**
     * fires correct event when a collectible is collected
     * @param collectibleId
     * @param collectorId
     */
    private void fireEvents(int collectibleId, int collectorId){
        CollectibleComponent collectibleComponent = (CollectibleComponent)
                ecsManager.getComponent(collectibleId, Components.COLLECTIBLE_COMPONENT_CODE);
        switch (collectibleComponent.type){
            case CollectibleComponent.COIN:
                ecsManager.fireEvent(ECSEvents.coinCollectedEvent(collectorId, collectibleComponent.value));
                Gdx.app.log("collection system", "COIN COLLECTED");
                break;
            case CollectibleComponent.HEART:
                ecsManager.fireEvent(ECSEvents.heartCollectedEvent(collectorId, collectibleComponent.value));
                Gdx.app.log("collection system", "HEART COLLECTED" + String.valueOf(collectibleComponent.value));
                break;
            case CollectibleComponent.KEY:
                ecsManager.fireEvent(ECSEvents.keyCollectedEvent(collectorId, collectibleComponent.value));
                Gdx.app.log("collection system", "KEY COLLECTED");
                break;
            default:
                break;
        }
    }

    /**
     * removes collectible so that it appears it has been collected
     * @param collectibleId
     */
    private void removeCollectible(int collectibleId){
        ecsManager.removeEntity(collectibleId);
    }
}
