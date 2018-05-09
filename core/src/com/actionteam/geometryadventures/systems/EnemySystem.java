package com.actionteam.geometryadventures.systems;
import com.actionteam.geometryadventures.AIUtils;
import com.actionteam.geometryadventures.GameUtils;
import com.actionteam.geometryadventures.components.CollisionComponent;
import com.actionteam.geometryadventures.components.Components;
import com.actionteam.geometryadventures.components.EnemyComponent;
import com.actionteam.geometryadventures.components.HealthComponent;
import com.actionteam.geometryadventures.components.PhysicsComponent;
import com.actionteam.geometryadventures.components.WeaponComponent;
import com.actionteam.geometryadventures.ecs.ECSEventListener;
import com.actionteam.geometryadventures.ecs.System;
import com.actionteam.geometryadventures.events.ECSEvents;
import com.badlogic.gdx.math.Vector2;

import static com.actionteam.geometryadventures.components.EnemyComponent.EnemyState.*;
import static com.actionteam.geometryadventures.components.EnemyComponent.EnemyTask;
import static com.actionteam.geometryadventures.components.EnemyComponent.EnemyTask.*;

/**
 * Created by rka97 on 4/2/2018.
 */

public class EnemySystem extends System implements ECSEventListener {
    int[] playerPosition;
    private AIUtils aiUtils;
    static boolean playerDead = false;

    public EnemySystem() {
        super(Components.ENEMY_COMPONENT_CODE);
        aiUtils = GameUtils.aiUtils;
        playerPosition = new int[2];
    }

    public static void AddTaskToEnemy(EnemyComponent ec, EnemyTask task)
    {
        switch(task)
        {
            case TASK_FOLLOW_SHOT:
                ec.taskQueue.clear();
                AddTaskToEnemy(ec, TASK_GO_TO);
                AddTaskToEnemy(ec, TASK_PATROL);
                break;
            case TASK_DESTROY_THREAT:
                if (ec.currentState != STATE_CHASING)
                {
                    ec.taskQueue.clear();
                    ec.taskQueue.add(task);
                }
                break;
            case TASK_GO_TO:
                ec.taskQueue.add(task);
                break;
            case TASK_GO_TO_CONTINUOUS:
                ec.taskQueue.add(task);
                break;
            case TASK_PATROL:
                if (ec.currentState != STATE_CHASING)
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
            case TASK_FOLLOW_SHOT:
                return true;
            case TASK_DESTROY_THREAT:
                /* Task is done if enemy can not see the player.            */
                /* Or if the player is dead.                                */
                if(!ec.canSeePlayer || playerDead)
                {
                    ec.currentState = STATE_WAITING;
//                    Gdx.app.log("Enemy System", "Task destroy threat is done.");
                    ec.taskQueue.add(TASK_PATROL);
                    return true;
                }
                break;
            case TASK_PATROL:
                if (ec.currentState != STATE_WAITING)
                {
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
                    ec.currentState = STATE_WAITING;
                    return true;
                }
                break;
            case TASK_GO_TO_CONTINUOUS:
                float dX = ec.targetGoToPosition.x - pc.position.x;
                float dY = ec.targetGoToPosition.y - pc.position.y;
                if (Math.abs(dX) < 0.1 && Math.abs(dY) < 0.1)
                {
                    ec.currentState = STATE_WAITING;
                    return true;
                }
                break;
            case TASK_STOP:
//                Gdx.app.log("Enemy System", "Stop Task");
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
            case TASK_PATROL:
//                Gdx.app.log("Enemy System", "Patrol Task");
                ec.currentState = EnemyComponent.EnemyState.STATE_WALKING;
                ec.taskQueue.add(TASK_PATROL);
                if(!(ec.patrolDirection.x == 0 && ec.patrolDirection.y == 0))
                {
                    ec.taskQueue.add(TASK_GO_TO_CONTINUOUS);
                    ec.targetGoToPosition.x = pc.position.x + ec.patrolDirection.x;
                    ec.targetGoToPosition.y = pc.position.y + ec.patrolDirection.y;
                    ec.taskQueue.add(TASK_PATROL);
                }
                break;
            case TASK_GO_TO_CONTINUOUS:
                float goToAngle = (float)Math.atan2(ec.patrolDirection.y, ec.patrolDirection.x);
                pc.velocity.x = ec.speed * ec.patrolDirection.x;
                pc.velocity.y = ec.speed * ec.patrolDirection.y;
                pc.rotationAngle = (float)Math.toDegrees(goToAngle);
                if(pc.rotationAngle < 0) pc.rotationAngle += 360;
                ec.motionLock = !ec.motionLock;
                break;
            case TASK_DESTROY_THREAT:
                ec.currentState = STATE_CHASING;
                EnemyTask destroyTask = ec.taskQueue.poll();
                /* Adds a task to go to the location with the current player position. */
                ec.taskQueue.add(TASK_GO_TO);
                ec.targetGoToPosition.x = playerPosition[0];
                ec.targetGoToPosition.y = playerPosition[1];
                ec.nextTilePosition.x = ec.targetGoToPosition.x;
                ec.nextTilePosition.y = ec.targetGoToPosition.y;
                /* Adds to the destroy threat ask again, such that the enemy can reconsider */
                /* And chase after the player if the player is not destroyed.               */
                ec.taskQueue.add(destroyTask);
                break;
            case TASK_GO_TO:
                //Gdx.app.log("Enemy System", "Go to Task");
                if(ec.canSeePlayer)
                {
                    ec.targetGoToPosition.x = playerPosition[0];
                    ec.targetGoToPosition.y = playerPosition[1];
                }
                float midX = pc.position.x + eCC.width / 2;
                float midY = pc.position.y + eCC.height / 2;
                float[] nextPos = aiUtils.calculatePath((int)Math.floor(midX),
                        (int)Math.floor(midY), (int)ec.targetGoToPosition.x, (int)ec.targetGoToPosition.y);
                ec.nextTilePosition.x = nextPos[0] + 0.5f;
                ec.nextTilePosition.y = nextPos[1] + 0.5f;
                float deltaX = ec.nextTilePosition.x - midX;
                float deltaY = ec.nextTilePosition.y - midY;
                float angle = (float)Math.atan2(deltaY, deltaX);
                pc.velocity.x = ec.speed * (float)Math.cos(angle);
                pc.velocity.y = ec.speed * (float)Math.sin(angle);
                angle = (float)Math.toDegrees(angle);
                if(angle < 0) angle += 360;
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
            /* Stop moving if nothing to do. */
            pc.velocity.x = 0;
            pc.velocity.y = 0;
        }
    }

    private void ProcessEnemyState(int entityId, EnemyComponent ec, PhysicsComponent pc, CollisionComponent eCC)
    {
        switch(ec.currentState)
        {
            case STATE_CHASING:
                WeaponComponent wp = (WeaponComponent)ecsManager.getComponent(entityId, Components.WEAPON_COMPONENT_CODE);
                float deltaX = playerPosition[0] - pc.position.x;
                float deltaY = playerPosition[1] - pc.position.y;
                float angle = (float)(2*Math.PI - Math.atan2(deltaY, deltaX));
                if (wp.weaponDamageRegion == WeaponComponent.SEMICIRCLE)
                {
                    /* Melee */
                    if(Math.abs(deltaX*deltaX + deltaY*deltaY) < wp.radiusOfDamageRegion * wp.radiusOfDamageRegion)
                    {
                        ecsManager.fireEvent(ECSEvents.attackEvent
                                (pc.position.x, pc.position.y, angle, entityId, false));
                    }
                }
                else
                {
                    /* Ranged */
                    ecsManager.fireEvent(ECSEvents.attackEvent
                            (pc.position.x, pc.position.y, angle, entityId, false));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void update(float dt) {
        /* We should update the enemies per their programmed paths here. */
        for (int entity : entities) {
            EnemyComponent enemyComponent = (EnemyComponent) ecsManager.getComponent(entity,
                    Components.ENEMY_COMPONENT_CODE);
            PhysicsComponent physicsComponent = (PhysicsComponent) ecsManager.getComponent(entity,
                    Components.PHYSICS_COMPONENT_CODE);
            CollisionComponent eCC = (CollisionComponent) ecsManager.getComponent(entity,
                    Components.COLLISION_COMPONENT_CODE);
            ProcessEnemyTasks(enemyComponent, physicsComponent, eCC);
            ProcessEnemyState(entity, enemyComponent, physicsComponent, eCC);
            HealthComponent healthComponent = (HealthComponent)
                    ecsManager.getComponent(entity, Components.HEALTH_COMPONENT_CODE);
        }
    }

    private void SwitchDirectionClockwise(EnemyComponent ec, PhysicsComponent pc)
    {
        if (ec.motionLock)
            return;
        if (ec.patrolDirection.x == 1 && ec.patrolDirection.y == 0)
        {
            ec.patrolDirection.x = 0;
            ec.patrolDirection.y = -1;
        }
        else if (ec.patrolDirection.x == 0 && ec.patrolDirection.y == -1)
        {
            ec.patrolDirection.x = -1;
            ec.patrolDirection.y = 0;
        }
        else if (ec.patrolDirection.x == -1 && ec.patrolDirection.y == 0)
        {
            ec.patrolDirection.x = 0;
            ec.patrolDirection.y = 1;
        }
        else if (ec.patrolDirection.x == 0 && ec.patrolDirection.y == 1)
        {
            ec.patrolDirection.x = 1;
            ec.patrolDirection.y = 0;
        }
        else
        {
//            Gdx.app.log("EnemySystem", "can not move anywhere!");
            ec.patrolDirection.x = 0;
            ec.patrolDirection.y = 0;
        }
        ec.motionLock = true;
    }

    private void ListenToLoudWeapon()
    {
        /* We should update the enemies per their programmed paths here. */
        for (int entity : entities) {
            EnemyComponent ec = (EnemyComponent) ecsManager.getComponent(entity,
                    Components.ENEMY_COMPONENT_CODE);
            PhysicsComponent pc = (PhysicsComponent) ecsManager.getComponent(entity,
                    Components.PHYSICS_COMPONENT_CODE);
            Vector2 delta = new Vector2(playerPosition[0] - pc.position.x,
                    playerPosition[1] - pc.position.y);
            if (delta.len2() <= ec.hearingRadius * ec.hearingRadius)
            {
                ec.targetGoToPosition.x = playerPosition[0];
                ec.targetGoToPosition.y = playerPosition[1];
                AddTaskToEnemy(ec, TASK_FOLLOW_SHOT);
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
                playerPosition[0] = (int)Math.floor(position[0]  + 0.35f);
                playerPosition[1] = (int)Math.floor(position[1]  + 0.35f);
                break;
            case ECSEvents.LOUD_WEAPON_FIRED_EVENT:
                ListenToLoudWeapon();
                break;
            case ECSEvents.ENEMY_COLLIDED_EVENT:
                int enemyID = (Integer) message;
                EnemyComponent enemyComponent = (EnemyComponent)ecsManager.getComponent(enemyID,
                        Components.ENEMY_COMPONENT_CODE);
                PhysicsComponent physicsComponent = (PhysicsComponent)ecsManager.getComponent(enemyID,
                        Components.PHYSICS_COMPONENT_CODE);
                SwitchDirectionClockwise(enemyComponent, physicsComponent);
                break;
            case ECSEvents.PLAYER_DEAD_EVENT:
                playerDead = true;
                break;
            default:
                break;
        }
        return false;
    }
}