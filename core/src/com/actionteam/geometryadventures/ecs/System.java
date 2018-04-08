package com.actionteam.geometryadventures.ecs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ibrahim on 3/18/18.
 */

public abstract class System {
    protected List<Integer> entities;
    protected final long componentsMask;
    protected long eventsMask;
    protected ECSManager ecsManager;

    public System(int... componentCodes){
        long tmpMask = 0;
        for(int componentCode : componentCodes){
            tmpMask |= (1L << componentCode);
        }
        componentsMask = tmpMask;
        entities = new ArrayList<Integer>();
    }

    void setEcsManager(ECSManager ecsManager){
        this.ecsManager = ecsManager;
        ecsManagerAttached();
    }

    protected void ecsManagerAttached(){}

    private void subscribe(int eventCode, ECSEventListener listener){
        eventsMask |= (1L << eventCode);
        ecsManager.subscribe(eventCode, listener);
    }

    boolean addEntity(int entityId){
        for(int id : entities){
            if(entityId == id) return false;
        }
        entityAdded(entityId);
        return entities.add(entityId);
    }

    protected void entityAdded(int entityId){}

    boolean removeEntity(int entityId){
        for(Iterator<Integer> iterator = entities.iterator(); iterator.hasNext();){
            int id = iterator.next();
            if(id == entityId){
                entityRemoved(id);
                return entities.remove(Integer.valueOf(id));
            }
        }
        return false;
    }

    protected void entityRemoved(int entityId){}

    long getComponentsMask(){
        return componentsMask;
    }

    private void fireEvent(int eventCode, Object message){
        ECSEvent event = new ECSEvent(eventCode, message);
        ecsManager.fireEvent(event);
    }

    public abstract void update(float dt);
}
