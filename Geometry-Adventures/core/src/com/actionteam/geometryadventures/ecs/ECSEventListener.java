package com.actionteam.geometryadventures.ecs;

/**
 * Created by ibrahim on 3/18/18.
 */

public interface ECSEventListener {
    boolean update(int eventCode, Object message);
}
