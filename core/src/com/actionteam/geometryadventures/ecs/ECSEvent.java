package com.actionteam.geometryadventures.ecs;

/**
 * Created by ibrahim on 3/18/18.
 */

public class ECSEvent {
    public int eventCode;
    public Object message;

    public ECSEvent(int eventCode, Object message){
        this.eventCode = eventCode;
        this.message = message;
    }
}
