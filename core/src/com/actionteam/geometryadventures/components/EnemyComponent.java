package com.actionteam.geometryadventures.components;

import com.actionteam.geometryadventures.ecs.Component;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.lang.Float;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by rka97 on 4/2/2018.
 */

public class EnemyComponent extends Component {
    public enum EnemyState {
        STATE_WAITING,
        STATE_WALKING,
        STATE_CHASING,
        STATE_COMBAT,
        STATE_CALIBRATION,
        STATE_MID_MOTION
    }
    public enum EnemyTask {
        TASK_DESTROY_THREAT,
        TASK_GO_TO,
        TASK_GO_TO_CONTINUOUS,
        TASK_PATROL,
        TASK_STOP
    }
    public Queue<EnemyTask> taskQueue;
    // Arrays of Float. [0]: x, [1]: y, [2]: angle at point, [3]: time standing in seconds.
    public ArrayList<Float[]> pathPoints;
    public int currentPointIndex;
    public float remainingTime;
    public EnemyState currentState;
    public EnemyState previousState;
    public float speed; // Not sure if including speed here (and not in Physics) is good.
    public float fieldOfView;
    public float lineOfSightLength;
    public Vector2 targetGoToPosition;
    public Vector2 nextTilePosition;
    public Vector2 patrolDirection;
    public boolean canSeePlayer;
    public boolean motionLock;
    public EnemyComponent() {
        super(Components.ENEMY_COMPONENT_CODE);
        pathPoints = new ArrayList<Float[]>();
        currentPointIndex = 0;
        remainingTime = 0;
        speed = 3.0f;
        fieldOfView = 57.0f;
        lineOfSightLength = 5.0f;
        currentState = EnemyState.STATE_WAITING;
        previousState = EnemyState.STATE_MID_MOTION;
        nextTilePosition = new Vector2();
        targetGoToPosition = new Vector2();
        patrolDirection = new Vector2(1, 0);
        canSeePlayer = false;
        taskQueue = new LinkedList<EnemyTask>();
        //taskQueue.add(EnemyTask.TASK_STOP);
        taskQueue.add(EnemyTask.TASK_PATROL);
        motionLock = false;
    }
}
