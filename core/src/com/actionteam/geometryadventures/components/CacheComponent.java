package com.actionteam.geometryadventures.components;

import com.actionteam.geometryadventures.ecs.Component;

/**
 * Created by theartful on 5/8/18.
 */

public class CacheComponent extends Component{
    public boolean isCached;

    public CacheComponent() {
        super(Components.CACHE_COMPONENT_CODE);
        isCached = true;
    }
}
