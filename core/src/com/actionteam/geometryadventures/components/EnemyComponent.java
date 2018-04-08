package com.actionteam.geometryadventures.components;

import com.actionteam.geometryadventures.ecs.Component;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.lang.Float;
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
    // Arrays of Float. [0]: x, [1]: y, [2]: angle at point, [3]: time standing in seconds.
    public ArrayList<Float[]> pathPoints;
    public int currentPointIndex;
    public float remainingTime;
    public EnemyState currentState;
    public EnemyState previousState;
    public float speed; // Not sure if including speed here (and not in Physics) is good.
    public float fieldOfView;
    public float lineOfSightLength;
    public Vector2 nextTilePosition;
    public boolean startedChasing;
    public EnemyComponent() {
        super(Components.ENEMY_COMPONENT_CODE);
        pathPoints = new ArrayList<Float[]>();
        currentPointIndex = 0;
        remainingTime = 0;
        speed = 3.0f;
        fieldOfView = (float)Math.toRadians(57.0f);
        lineOfSightLength = 10.0f;
        currentState = EnemyState.STATE_WAITING;
        previousState = EnemyState.STATE_MID_MOTION;
        nextTilePosition = new Vector2();
        startedChasing = false;
    }
}
