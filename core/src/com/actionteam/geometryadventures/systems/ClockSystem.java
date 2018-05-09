package com.actionteam.geometryadventures.systems;

import com.actionteam.geometryadventures.ecs.ECSEvent;
import com.actionteam.geometryadventures.ecs.ECSEventListener;
import com.actionteam.geometryadventures.ecs.System;
import com.actionteam.geometryadventures.events.ECSEvents;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Created by theartful on 5/9/18.
 * <p>
 * This system is responsible for providing in game clock and
 * delaying tasks
 */

public class ClockSystem extends System implements ECSEventListener {

    // time in millis
    private static int clock;
    // in-game time in millis
    private static int gameTime;
    // in-game time in seconds
    private static float gameSeconds;
    // in-game time in minutes
    private static float gameMinutes;
    private boolean isLevelRunning;
    // priority queue of tasks to be done
    private PriorityQueue<Task> tasks;

    /**
     * The clock system can be given tasks to be done after @delay time
     * Tasks are just events to be fired to the ecs manager
     */
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
        super();
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
        // update time
        clock += dt * 1000;
        if (isLevelRunning) {
            gameTime += dt * 1000;
            gameSeconds += dt;
            gameMinutes += dt / 60;
            if (gameSeconds >= 60) gameSeconds -= 60;
        }

        // see if any task is due and do it
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

    /**
     * @return time passed since @time
     */
    static long timeSinceMillis(long time) {
        return gameTime - time;
    }

    /**
     * @return time in millis
     */
    public static int millis() {
        return gameTime;
    }

    /**
     * Adds task to be done
     * @param task task to be added
     */
    void addTask(Task task) {
        tasks.add(task);
    }

    static float getGameSeconds() {
        return gameSeconds;
    }

    /**
     * @return game time in minutes
     */
    static float getGameMinutes() {
        return gameMinutes;
    }
}
