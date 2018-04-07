package com.actionteam.geometryadventures.components;

import com.actionteam.geometryadventures.ecs.Component;

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
        STATE_COMBAT
    }
    // Arrays of Float. [0]: x, [1]: y, [2]: angle at point, [3]: time standing in seconds.
    public ArrayList<Float[]> pathPoints;
    public int currentPointIndex;
    public float remainingTime;
    public EnemyState currentState;
    public float speed; // Not sure if including speed here (and not in Physics) is good.
    public float fieldOfView;
    public float lineOfSightLength;

    public EnemyComponent() {
        super(Components.ENEMY_COMPONENT_CODE);
        pathPoints = new ArrayList<Float[]>();
        currentPointIndex = 0;
        remainingTime = 0;
        speed = 3.0f;
        fieldOfView = 57.0f;
        lineOfSightLength = 3.0f;
        currentState = EnemyState.STATE_CHASING;
    }
}
