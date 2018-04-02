package com.actionteam.geometryadventures.systems;

import com.actionteam.geometryadventures.components.Components;
import com.actionteam.geometryadventures.ecs.ECSEventListener;
import com.actionteam.geometryadventures.ecs.System;
import com.actionteam.geometryadventures.events.ECSEvents;

public class WeaponSystem extends System implements ECSEventListener{

    public WeaponSystem(){ super(Components.WEAPON_COMPONENT_CODE, Components.HEALTH_COMPONENT_CODE, Components.LETHAL_COMPONENT_CODE);}

    @Override
    public boolean handle(int eventCode, Object message) {
        switch (eventCode){
            case ECSEvents.ATTACK_EVENT:
                float[] weaponData = (float[]) message;
                entityAttacked(weaponData[0], weaponData[1], weaponData[2], (int)weaponData[3]);
        }
        return false;
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void ecsManagerAttached(){
        ecsManager.subscribe(ECSEvents.ATTACK_EVENT, this);
    }

    private void entityAttacked(float x, float y, float angle, int componentId){

    }
}
