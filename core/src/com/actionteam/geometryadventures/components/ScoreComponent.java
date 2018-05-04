package com.actionteam.geometryadventures.components;

import com.actionteam.geometryadventures.ecs.Component;
import com.badlogic.gdx.utils.TimeUtils;


/**
 * Created by Ibrahim M. Akrab on 5/2/18.
 * ibrahim.m.akrab@gmail.com
 */
public class ScoreComponent extends Component {

    public static final int ENEMY_DEATH_SCORE= 10;
    public static final long TIME_BETWEEN_EACH_KILL_TO_COMBO = 500;
    public static final int SCORE_PER_COMBO = 2;

    public long lastKillTime;
    public int comboNumber = 0;
    public long levelStartTime;
    public int longestCombo = 0;
    public int killsNumber = 0;

    public int score = 0;

    public ScoreComponent() {
        super(Components.SCORE_COMPONENT_CODE);
        lastKillTime= TimeUtils.millis();
        levelStartTime = TimeUtils.millis();
    }
}
