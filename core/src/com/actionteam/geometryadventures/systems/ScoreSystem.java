package com.actionteam.geometryadventures.systems;

import com.actionteam.geometryadventures.components.Components;
import com.actionteam.geometryadventures.ecs.ECSEventListener;
import com.actionteam.geometryadventures.ecs.System;
import com.actionteam.geometryadventures.events.ECSEvents;

/**
 * Created by Ibrahim M. Akrab on 5/2/18.
 * ibrahim.m.akrab@gmail.com
 */
public class ScoreSystem extends System implements ECSEventListener{

    public ScoreSystem(){ super(Components.SCORE_COMPONENT_CODE);}

    @Override
    public boolean handle(int eventCode, Object message) {
        switch (eventCode){
            case ECSEvents.ENEMY_DEAD_EVENT:

        }
        return false;
    }

    @Override
    protected void ecsManagerAttached() {
        ecsManager.subscribe(ECSEvents.ENEMY_DEAD_EVENT, this);
    }

    @Override
    public void update(float dt) {

    }
}
