package com.actionteam.geometryadventures.systems;

import com.actionteam.geometryadventures.components.CollisionComponent;
import com.actionteam.geometryadventures.components.Components;
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

/** TODO :: circle collision , naming convention , comments **/

public class CollisionSystem extends System implements ECSEventListener{

    public CollisionSystem() { super(Components.COLLISION_COMPONENT_CODE ,Components.PHYSICS_COMPONENT_CODE);}


    @Override
    public void update(float dt) {

    }

    @Override
    public void ecsManagerAttached() {
        ecsManager.subscribe(ECSEvents.COLLIDABLE_MOVED_EVENT,this);

    }

    @Override
    public boolean handle(int eventCode, Object message) {
        switch (eventCode) {
            case ECSEvents.COLLIDABLE_MOVED_EVENT:
                float [] collisionData = (float[]) message;
                collide(collisionData[0],collisionData[1],collisionData[2],collisionData[3],
                        (int)collisionData[4]);


        }
        return true;
    }

    public void collide (float beginX, float beginY , float endX ,float endY , int entityID)
    {
        CollisionComponent myCc = (CollisionComponent) ecsManager.getComponent(entityID, Components.COLLISION_COMPONENT_CODE);
        PhysicsComponent myPc = (PhysicsComponent) ecsManager.getComponent(entityID, Components.PHYSICS_COMPONENT_CODE);
        CollisionComponent cc;
        PhysicsComponent pc;

        boolean EntityColided = false ;

        for(int e : entities) {
            cc = (CollisionComponent) ecsManager.getComponent(e, Components.COLLISION_COMPONENT_CODE);
            pc = (PhysicsComponent) ecsManager.getComponent(e, Components.PHYSICS_COMPONENT_CODE);

            if(e == entityID || (cc.mask & (1L << myCc.id)) == 0) continue;

            if (myCc.shapeType == CollisionComponent.RECTANGLE) {
                switch (cc.shapeType) {
                    case CollisionComponent.RECTANGLE:
                        EntityColided = rectRectCollision(endX, endY, myCc.width , myCc.height, pc.position.x, pc.position.y,
                               cc.width,cc.height );
                        break;
                    case CollisionComponent.CIRCLE:
                        EntityColided = circRectCollision(pc, myPc, cc, myCc);
                        break;
                }
            } else {
                switch (cc.shapeType) {
                    case CollisionComponent.RECTANGLE:
                        EntityColided = circRectCollision(myPc, pc, myCc, cc);
                        break;
                    case CollisionComponent.CIRCLE:
                        EntityColided = circCircCollision(myPc, pc, myCc, cc);
                        break;
                }
            }

            if (EntityColided) {
                ecsManager.fireEvent(ECSEvents.collisionEvent(EntityColided));
                Gdx.app.log("Collision", entityID + " " + e);
                return;
            }
        }

        ecsManager.fireEvent(ECSEvents.collisionEvent(EntityColided));

    }

    public boolean rectRectCollision(float X1 , float Y1 , float width1 , float height1 , float X2, float Y2,
                                     float width2, float height2)
    {
        float middleX1 = X1 + width1/ 2.0f;
        float middleY1 = Y1 + height1/ 2.0f;
        float middleX2 = X2 + width2 / 2.0f;
        float middleY2 =  Y2+ height2 / 2.0f;
        boolean UpDownCollision = (Math.abs(middleY1 - middleY2) < height1/2+height2/2)
                &&(Math.abs(middleX1- middleX2) < width1/2 + width2/2);

        boolean LeftRightCollision = ( Math.abs(middleX1 - middleX2) < width1/2 + width2/2)
                &&(Math.abs(middleY1 - middleY2) < height1/2 + height2/2 );

        return (LeftRightCollision || UpDownCollision);
    }

    public boolean circRectCollision(PhysicsComponent PC1, PhysicsComponent PC2, CollisionComponent CC1
            , CollisionComponent CC2) {
        boolean upDownCollision = ( Math.abs(PC1.position.y - PC2 .position.y) == CC1.radius + CC2.height/2)
                &&(Math.abs(PC1.position.x - PC2.position.x) < CC1.radius+ CC2.width/2 );

        boolean leftRightColision = ( Math.abs(PC1.position.x - PC2 .position.x) == CC1.radius/2 + CC2.width/2)
                &&(Math.abs(PC1.position.y - PC2.position.y) < CC1.radius + CC2.height/2 );

        return (leftRightColision || upDownCollision);
    }

    public boolean circCircCollision(PhysicsComponent PC1, PhysicsComponent PC2, CollisionComponent CC1
            , CollisionComponent CC2) {
        double dist = Math.pow(PC1.position.x - PC2.position.x, 2) + Math.pow(PC1.position.y - PC2.position.y, 2);
        double radSum = Math.pow(CC1.radius + CC2.radius, 2);
        boolean collided = (radSum <= dist);
        return collided;
    }

    public boolean lineSegmentCollision(float startX, float startY, float endX, float endY)
    {
        for (int e : entities)
        {
            CollisionComponent cc = (CollisionComponent) ecsManager.getComponent(e, Components.COLLISION_COMPONENT_CODE);
            if (!cc.blocksVision)
                continue;
            PhysicsComponent pc = (PhysicsComponent) ecsManager.getComponent(e, Components.PHYSICS_COMPONENT_CODE);
            Vector2 start = new Vector2(startX, startY);
            Vector2 end = new Vector2(endX, endY);
            switch(cc.shapeType) {
                case CollisionComponent.CIRCLE:
                    if (Intersector.intersectSegmentCircle(start, end, pc.position, cc.radius*cc.radius))
                        return true;
                    break;
                case CollisionComponent.RECTANGLE:
                    float xmin = pc.position.x - cc.width/2;
                    float xmax = pc.position.x + cc.width/2;
                    float ymin = pc.position.y - cc.height/2;
                    float ymax = pc.position.y + cc.height/2;
                    // check intersection with all four lines.
                    if (   Intersector.intersectSegments(startX, startY, endX, endY, xmin, ymin, xmin, ymax, null)
                        || Intersector.intersectSegments(startX, startY, endX, endY, xmin, ymin, xmax, ymin, null)
                        || Intersector.intersectSegments(startX, startY, endX, endY, xmin, ymax, xmax, ymax, null)
                        || Intersector.intersectSegments(startX, startY, endX, endY, xmax, ymin, xmax, ymax, null))
                    {
                        return true;
                    }
                    break;
            }

        }
        return false;
    }
}
