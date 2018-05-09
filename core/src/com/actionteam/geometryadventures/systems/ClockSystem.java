package com.actionteam.geometryadventures.systems;

import com.actionteam.geometryadventures.components.Components;
import com.actionteam.geometryadventures.ecs.ECSEventListener;
import com.actionteam.geometryadventures.ecs.System;
import com.actionteam.geometryadventures.events.ECSEvents;

/**
 * Created by theartful on 5/9/18.
 */

public class ClockSystem extends System implements ECSEventListener {

    public static int clock;
    public static int gameTime;
    public static float gameSeconds;
    public static float gameMinutes;
    private boolean isLevelRunning;

    public ClockSystem() {
        super(Components.CLOCK_COMPONENT);
        gameTime = 0;
        clock = 0;
    }

    @Override
    protected void ecsManagerAttached() {
        ecsManager.subscribe(ECSEvents.LEVEL_STARTED, this);
        ecsManager.subscribe(ECSEvents.LEVEL_PAUSED, this);
    }

    @Override
    public void update(float dt) {
        clock += dt * 1000;
        if (isLevelRunning) {
            gameTime += dt * 1000;
            gameSeconds += dt;
            gameMinutes += dt / 60;
            if(gameSeconds >= 60) gameSeconds -= 60;
        }
    }

    @Override
    public boolean handle(int eventCode, Object message) {
        switch (eventCode) {
            case ECSEvents.LEVEL_STARTED:
                isLevelRunning = true;
                break;
            case ECSEvents.LEVEL_PAUSED:
                isLevelRunning = false;
                break;
        }
        return false;
    }

    public static long timeSinceMillis(long timeOfCreation) {
        return clock - timeOfCreation;
    }

    public static int millis() {
        return clock;
    }
}
