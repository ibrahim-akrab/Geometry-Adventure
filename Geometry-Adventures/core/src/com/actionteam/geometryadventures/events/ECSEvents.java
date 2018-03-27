package com.actionteam.geometryadventures.events;

import com.actionteam.geometryadventures.ecs.ECSEvent;

/**
 * Created by theartful on 3/27/18.
 */

public class ECSEvents {
    public static final int RESIZE_EVENT_CODE = 0;

    public static ECSEvent resizeEvent(int width, int height){
        return new ECSEvent(RESIZE_EVENT_CODE, new int[]{width, height});
    }

}
