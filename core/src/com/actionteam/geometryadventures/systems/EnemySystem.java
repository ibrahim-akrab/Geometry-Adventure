package com.actionteam.geometryadventures.systems;
import com.actionteam.geometryadventures.AIUtils;
import com.actionteam.geometryadventures.GameUtils;
import com.actionteam.geometryadventures.components.CollisionComponent;
import com.actionteam.geometryadventures.components.Components;
import com.actionteam.geometryadventures.components.EnemyComponent;
import com.actionteam.geometryadventures.components.PhysicsComponent;
import com.actionteam.geometryadventures.ecs.ECSEventListener;
import com.actionteam.geometryadventures.ecs.System;
import com.actionteam.geometryadventures.events.ECSEvents;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import static com.actionteam.geometryadventures.components.EnemyComponent.EnemyState.STATE_CHASING;
import static com.actionteam.geometryadventures.components.EnemyComponent.EnemyState.STATE_MID_MOTION;
import static com.actionteam.geometryadventures.components.EnemyComponent.EnemyState.STATE_WAITING;
import static com.actionteam.geometryadventures.components.EnemyComponent.EnemyTask;
import static com.actionteam.geometryadventures.components.EnemyComponent.EnemyTask.TASK_DESTROY_THREAT;
import static com.actionteam.geometryadventures.components.EnemyComponent.EnemyTask.TASK_GO_TO;
import static com.actionteam.geometryadventures.components.EnemyComponent.EnemyTask.TASK_STOP;

/**
 * Created by rka97 on 4/2/2018.
 */

public class EnemySystem extends System implements ECSEventListener {
    int[] playerPosition;
    private AIUtils aiUtils;

    public EnemySystem() {
        super(Components.ENEMY_COMPONENT_CODE);
        aiUtils = GameUtils.aiUtils;
        playerPosition = new int[2];
    }

    public static void AddTaskToEnemy(EnemyComponent ec, EnemyTask task)
    {
        switch(task)
        {
            case TASK_DESTROY_THREAT:
                if (ec.currentState != STATE_CHASING)
                    ec.taskQueue.add(task);
                break;
            case TASK_GO_TO:
                ec.taskQueue.add(task);
                break;
            default:
                break;
        }
    }

    private boolean IsTaskDone(EnemyComponent ec, PhysicsComponent pc, CollisionComponent eCC,
                               EnemyTask task)
    {
        switch(task)
        {
            case TASK_DESTROY_THREAT:
                /* Task is done if enemy can not see the player.            */
                /* Or if the player is dead.                                */
                /* Implement if player is dead from the lifetime component. */
                if(!ec.canSeePlayer)
                {
                    ec.currentState = STATE_WAITING;
                    Gdx.app.log("Enemy System", "Player no longer seen.");
                    return true;
                }
                break;
            case TASK_GO_TO:
                // Is current position within 0.1 of the target position?
                float midX = pc.position.x + eCC.width / 2;
                float midY = pc.position.y + eCC.height / 2;
                float deltaX = ec.nextTilePosition.x - midX;
                float deltaY = ec.nextTilePosition.y - midY;
                if (Math.abs(deltaX) < 0.1 && Math.abs(deltaY) < 0.1)
                {
                    return true;
                }
                break;
            case TASK_STOP:
                Gdx.app.log("Enemy System", "Stop Task");
                pc.velocity.x = 0.0f;
                pc.velocity.y = 0.0f;
                ec.currentState = STATE_WAITING;
                return true;
            default:
                break;
        }
        return false;
    }

    private void ProcessEnemyTask(EnemyComponent ec, PhysicsComponent pc, CollisionComponent eCC,
                                  EnemyTask task)
    {
        switch(task)
        {
            case TASK_DESTROY_THREAT:
                Gdx.app.log("Enemy System", "Destroy Threat Task");
                ec.currentState = STATE_CHASING;
                EnemyTask destroyTask = ec.taskQueue.poll();
                /* Adds a task to go to the location with the current player position. */
                ec.taskQueue.add(TASK_GO_TO);
                ec.targetGoToPosition.x = playerPosition[0];
                ec.targetGoToPosition.y = playerPosition[1];
                float[] nextPos = aiUtils.calculatePath((int)Math.floor(pc.position.x + eCC.width / 2),
                        (int)Math.floor(pc.position.y + eCC.height / 2), (int)ec.targetGoToPosition.x, (int)ec.targetGoToPosition.y);
                ec.nextTilePosition.x = nextPos[0] + 0.5f;
                ec.nextTilePosition.y = nextPos[1] + 0.5f;
                /* Adds to the destroy threat ask again, such that the enemy can reconsider */
                /* And chase after the player if the player is not destroyed.               */
                ec.taskQueue.add(destroyTask);
                break;
            case TASK_GO_TO:
                Gdx.app.log("Enemy System", "Go to Task");
                if(ec.canSeePlayer)
                {
                    ec.targetGoToPosition.x = playerPosition[0];
                    ec.targetGoToPosition.y = playerPosition[1];
                }
                float midX = pc.position.x + eCC.width / 2;
                float midY = pc.position.y + eCC.height / 2;
                nextPos = aiUtils.calculatePath((int)Math.floor(midX),
                        (int)Math.floor(midY), (int)ec.targetGoToPosition.x, (int)ec.targetGoToPosition.y);
                ec.nextTilePosition.x = nextPos[0] + 0.5f;
                ec.nextTilePosition.y = nextPos[1] + 0.5f;
                float deltaX = ec.nextTilePosition.x - midX;
                float deltaY = ec.nextTilePosition.y - midY;
                float angle = (float)Math.atan2(deltaY, deltaX);
                pc.velocity.x = ec.speed * (float)Math.cos(angle);
                pc.velocity.y = ec.speed * (float)Math.sin(angle);
                angle = (float)Math.toDegrees(angle);
                pc.rotationAngle = angle;
                break;
            case TASK_STOP:
                break;
        }
    }

    private void ProcessEnemyTasks(EnemyComponent ec, PhysicsComponent pc, CollisionComponent eCC)
    {
        while (!ec.taskQueue.isEmpty())
        {
            EnemyTask currentTask = ec.taskQueue.peek();
            if (!IsTaskDone(ec, pc, eCC, currentTask))
            {
                ProcessEnemyTask(ec, pc, eCC, currentTask);
                return;
            }
            else
            {
                ec.taskQueue.remove();
            }
        }
        if(ec.taskQueue.isEmpty())
        {
            pc.velocity.x = 0;
            pc.velocity.y = 0;
        }
    }

    @Override
    public void update(float dt) {
        /* We should update the enemies per their programmed paths here. */
        for(int entity : entities){
            EnemyComponent enemyComponent = (EnemyComponent)ecsManager.getComponent(entity,
                    Components.ENEMY_COMPONENT_CODE);
            PhysicsComponent physicsComponent = (PhysicsComponent)ecsManager.getComponent(entity,
                    Components.PHYSICS_COMPONENT_CODE);
            CollisionComponent eCC = (CollisionComponent)ecsManager.getComponent(entity,
                    Components.COLLISION_COMPONENT_CODE);
            ProcessEnemyTasks(enemyComponent, physicsComponent, eCC);
            // update(enemyComponent, physicsComponent, eCC, dt,entity);
        }
    }

    private void update(EnemyComponent ec, PhysicsComponent pc, CollisionComponent eCC, float dt, int entity)
    {
        /* FSM (Flying Spaghetti Monster) below. */
        float[] nextPos;
        float   angle;
        float   deltaX;
        float   deltaY;
        switch (ec.currentState)
        {
            case STATE_MID_MOTION:
                float midX = pc.position.x + eCC.width / 2;
                float midY = pc.position.y + eCC.height / 2;
                nextPos = aiUtils.calculatePath((int)Math.floor(midX),
                        (int)Math.floor(midY), playerPosition[0], playerPosition[1]);
                ec.nextTilePosition.x = nextPos[0];
                ec.nextTilePosition.y = nextPos[1];
                deltaX = (ec.nextTilePosition.x + 0.5f) - midX;
                deltaY = (ec.nextTilePosition.y + 0.5f) - midY;
                angle = (float)Math.atan2(deltaY, deltaX);
                pc.velocity.x = ec.speed * (float)Math.cos(angle);
                pc.velocity.y = ec.speed * (float)Math.sin(angle);
                angle = (float)Math.toDegrees(angle);
                pc.rotationAngle = angle;
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
                    if(ec.pathPoints.isEmpty()) return;
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
                playerPosition[0] = (int)Math.floor(position[0]  + 0.35f);
                playerPosition[1] = (int)Math.floor(position[1]  + 0.35f);
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
