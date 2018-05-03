package com.actionteam.geometryadventures.components;

import com.actionteam.geometryadventures.ecs.Component;

/**
 * Created by Ibrahim M. Akrab on 5/3/18.
 * ibrahim.m.akrab@gmail.com
 */
public class CollectibleComponent extends Component {

    public int coin;

    public CollectibleComponent() {
        super(Components.COLLECTIBLE_COMPONENT_CODE);
    }
}
