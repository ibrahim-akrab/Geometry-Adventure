package com.actionteam.geometryadventures.systems;

import com.actionteam.geometryadventures.components.Components;
import com.actionteam.geometryadventures.components.HealthComponent;
import com.actionteam.geometryadventures.components.LethalComponent;
import com.actionteam.geometryadventures.ecs.ECSEventListener;
import com.actionteam.geometryadventures.ecs.System;
import com.actionteam.geometryadventures.events.ECSEvents;
import com.badlogic.gdx.Gdx;

/**
 * Created by Ibrahim M. Akrab on 5/1/18.
 * ibrahim.m.akrab@gmail.com
 */
public class HealthSystem extends System implements ECSEventListener {

    public HealthSystem(){ super(Components.HEALTH_COMPONENT_CODE);}

    @Override
    public boolean handle(int eventCode, Object message) {
        switch (eventCode){
            case ECSEvents.BULLET_COLLIDED_EVENT: {
                int[] data = (int[]) message;
                int bulletId = data[0];
                int entityId = data[1];
                if (bulletCollided(bulletId, entityId)) {
                    isDead(bulletId, entityId);
                }
                removeBullet(bulletId);
            }
            case ECSEvents.HEART_COLLECTED_EVENT:{
                int[] data = (int[]) message;
                int collectorId = data[0];
                int heartValue = data[1];
                increaseHealth(collectorId, heartValue);
            }

        }
        return false;
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void ecsManagerAttached(){
        ecsManager.subscribe(ECSEvents.BULLET_COLLIDED_EVENT, this);
        ecsManager.subscribe(ECSEvents.HEART_COLLECTED_EVENT, this);
    }

    /**
     *
     * @param bulletId
     * @param entityId
     * @return true if it hit an entity with a health component
     */
    private boolean bulletCollided(int bulletId, int entityId){
        // get the hit entity health component
        HealthComponent healthComponent = (HealthComponent)
                ecsManager.getComponent(entityId, Components.HEALTH_COMPONENT_CODE);
        if (healthComponent == null){
            return false;
        }
        Gdx.app.log("Health", String.valueOf(healthComponent.health));
        LethalComponent lethalComponent = (LethalComponent)
                ecsManager.getComponent(bulletId, Components.LETHAL_COMPONENT_CODE);
        healthComponent.takeDamage(lethalComponent.damage);
        return true;
    }

    private boolean isDead(int bulletId, int entityId) {
        HealthComponent healthComponent = (HealthComponent)
                ecsManager.getComponent(entityId, Components.HEALTH_COMPONENT_CODE);
        LethalComponent lethalComponent = (LethalComponent)
                ecsManager.getComponent(bulletId, Components.LETHAL_COMPONENT_CODE);
        if (healthComponent.health <= 0 && !healthComponent.isDead) {
            healthComponent.isDead = true;
            if (ecsManager.entityHasComponent(entityId, Components.ENEMY_COMPONENT_CODE)) {
                ecsManager.fireEvent(ECSEvents.enemyDeadEvent(entityId, lethalComponent.owner));
            } else {
                ecsManager.fireEvent(ECSEvents.playerDeadEvent(entityId, lethalComponent.owner));
                Gdx.app.log("Player", "Dead");
            }
            return true;
        }
        return false;
    }

    private void removeBullet(int bulletId){
        ecsManager.removeEntity(bulletId);
    }

    private void increaseHealth(int collectorId, int heartValue){
        HealthComponent healthComponent = (HealthComponent)
                ecsManager.getComponent(collectorId, Components.HEALTH_COMPONENT_CODE);
        healthComponent.heal(heartValue);
    }
}
