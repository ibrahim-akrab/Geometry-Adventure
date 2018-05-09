package com.actionteam.geometryadventures.components;

import com.actionteam.geometryadventures.ecs.Component;

/**
 * Created by Ibrahim M. Akrab on 5/9/18.
 * ibrahim.m.akrab@gmail.com
 */
public class CollectorComponent extends Component {
    public int coinCount = 0;
    public CollectorComponent() {
        super(Components.COLLECTOR_COMPONENT_CODE);
    }
}
