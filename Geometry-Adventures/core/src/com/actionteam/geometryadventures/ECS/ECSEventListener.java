package com.actionteam.geometryadventures.ECS;

/**
 * Created by ibrahim on 3/18/18.
 */

public interface ECSEventListener {
    boolean update(int eventCode, Object message);
}
