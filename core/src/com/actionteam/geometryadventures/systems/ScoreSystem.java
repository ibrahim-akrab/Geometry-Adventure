package com.actionteam.geometryadventures.systems;

import com.actionteam.geometryadventures.components.Components;
import com.actionteam.geometryadventures.components.ScoreComponent;
import com.actionteam.geometryadventures.ecs.ECSEventListener;
import com.actionteam.geometryadventures.ecs.System;
import com.actionteam.geometryadventures.events.ECSEvents;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Created by Ibrahim M. Akrab on 5/2/18.
 * ibrahim.m.akrab@gmail.com
 */
public class ScoreSystem extends System implements ECSEventListener {

    public ScoreSystem() {
        super(Components.SCORE_COMPONENT_CODE);
    }

    /**
     * handles when an event it is subscribed to is fired
     * @param eventCode determines event's type
     * @param message event's data
     * @return true of event has been handled
     */
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
                break;
            case ECSEvents.COIN_COLLECTED_EVENT:{
                int[] eventData = (int[]) message;
                int collectorId = eventData[0];
                int coinsValue = eventData[1];
                addCoinsToScore(collectorId, coinsValue);
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
        ecsManager.subscribe(ECSEvents.END_OF_LEVEL_EVENT, this);
        ecsManager.subscribe(ECSEvents.COIN_COLLECTED_EVENT, this);

    }

    @Override
    public void update(float dt) {

    }

    /**
     * increases entity's player based on the score's increment value
     * @param killerId
     * @param scoreIncrementValue
     */
    public void increaseScore(int killerId, int scoreIncrementValue) {
        ScoreComponent scoreComponent = (ScoreComponent)
                ecsManager.getComponent(killerId, Components.SCORE_COMPONENT_CODE);
        if (scoreComponent == null) {
            return;
        }
        scoreComponent.score += scoreIncrementValue;
        scoreComponent.killsNumber++;
        checkCombo(scoreComponent);
        scoreComponent.lastKillTime = ClockSystem.millis();
    }

    /**
     * checks for combos (killing more than an enemy fast enough)
     * @param scoreComponent
     */
    public void checkCombo(ScoreComponent scoreComponent) {
        if (ClockSystem.timeSinceMillis(scoreComponent.lastKillTime) < ScoreComponent.TIME_BETWEEN_EACH_KILL_TO_COMBO) {
            scoreComponent.comboNumber++;
        } else {
            scoreComponent.score += scoreComponent.comboNumber * scoreComponent.comboNumber * ScoreComponent.SCORE_PER_COMBO;
            if (scoreComponent.comboNumber > scoreComponent.longestCombo) {
                scoreComponent.longestCombo = scoreComponent.comboNumber;
            }
            scoreComponent.comboNumber = 0;
        }
    }

    /**
     * calculates final score when level is finished
     */
    public void calculateFinalScore() {
        for (int entity : entities) {
            ScoreComponent scoreComponent = (ScoreComponent)
                    ecsManager.getComponent(entity, Components.SCORE_COMPONENT_CODE);
            scoreComponent.score += scoreComponent.longestCombo * 12;
            scoreComponent.score += scoreComponent.killsNumber *
                    (3 * 60 * 1000 / ClockSystem.timeSinceMillis(scoreComponent.levelStartTime));
            Gdx.app.log("Score", String.valueOf(scoreComponent.score));
            Preferences preferences = Gdx.app.getPreferences("ScorePrefs");
            int highScore = preferences.getInteger("Score", scoreComponent.score);
            //Gdx.app.log("High Score = ", String.valueOf(highScore));
            if (scoreComponent.score >= highScore)
            {
                preferences.putInteger("Score", scoreComponent.score);
                preferences.flush();
            }
        }
    }

    /**
     * adds coins to the score bar in the player's score component
     * @param collectorId
     * @param coinsValue
     */
    public void addCoinsToScore(int collectorId, int coinsValue){
        ScoreComponent scoreComponent = (ScoreComponent)
                ecsManager.getComponent(collectorId, Components.SCORE_COMPONENT_CODE);
        scoreComponent.score += coinsValue;
    }
}
