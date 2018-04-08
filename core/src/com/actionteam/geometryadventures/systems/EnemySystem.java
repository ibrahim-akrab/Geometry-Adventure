package com.actionteam.geometryadventures.systems;
import com.actionteam.geometryadventures.AIUtils;
import com.actionteam.geometryadventures.GameUtils;
import com.actionteam.geometryadventures.components.Components;
import com.actionteam.geometryadventures.components.EnemyComponent;
import com.actionteam.geometryadventures.components.PhysicsComponent;
import com.actionteam.geometryadventures.ecs.ECSEventListener;
import com.actionteam.geometryadventures.ecs.System;
import com.actionteam.geometryadventures.events.ECSEvents;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
/**
 * Created by rka97 on 4/2/2018.
 */

public class EnemySystem extends System implements ECSEventListener {
    int[] playerPosition;

    public EnemySystem() {
        super(Components.ENEMY_COMPONENT_CODE);
        aiUtils = GameUtils.aiUtils;
        playerPosition = new int[2];
    }

    private AIUtils aiUtils;

    @Override
    public void update(float dt) {
        /* We should update the enemies per their programmed paths here. */
        for(int entity : entities){
            EnemyComponent enemyComponent = (EnemyComponent)ecsManager.getComponent(entity,
                    Components.ENEMY_COMPONENT_CODE);
            PhysicsComponent physicsComponent = (PhysicsComponent)ecsManager.getComponent(entity,
                    Components.PHYSICS_COMPONENT_CODE);
            update(enemyComponent, physicsComponent, dt,entity);
        }
    }

    private void update(EnemyComponent ec, PhysicsComponent pc, float dt, int entity)
    {
        /* FSM (Flying Spaghetti Monster) below. */
        switch (ec.currentState)
        {
            case STATE_CHASING:
                float[] nextPos = aiUtils.calculatePath((int)Math.round(pc.position.x),
                        (int)Math.round(pc.position.y), playerPosition[0], playerPosition[1]);
                float angle = (float)Math.atan2(nextPos[1]-pc.position.y, nextPos[0]-pc.position.x);
                pc.velocity.x = ec.speed * (float)Math.cos(angle);
                pc.velocity.y = ec.speed * (float)Math.sin(angle);
                angle = (float)Math.atan2(playerPosition[1] - pc.position.y, playerPosition[0] - pc.position.x);
                pc.rotationAngle = ((float)Math.toDegrees(angle) + pc.rotationAngle*9)/10;
                break;
            case STATE_COMBAT:
                //TODO: combat.
                break;
            case STATE_WAITING:
                ec.remainingTime -= dt;
                if (ec.remainingTime <= 0) {
                    ec.currentState = EnemyComponent.EnemyState.STATE_WALKING;
                    ec.currentPointIndex = (ec.currentPointIndex + 1) % ec.pathPoints.size();
                    Float[] nextPosition = ec.pathPoints.get(ec.currentPointIndex);
                    float deltaX = nextPosition[0] - pc.position.x;
                    float deltaY = nextPosition[1] - pc.position.y;
                    angle = (float)Math.atan2(deltaY, deltaX);
                    pc.velocity.x = ec.speed * (float)Math.cos(angle);
                    pc.velocity.y = ec.speed * (float)Math.sin(angle);
                    pc.rotationAngle = (float)Math.toDegrees(angle);
                }
                break;
            case STATE_WALKING:
                Float[] nextPosition = ec.pathPoints.get(ec.currentPointIndex);
                float deltaX = nextPosition[0] - pc.position.x;
                float deltaY = nextPosition[1] - pc.position.y;
                if (Math.abs(deltaX) <= 0.1 && Math.abs(deltaY) <= 0.1)
                {
                    ec.currentState = EnemyComponent.EnemyState.STATE_WAITING;
                    ec.remainingTime = nextPosition[3];
                    pc.velocity.x = 0;
                    pc.velocity.y = 0;
                    pc.rotationAngle = nextPosition[2];
                }
                break;
            default:
                break;
        }
    }

    private void checkPlayerVisibility(float playerX, float playerY) {
        for(int entity : entities) {
            EnemyComponent ec = (EnemyComponent)ecsManager.getComponent(entity,
                    Components.ENEMY_COMPONENT_CODE);
            PhysicsComponent pc = (PhysicsComponent)ecsManager.getComponent(entity,
                    Components.PHYSICS_COMPONENT_CODE);

            float deltaX = playerX - pc.position.x;
            float deltaY = playerY - pc.position.y;
            // Is the player in this enemy's view arc?
            float lookAtAngle = (float)Math.atan2(deltaY, deltaX);
            Gdx.app.log("EnemySystem", "Here");
            if (Math.abs(lookAtAngle - pc.rotationAngle) > ec.fieldOfView ||
                    deltaX * deltaX + deltaY * deltaY > ec.lineOfSightLength * ec.lineOfSightLength)
                continue;
            // If we reach here, the player's in our view arc. We need to do collision detection on
            // the player-enemy ray.
            Gdx.app.log("EnemySystem", "Player seen!");
        }
    }

    @Override
    public void ecsManagerAttached() {
        ecsManager.subscribe(ECSEvents.PLAYER_MOVED_EVENT,this);
        ecsManager.subscribe(ECSEvents.LOUD_WEAPON_FIRED_EVENT, this);
    }

    @Override
    public boolean handle(int eventCode, Object message) {
        switch (eventCode) {
            case ECSEvents.PLAYER_MOVED_EVENT:
                float[] position = (float[]) message;
                //checkPlayerVisibility(position[0], position[1]);
                //Gdx.app.log("EnemySystem","Player position: " + position[0] + " " + position[1] + ").");
                // Chase after the player.
                playerPosition[0] = (int)position[0];
                playerPosition[1] = (int)position[1];
                break;
            case ECSEvents.LOUD_WEAPON_FIRED_EVENT:
                break;
            default:
                break;
        }
        return false;
    }
}
