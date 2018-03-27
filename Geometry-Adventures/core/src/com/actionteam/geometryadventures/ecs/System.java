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

    public void setEcsManager(ECSManager ecsManager){
        this.ecsManager = ecsManager;
    }

    private void subscribe(int eventCode, ECSEventListener listener){
        eventsMask |= (1L << eventCode);
        ecsManager.subscribe(eventCode, listener);
    }

    public boolean addEntity(int entityId){
        for(int id : entities){
            if(entityId == id) return false;
        }
        return entities.add(entityId);
    }

    public boolean removeEntity(int entityId){
        for(Iterator<Integer> iterator = entities.iterator(); iterator.hasNext();){
            int id = iterator.next();
            if(id == entityId){
                return entities.remove(Integer.valueOf(id));
            }
        }
        return false;
    }

    public long getComponentsMask(){
        return componentsMask;
    }

    private void fireEvent(int eventCode, Object message){
        ECSEvent event = new ECSEvent(eventCode, message);
        ecsManager.fireEvent(event);
    }

    public abstract void update(float dt);
}
