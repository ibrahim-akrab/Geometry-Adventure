package com.actionteam.geometryadventures.systems;

import com.actionteam.geometryadventures.AIUtils;
import com.actionteam.geometryadventures.GameUtils;
import com.actionteam.geometryadventures.components.CollisionComponent;
import com.actionteam.geometryadventures.components.EnemyComponent;
import com.actionteam.geometryadventures.components.PhysicsComponent;
import com.actionteam.geometryadventures.ecs.System;
import com.actionteam.geometryadventures.components.Components;
import com.actionteam.geometryadventures.ecs.ECSEventListener;
import com.actionteam.geometryadventures.events.ECSEvents;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import static com.actionteam.geometryadventures.components.EnemyComponent.EnemyState.STATE_CHASING;

/**
 * Created by rka97 on 4/9/2018.
 */

public class VisionSystem extends System implements ECSEventListener {
    Vector2 playerPosition;
    private AIUtils aiUtils;

    public VisionSystem() {
        super(Components.ENEMY_COMPONENT_CODE);
        aiUtils = GameUtils.aiUtils;
        playerPosition = new Vector2();
    }

    @Override
    public void update(float dt) {
        for (int entity : entities) {
            updateEnemyVisibility(entity);
        }
    }

    private boolean isClockwise(float startAngle, float angle)
    {
        return Math.sin(Math.toRadians(startAngle - angle)) < 0;
    }

    private void updateEnemyVisibility(int entity) {
        EnemyComponent ec = (EnemyComponent)ecsManager.getComponent(entity,
                Components.ENEMY_COMPONENT_CODE);
        PhysicsComponent pc = (PhysicsComponent)ecsManager.getComponent(entity,
                Components.PHYSICS_COMPONENT_CODE);
        CollisionComponent cc = (CollisionComponent)ecsManager.getComponent(entity,
                Components.COLLISION_COMPONENT_CODE);
        Vector2 enemyPosition = new Vector2(pc.position.x + cc.width/2,
                pc.position.y + cc.height/2);
        Vector2 delta = new Vector2(playerPosition.x - enemyPosition.x,
                playerPosition.y - enemyPosition.y);
        float angle = (float)Math.toDegrees(Math.atan2(delta.y, delta.x));
        float enemyRotationAngle = pc.rotationAngle;
        float viewArcStartAngle = enemyRotationAngle - ec.fieldOfView;
        float viewArcEndAngle   = enemyRotationAngle + ec.fieldOfView;
        if ( !isClockwise(viewArcStartAngle, angle) || isClockwise(viewArcEndAngle, angle) ||
                delta.len2() > ec.lineOfSightLength * ec.lineOfSightLength)
        {
            return;
        }
        Gdx.app.log("EnemySystem", "Player within range");
        // If we reach here, the player's in our view arc. We need to do collision detection on
        // the player-enemy ray.
        Vector2 start = new Vector2 (enemyPosition.x, enemyPosition.y);
        Vector2 end = new Vector2 (playerPosition.x, playerPosition.y);
        if(!aiUtils.checkLineSegmentCollision(start, end))
        {
            Gdx.app.log("EnemySystem", "Can See player!");
            ec.canSeePlayer = true;
            EnemySystem.AddTaskToEnemy(ec, EnemyComponent.EnemyTask.TASK_DESTROY_THREAT);
        }
        else
        {
            ec.canSeePlayer = false;
            Gdx.app.log("VisionSystem", "Can not see player!");
        }
    }

    @Override
    public void ecsManagerAttached() {
        ecsManager.subscribe(ECSEvents.PLAYER_MOVED_EVENT, this);
    }

    @Override
    public boolean handle(int eventCode, Object message) {
        switch (eventCode) {
            case ECSEvents.PLAYER_MOVED_EVENT:
                float[] position = (float[]) message;
                playerPosition.x = (int) Math.floor(position[0] + 0.35f);
                playerPosition.y = (int) Math.floor(position[1] + 0.35f);
                break;
            default:
                break;
        }
        return false;
    }
}
