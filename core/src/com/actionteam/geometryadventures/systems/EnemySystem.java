package com.actionteam.geometryadventures.systems;
import com.actionteam.geometryadventures.AIUtils;
import com.actionteam.geometryadventures.GameUtils;
import com.actionteam.geometryadventures.components.CollisionComponent;
import com.actionteam.geometryadventures.components.Components;
import com.actionteam.geometryadventures.components.EnemyComponent;
import com.actionteam.geometryadventures.components.PhysicsComponent;
import com.actionteam.geometryadventures.ecs.Component;
import com.actionteam.geometryadventures.ecs.ECSEventListener;
import com.actionteam.geometryadventures.ecs.System;
import com.actionteam.geometryadventures.events.ECSEvents;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import static com.actionteam.geometryadventures.components.EnemyComponent.EnemyState.STATE_CALIBRATION;
import static com.actionteam.geometryadventures.components.EnemyComponent.EnemyState.STATE_CHASING;
import static com.actionteam.geometryadventures.components.EnemyComponent.EnemyState.STATE_MID_MOTION;

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
        CollisionComponent eCC = (CollisionComponent)ecsManager.getComponent(entity,
                Components.COLLISION_COMPONENT_CODE);
        switch (ec.currentState)
        {
            case STATE_MID_MOTION:
                float midX = pc.position.x + eCC.width / 2;
                float midY = pc.position.y + eCC.height / 2;
                float[] nextPos = aiUtils.calculatePath((int)Math.floor(midX),
                        (int)Math.floor(midY), playerPosition[0], playerPosition[1]);
                ec.nextTilePosition.x = nextPos[0];
                ec.nextTilePosition.y = nextPos[1];
                float deltaX = (ec.nextTilePosition.x + 0.5f) - midX;
                float deltaY = (ec.nextTilePosition.y + 0.5f) - midY;
                float angle = (float)Math.atan2(deltaY, deltaX);
                pc.velocity.x = ec.speed * (float)Math.cos(angle);
                pc.velocity.y = ec.speed * (float)Math.sin(angle);
                angle = (float)Math.toDegrees(angle);
                pc.rotationAngle = (pc.rotationAngle*9+angle)/10.0f;
                if(Math.abs(deltaX) < 0.1 && Math.abs(deltaY) < 0.1)
                {
                    ec.currentState = ec.previousState;
                }
                break;
            case STATE_CHASING:
                nextPos = aiUtils.calculatePath((int)Math.floor(pc.position.x + eCC.width/2),
                        (int)Math.floor(pc.position.y + eCC.height/2), playerPosition[0], playerPosition[1]);
                ec.nextTilePosition.x = nextPos[0];
                ec.nextTilePosition.y = nextPos[1];
                ec.currentState = STATE_MID_MOTION;
                ec.previousState = STATE_CHASING;
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
                    deltaX = nextPosition[0] - pc.position.x;
                    deltaY = nextPosition[1] - pc.position.y;
                    angle = (float)Math.atan2(deltaY, deltaX);
                    pc.velocity.x = ec.speed * (float)Math.cos(angle);
                    pc.velocity.y = ec.speed * (float)Math.sin(angle);
                    pc.rotationAngle = (float)Math.toDegrees(angle);
                }
                break;
            case STATE_WALKING:
                Float[] nextPosition = ec.pathPoints.get(ec.currentPointIndex);
                deltaX = nextPosition[0] - pc.position.x;
                deltaY = nextPosition[1] - pc.position.y;
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
            CollisionComponent cc = (CollisionComponent)ecsManager.getComponent(entity,
                    Components.COLLISION_COMPONENT_CODE);
            float enemyX = pc.position.x + cc.width/2;
            float enemyY = pc.position.y + cc.height/2;
            float deltaX = playerX - enemyY;
            float deltaY = playerY - enemyY;
            // Is the player in this enemy's view arc?
            float lookAtAngle = (float)Math.atan2(deltaY, deltaX);
            if (Math.abs(lookAtAngle - pc.rotationAngle) > ec.fieldOfView ||
                    deltaX * deltaX + deltaY * deltaY > ec.lineOfSightLength * ec.lineOfSightLength)
                continue;
            Gdx.app.log("EnemySystem", "Player seen");
            // If we reach here, the player's in our view arc. We need to do collision detection on
            // the player-enemy ray.
            Vector2 start = new Vector2 (enemyX, enemyY);
            Vector2 end = new Vector2 (playerX, playerY);
            if(!aiUtils.checkLineSegmentCollision(start, end))
            {
                Gdx.app.log("EnemySystem", "Path to player available!");
            }
        }
    }

    @Override
    public void ecsManagerAttached() {
        ecsManager.subscribe(ECSEvents.PLAYER_MOVED_EVENT,this);
        ecsManager.subscribe(ECSEvents.LOUD_WEAPON_FIRED_EVENT, this);
        ecsManager.subscribe(ECSEvents.ENEMY_COLLIDED_EVENT, this);
    }

    @Override
    public boolean handle(int eventCode, Object message) {
        switch (eventCode) {
            case ECSEvents.PLAYER_MOVED_EVENT:
                float[] position = (float[]) message;
                //Gdx.app.log("EnemySystem","Player position: " + position[0] + " " + position[1] + ").");
                // Chase after the player.
                playerPosition[0] = (int)Math.floor(position[0]  + 0.35f);
                playerPosition[1] = (int)Math.floor(position[1]  + 0.35f);
                checkPlayerVisibility(playerPosition[0], playerPosition[1]);
                break;
            case ECSEvents.LOUD_WEAPON_FIRED_EVENT:
                break;
            case ECSEvents.ENEMY_COLLIDED_EVENT:
                int enemyID = (Integer) message;
                EnemyComponent enemyComponent = (EnemyComponent)ecsManager.getComponent(enemyID,
                        Components.ENEMY_COMPONENT_CODE);
                enemyComponent.previousState = enemyComponent.currentState;
                //enemyComponent.currentState = STATE_CALIBRATION;
                break;
            default:
                break;
        }
        return false;
    }
}
