package com.actionteam.geometryadventures.systems;

import com.actionteam.geometryadventures.components.Components;
import com.actionteam.geometryadventures.components.ScoreComponent;
import com.actionteam.geometryadventures.ecs.ECSEventListener;
import com.actionteam.geometryadventures.ecs.System;
import com.actionteam.geometryadventures.events.ECSEvents;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Created by Ibrahim M. Akrab on 5/2/18.
 * ibrahim.m.akrab@gmail.com
 */
public class ScoreSystem extends System implements ECSEventListener{

    public ScoreSystem(){ super(Components.SCORE_COMPONENT_CODE);}

    @Override
    public boolean handle(int eventCode, Object message) {
        switch (eventCode){
            case ECSEvents.ENEMY_DEAD_EVENT: {
                int[] eventData = (int[]) message;
                int enemyId = eventData[0];
                int playerId = eventData[1];
                increaseScore(playerId, ScoreComponent.ENEMY_DEATH_SCORE);
                break;
            }
            case ECSEvents.PLAYER_DEAD_EVENT: {
                int[] eventData = (int[]) message;
                int enemyId = eventData[0];
                int playerId = eventData[1];
                Gdx.app.log("Player status", "Dead");
                break;
            }
            default:
                    return false;
        }
        return true;
    }

    @Override
    protected void ecsManagerAttached() {
        ecsManager.subscribe(ECSEvents.ENEMY_DEAD_EVENT, this);
        ecsManager.subscribe(ECSEvents.PLAYER_DEAD_EVENT, this);
    }

    @Override
    public void update(float dt) {

    }

    public void increaseScore(int killerId, int scoreIncrementValue){
        ScoreComponent scoreComponent = (ScoreComponent)
                ecsManager.getComponent(killerId, Components.SCORE_COMPONENT_CODE);
        if (scoreComponent == null){
            return;
        }
        scoreComponent.score += scoreIncrementValue;
        checkCombo(scoreComponent);
        scoreComponent.lastKillTime = TimeUtils.millis();
//        Gdx.app.log("Player Score", String.valueOf(scoreComponent.score));
//        Gdx.app.log("Player Combo", String.valueOf(scoreComponent.comboNumber));
    }

    public void checkCombo(ScoreComponent scoreComponent){
        if (TimeUtils.timeSinceMillis(scoreComponent.lastKillTime) < ScoreComponent.TIME_BETWEEN_EACH_KILL_TO_COMBO){
            scoreComponent.comboNumber++;
        } else {
            scoreComponent.score += scoreComponent.comboNumber * scoreComponent.comboNumber * ScoreComponent.SCORE_PER_COMBO;
            scoreComponent.comboNumber = 0;

        }
    }

}
