package com.actionteam.geometryadventures.systems;

import com.actionteam.geometryadventures.components.CollectibleComponent;
import com.actionteam.geometryadventures.components.Components;
import com.actionteam.geometryadventures.components.ParentEntityComponent;
import com.actionteam.geometryadventures.ecs.ECSEventListener;
import com.actionteam.geometryadventures.ecs.Entity;
import com.actionteam.geometryadventures.ecs.System;
import com.actionteam.geometryadventures.events.ECSEvents;

/**
 * Created by Ibrahim M. Akrab on 5/3/18.
 * ibrahim.m.akrab@gmail.com
 */
public class CollectionSystem extends System implements ECSEventListener{

    public CollectionSystem(){ super(Components.COLLECTIBLE_COMPONENT_CODE, Components.PARENT_ENTITY_COMPONENT_CODE);}

    @Override
    public boolean handle(int eventCode, Object message) {
        switch (eventCode){
            case ECSEvents.COLLECTIBLE_COLLIDED_EVENT:
                int[] collectibleData = (int[]) message;
                int collectibleId = collectibleData[0];
                int collectorId = collectibleData[1];
                if (collect(collectibleId, collectorId)){
                    fireEvents(collectibleId, collectorId);
                    removeCollectibleFromScreen(collectibleId);
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

    private boolean collect(int collectibleId, int collectorId){
        if (!ecsManager.entityHasComponent(collectorId, Components.CONTROL_COMPONENT_CODE)){
            return false;
        }
        ParentEntityComponent parentEntityComponent = (ParentEntityComponent)
                ecsManager.getComponent(collectibleId, Components.PARENT_ENTITY_COMPONENT_CODE);
        parentEntityComponent.parentEntityId = collectorId;
        return true;
    }

    private void fireEvents(int collectibleId, int collectorId){
        CollectibleComponent collectibleComponent = (CollectibleComponent)
                ecsManager.getComponent(collectibleId, Components.COLLECTIBLE_COMPONENT_CODE);
        switch (collectibleComponent.type){
            case CollectibleComponent.COIN:
                ecsManager.fireEvent(ECSEvents.coinCollectedEvent(collectorId, collectibleComponent.value));
                break;
            case CollectibleComponent.HEART:
                ecsManager.fireEvent(ECSEvents.heartCollectedEvent(collectorId, collectibleComponent.value));
                break;
            case CollectibleComponent.KEY:
                ecsManager.fireEvent(ECSEvents.keyCollectedEvent(collectorId, collectibleComponent.value));
                break;
            default:
                break;
        }
    }

    private void removeCollectibleFromScreen(int collectibleId){
        ecsManager.removeComponent(ecsManager.getComponent(collectibleId, Components.PHYSICS_COMPONENT_CODE).getId());
//        Entity entity = ecsManager.getEntity(collectibleId);
//        entity.removeComponent(Components.PHYSICS_COMPONENT_CODE);
    }
}
