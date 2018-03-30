package com.actionteam.geometryadventures.ecs;


/**
 * Created by ibrahim on 3/18/18.
 */

public interface ECSEventListener {
    boolean handle(int eventCode, Object message);
}
