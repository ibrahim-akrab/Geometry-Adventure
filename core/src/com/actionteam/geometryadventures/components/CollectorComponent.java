package com.actionteam.geometryadventures.components;

import com.actionteam.geometryadventures.ecs.Component;

/**
 * Created by Ibrahim M. Akrab on 5/3/18.
 * ibrahim.m.akrab@gmail.com
 */
public class CollectorComponent extends Component {

    public int coins;

    public CollectorComponent() {
        super(Components.COLLECTOR_COMPONENT_CODE);
        coins = 0;
    }
}
