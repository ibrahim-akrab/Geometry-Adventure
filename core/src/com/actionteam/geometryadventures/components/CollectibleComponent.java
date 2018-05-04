package com.actionteam.geometryadventures.components;

import com.actionteam.geometryadventures.ecs.Component;

/**
 * Created by Ibrahim M. Akrab on 5/3/18.
 * ibrahim.m.akrab@gmail.com
 */
public class CollectibleComponent extends Component {

    public static final int COIN    = 0;
    public static final int HEART   = 1;
    public static final int KEY     = 2;


    public int type;
    public int value;

    public CollectibleComponent() {
        super(Components.COLLECTIBLE_COMPONENT_CODE);
    }
}
