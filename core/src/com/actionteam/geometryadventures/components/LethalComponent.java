package com.actionteam.geometryadventures.components;

import com.actionteam.geometryadventures.ecs.Component;

/**
 * Created by ibrahim on 3/29/18.
 */

public class LethalComponent extends Component {
    public int damage;
    public int owner;

    public LethalComponent() {
        super(Components.LETHAL_COMPONENT_CODE);
    }
}
