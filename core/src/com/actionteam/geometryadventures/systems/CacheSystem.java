package com.actionteam.geometryadventures.systems;

import com.actionteam.geometryadventures.components.CacheComponent;
import com.actionteam.geometryadventures.components.Components;
import com.actionteam.geometryadventures.components.PhysicsComponent;
import com.actionteam.geometryadventures.ecs.ECSEventListener;
import com.actionteam.geometryadventures.ecs.System;
import com.actionteam.geometryadventures.events.ECSEvents;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by theartful on 5/8/18.
 * This system is used to improve performance by disabling all entities that
 * are out of the screen
 */

public class CacheSystem extends System implements ECSEventListener {

    // rendered area radius
    private final static int INNER_RADIUS = 15;
    private final static int OUTER_RADIUS = 30;

    // center of the screen
    private float centerX;
    private float centerY;

    // list of entities that has the cache component
    private List<CompEnt> entityList;

    // basic struct to hold entities
    private class CompEnt {
        PhysicsComponent pc;
        CacheComponent cc;
        int entity;

        CompEnt(PhysicsComponent pc, CacheComponent cc,
                int entity) {
            this.pc = pc;
            this.cc = cc;
            this.entity = entity;
        }
    }

    /**
     * Cache system constructor
     *
     * @param initialX the x coordinate of the center of the screen
     * @param initialY the y coordinate of the center of the screen
     */
    public CacheSystem(float initialX, float initialY) {
        super(Components.CACHE_COMPONENT_CODE, Components.PHYSICS_COMPONENT_CODE);
        entityList = new ArrayList<CompEnt>();
        centerX = initialX;
        centerY = initialY;
    }

    @Override
    protected void ecsManagerAttached() {
        // subscribe to events
        ecsManager.subscribe(ECSEvents.PLAYER_MOVED_EVENT, this);
    }

    /**
     * Stores the cache component of the entity for further usage instead of querying the
     * ecsManager each time they are needed
     *
     * @param entityId the id of the new entity
     */
    @Override
    protected void entityAdded(int entityId) {
        PhysicsComponent pc = (PhysicsComponent) ecsManager.getComponent(entityId,
                Components.PHYSICS_COMPONENT_CODE);
        CacheComponent cc = (CacheComponent) ecsManager.getComponent(entityId,
                Components.CACHE_COMPONENT_CODE);
        CompEnt ent = new CompEnt(pc, cc, entityId);
        entityList.add(ent);
        ent.cc.isCached = Math.abs(ent.pc.position.x - centerX) < OUTER_RADIUS &&
                Math.abs(ent.pc.position.y - centerY) < OUTER_RADIUS;
    }

    /**
     * removes the removed entity from the list
     * @param entityId the id of the removed entity
     * @param index the index of the removed entity
     */
    @Override
    protected void entityRemoved(int entityId, int index) {
        entityList.remove(index);
    }


    @Override
    public void update(float dt) {
    }


    @Override
    public boolean handle(int eventCode, Object message) {
        switch (eventCode) {
            case ECSEvents.PLAYER_MOVED_EVENT:
                float[] position = (float[]) message;
                checkPosition(position[0], position[1]);
        }
        return true;
    }

    /**
     * checks the position of the player to see whether or not the cached entities should
     * be updated
     * @param x the x coordinate of the position of the player
     * @param y the y coordinate of the position of the player
     */
    private void checkPosition(float x, float y) {
        if (Math.abs(x - centerX) > INNER_RADIUS || Math.abs(y - centerY) > INNER_RADIUS) {
            centerX = x;
            centerY = y;
            updateCache();
        }
    }

    /**
     * updates cached entities
     */
    private void updateCache() {
        for (CompEnt e : entityList) {
            e.cc.isCached = Math.abs(e.pc.position.x - centerX) < OUTER_RADIUS &&
                    Math.abs(e.pc.position.y - centerY) < OUTER_RADIUS;
        }
    }

}
