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
 */

public class CacheSystem extends System implements ECSEventListener {

    private final static int INNER_RADIUS = 15;
    private final static int OUTER_RADIUS = 30;

    private float centerX;
    private float centerY;

    private List<CompEnt> entityList;

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

    @Override
    protected void entityAdded(int entityId) {
        PhysicsComponent pc = (PhysicsComponent) ecsManager.getComponent(entityId,
                Components.PHYSICS_COMPONENT_CODE);
        CacheComponent cc = (CacheComponent) ecsManager.getComponent(entityId,
                Components.CACHE_COMPONENT_CODE);
        CompEnt ent = new CompEnt(pc, cc, entityId);
        entityList.add(ent);
        if (Math.abs(ent.pc.position.x - centerX) < OUTER_RADIUS &&
                Math.abs(ent.pc.position.y - centerY) < OUTER_RADIUS) {
            ent.cc.isCached = true;
        } else {
            ent.cc.isCached = false;
        }

    }

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

    private void checkPosition(float x, float y) {
        if (Math.abs(x - centerX) > INNER_RADIUS || Math.abs(y - centerY) > INNER_RADIUS) {
            centerX = x;
            centerY = y;
            updateCache();
        }
    }

    private void updateCache() {
        for (CompEnt e : entityList) {
            if (Math.abs(e.pc.position.x - centerX) < OUTER_RADIUS &&
                    Math.abs(e.pc.position.y - centerY) < OUTER_RADIUS) {
                e.cc.isCached = true;
            } else {
                e.cc.isCached = false;
            }
        }
    }

}
