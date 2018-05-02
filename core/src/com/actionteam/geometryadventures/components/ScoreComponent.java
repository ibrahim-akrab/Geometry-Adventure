package com.actionteam.geometryadventures.components;

import com.actionteam.geometryadventures.ecs.Component;

/**
 * Created by Ibrahim M. Akrab on 5/2/18.
 * ibrahim.m.akrab@gmail.com
 */
public class ScoreComponent extends Component {

    public static final int enemyDeathScore = 10;

    public int score = 0;

    protected ScoreComponent() {
        super(Components.SCORE_COMPONENT_CODE);
    }

    public void increaseScore(int value){
        score += value;
    }
}
