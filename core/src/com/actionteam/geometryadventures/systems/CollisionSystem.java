package com.actionteam.geometryadventures.systems;

import com.actionteam.geometryadventures.components.CollisionComponent;
import com.actionteam.geometryadventures.components.Components;
import com.actionteam.geometryadventures.components.EnemyComponent;
import com.actionteam.geometryadventures.components.PhysicsComponent;
import com.actionteam.geometryadventures.ecs.ECSEventListener;
import com.actionteam.geometryadventures.ecs.System;
import com.actionteam.geometryadventures.events.ECSEvents;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Omnia- on 30/03/2018.
 */

public class CollisionSystem extends System implements ECSEventListener {

    public CollisionSystem() {
        super(Components.COLLISION_COMPONENT_CODE, Components.PHYSICS_COMPONENT_CODE);
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void ecsManagerAttached() {
        ecsManager.subscribe(ECSEvents.COLLIDABLE_MOVED_EVENT, this);
    }


    /*
    handles the collision event fired by the physics system.  sends the collision data to collide
     to check if a collision occurred.
    collision data : beginX ,beginY , endX,endY,entityID all stored in an array.
    */
    @Override
       public boolean handle(int eventCode, Object message) {
        switch (eventCode) {
            case ECSEvents.COLLIDABLE_MOVED_EVENT:
                float[] collisionData = (float[]) message;
                collide(collisionData[0], collisionData[1], collisionData[2], collisionData[3],
                        (int) collisionData[4]);


        }
        return true;
    }
    /*
      does the actual collision by checking the spacing between the bounding volumes of the two colliding objects
      using the parameters previously sent by handle.
     */
    public void collide(float beginX, float beginY, float endX, float endY, int entityID) {
        CollisionComponent myCc = (CollisionComponent) ecsManager.getComponent(entityID, Components.COLLISION_COMPONENT_CODE);
        PhysicsComponent myPc = (PhysicsComponent) ecsManager.getComponent(entityID, Components.PHYSICS_COMPONENT_CODE);
        CollisionComponent cc;
        PhysicsComponent pc;

        boolean entityCollided = false;

        for (int e : entities) {
            cc = (CollisionComponent) ecsManager.getComponent(e, Components.COLLISION_COMPONENT_CODE);
            pc = (PhysicsComponent) ecsManager.getComponent(e, Components.PHYSICS_COMPONENT_CODE);

            if (e == entityID || (cc.mask & (1L << myCc.id)) == 0) continue;

            if (myCc.shapeType == CollisionComponent.RECTANGLE) {
                switch (cc.shapeType) {
                    case CollisionComponent.RECTANGLE:
                        entityCollided = rectRectCollision(endX, endY, myCc.width, myCc.height, pc.position.x, pc.position.y,
                                cc.width, cc.height);
                        break;
                    case CollisionComponent.CIRCLE:
                        entityCollided = circRectCollision(endX,endY, myCc.width, myCc.height, pc.position.x,pc.position.y,cc.radius);
                        break;
                }
            } else {
                switch (cc.shapeType) {
                    case CollisionComponent.RECTANGLE:
                        entityCollided = circRectCollision(pc.position.x,pc.position.y,cc.width,cc.height,endX,endY,myCc.radius);
                        break;
                    case CollisionComponent.CIRCLE:
                        entityCollided = circCircCollision(pc.position.x,pc.position.y,cc.radius,endX,endY,myCc.radius);
                        break;
                }
            }

            if (entityCollided) {
                ecsManager.fireEvent(ECSEvents.collisionEvent(entityCollided));
                Gdx.app.log("Collision", entityID + " " + e);
                Gdx.app.log("Collision", "(" + beginX + ", " + beginY + ") " + "("
                                    + endX + ", " + endY + ").");
                if ((EnemyComponent) ecsManager.getComponent(entityID, Components.ENEMY_COMPONENT_CODE) != null)
                {
                    ecsManager.fireEvent(ECSEvents.enemyCollisionEvent((Integer)entityID));
                }
                return;
            }
        }

        ecsManager.fireEvent(ECSEvents.collisionEvent(entityCollided));

    }

    /*
    the collision handling differs according to the shapes of bounding volumes.
    */

    public boolean rectRectCollision(float X1, float Y1, float width1, float height1, float X2, float Y2,
                                     float width2, float height2) {
        float middleX1 = X1 + width1 / 2.0f;
        float middleY1 = Y1 + height1 / 2.0f;
        float middleX2 = X2 + width2 / 2.0f;
        float middleY2 = Y2 + height2 / 2.0f;
        boolean UpDownCollision = (Math.abs(middleY1 - middleY2) < height1 / 2 + height2 / 2)
                && (Math.abs(middleX1 - middleX2) < width1 / 2 + width2 / 2);

        boolean LeftRightCollision = (Math.abs(middleX1 - middleX2) < width1 / 2 + width2 / 2)
                && (Math.abs(middleY1 - middleY2) < height1 / 2 + height2 / 2);

        return (LeftRightCollision || UpDownCollision);
    }

    public boolean circRectCollision(float X1, float Y1, float width, float height, float X2, float Y2,
                                     float radius) {

        float middleX1 = X1 + width / 2.0f;
        float middleY1 = Y1 + height / 2.0f;
        float middleX2 = X2 + radius;
        float middleY2 = Y2 + radius;
        boolean upDownCollision = (Math.abs(middleY1 - middleY2) == radius + height / 2.0f)
                && (Math.abs(middleX1 - middleX2) < radius + width / 2.0f);

        boolean leftRightColision = (Math.abs(middleX1 - middleX2) == radius + width / 2.0f)
                && (Math.abs(middleY1 - middleY2) < radius + height / 2.0f);
        return (leftRightColision || upDownCollision);
    }

    public boolean circCircCollision(float X1, float Y1, float radius1, float X2, float Y2,
                                     float radius2) {
        double middleX1 = X1 + radius1;
        double middleY1 = Y1 + radius1;
        double middleX2 = X2 + radius2;
        double middleY2 = Y2 + radius2;

        double dist = Math.pow(middleX1-middleX2,2) + Math.pow(middleY1-middleY2, 2);
        double radSum = Math.pow(radius1 + radius2, 2);
        boolean collided = (radSum <= dist);
        return collided;
    }

}
