package com.actionteam.geometryadventures.ecs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ibrahim on 3/18/18.
 */

/**
 * Systems are the logic part, they perform action on every Entity that the system is interested in
 * A system is interested in every entity that has ALL of the components specified by the mask
 */

public abstract class System {
    // a 64-bit mask where the ith bit represents the interest in the component with the code i
    private final long componentsMask;
    // a list of all the entities that the system is interested in
    protected List<Integer> entities;
    protected ECSManager ecsManager;

    /**
     * Initializes the system
     *
     * @param componentCodes A list of the components that the system is interested in
     */
    public System(int... componentCodes) {
        long tmpMask = 0;
        for (int componentCode : componentCodes) {
            tmpMask |= (1L << componentCode);
        }
        componentsMask = tmpMask;
        entities = new ArrayList<Integer>();
        setEcsManager();
    }

    void setEcsManager() {
        this.ecsManager = ECSManager.getInstance();
        ecsManagerAttached();
    }

    protected abstract void ecsManagerAttached();

    /**
     * Adds an entity to the system
     *
     * @return true if successful, false otherwise
     */
    boolean addEntity(int entityId) {
        for (int id : entities) {
            if (entityId == id) return false;
        }
        entityAdded(entityId);
        return entities.add(entityId);
    }

    protected void entityAdded(int entityId) {
    }

    /**
     * Removes an entity from the system
     *
     * @return true if successful, false otherwise
     */
    boolean removeEntity(int entityId) {
        int index = 0;
        for (Iterator<Integer> iterator = entities.iterator(); iterator.hasNext(); ) {
            int id = iterator.next();
            if (id == entityId) {
                entityRemoved(id, index);
                iterator.remove();
                return true;
            }
            index++;
        }
        return false;
    }

    protected void entityRemoved(int entityId, int index) {
    }

    /**
     * Decides whether the system is interested in an entity with the components specified by
     * the parameter @entityComponentMask or not
     *
     * @param entityComponentsMask The components mask of the entity
     * @return true if interested, false otherwise
     */
    boolean isInterested(long entityComponentsMask) {
        return (entityComponentsMask & componentsMask) == componentsMask;
    }

    public abstract void update(float dt);
}
