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
public class ScoreSystem extends System implements ECSEventListener {

    public ScoreSystem() {
        super(Components.SCORE_COMPONENT_CODE);
    }

    @Override
    public boolean handle(int eventCode, Object message) {
        switch (eventCode) {
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
                ecsManager.fireEvent(ECSEvents.endOfLevelEvent());
                Gdx.app.exit();
                break;
            }
            case ECSEvents.END_OF_LEVEL_EVENT:
                calculateFinalScore();
            default:
                return false;
        }
        return true;
    }

    @Override
    protected void ecsManagerAttached() {
        ecsManager.subscribe(ECSEvents.ENEMY_DEAD_EVENT, this);
        ecsManager.subscribe(ECSEvents.PLAYER_DEAD_EVENT, this);
        ecsManager.subscribe(ECSEvents.END_OF_LEVEL_EVENT, this);
    }

    @Override
    public void update(float dt) {

    }

    public void increaseScore(int killerId, int scoreIncrementValue) {
        ScoreComponent scoreComponent = (ScoreComponent)
                ecsManager.getComponent(killerId, Components.SCORE_COMPONENT_CODE);
        if (scoreComponent == null) {
            return;
        }
        scoreComponent.score += scoreIncrementValue;
        scoreComponent.killsNumber++;
        checkCombo(scoreComponent);
        scoreComponent.lastKillTime = TimeUtils.millis();
    }

    public void checkCombo(ScoreComponent scoreComponent) {
        if (TimeUtils.timeSinceMillis(scoreComponent.lastKillTime) < ScoreComponent.TIME_BETWEEN_EACH_KILL_TO_COMBO) {
            scoreComponent.comboNumber++;
        } else {
            scoreComponent.score += scoreComponent.comboNumber * scoreComponent.comboNumber * ScoreComponent.SCORE_PER_COMBO;
            if (scoreComponent.comboNumber > scoreComponent.longestCombo) {
                scoreComponent.longestCombo = scoreComponent.comboNumber;
            }
            scoreComponent.comboNumber = 0;
        }
    }

    public void calculateFinalScore() {
        for (int entity : entities) {
            ScoreComponent scoreComponent = (ScoreComponent)
                    ecsManager.getComponent(entity, Components.SCORE_COMPONENT_CODE);
            scoreComponent.score += scoreComponent.longestCombo * 12;
            scoreComponent.score += scoreComponent.killsNumber *
                    (3 * 60 * 1000 / TimeUtils.timeSinceMillis(scoreComponent.levelStartTime));
            Gdx.app.log("Score", String.valueOf(scoreComponent.score));
        }
    }

}
