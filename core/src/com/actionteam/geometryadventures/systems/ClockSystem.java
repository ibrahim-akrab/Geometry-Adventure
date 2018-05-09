package com.actionteam.geometryadventures.systems;

import com.actionteam.geometryadventures.components.Components;
import com.actionteam.geometryadventures.ecs.ECSEvent;
import com.actionteam.geometryadventures.ecs.ECSEventListener;
import com.actionteam.geometryadventures.ecs.System;
import com.actionteam.geometryadventures.events.ECSEvents;
import com.badlogic.gdx.Gdx;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Created by theartful on 5/9/18.
 */

public class ClockSystem extends System implements ECSEventListener {

    static int clock;
    static int gameTime;
    static float gameSeconds;
    static float gameMinutes;
    private boolean isLevelRunning;
    private PriorityQueue<Task> tasks;

    static public class Task {
        ECSEvent event;
        int timeIssued;
        int delay;

        public Task(ECSEvent event, int delay) {
            this.event = event;
            this.timeIssued = clock;
            this.delay = delay;
        }
    }

    public ClockSystem() {
        super(Components.CLOCK_COMPONENT);
        gameTime = 0;
        clock = 0;
        Comparator<Task> taskComparator = new Comparator<Task>() {
            @Override
            public int compare(Task t1, Task t2) {
                return t1.timeIssued + t1.delay - t2.timeIssued - t2.delay;
            }
        };
        tasks = new PriorityQueue<Task>(5, taskComparator);
    }

    @Override
    protected void ecsManagerAttached() {
        ecsManager.subscribe(ECSEvents.LEVEL_STARTED, this);
        ecsManager.subscribe(ECSEvents.LEVEL_PAUSED, this);
        ecsManager.subscribe(ECSEvents.ADD_TASK, this);
    }

    @Override
    public void update(float dt) {
        clock += dt * 1000;
        if (isLevelRunning) {
            gameTime += dt * 1000;
            gameSeconds += dt;
            gameMinutes += dt / 60;
            if (gameSeconds >= 60) gameSeconds -= 60;
        }
        if (!tasks.isEmpty()) {
            Task task = tasks.peek();
            if (task.timeIssued + task.delay <= clock) {
                ecsManager.fireEvent(task.event);
                tasks.poll();
            }
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
            case ECSEvents.ADD_TASK:
                addTask((Task) message);
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

    public void addTask(Task task) {
        tasks.add(task);
    }
}
