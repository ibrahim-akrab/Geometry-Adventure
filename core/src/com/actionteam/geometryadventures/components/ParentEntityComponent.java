package com.actionteam.geometryadventures.components;

import com.actionteam.geometryadventures.ecs.Component;

/**
 * Created by Ibrahim M. Akrab on 5/4/18.
 * ibrahim.m.akrab@gmail.com
 */
public class ParentEntityComponent extends Component{

    public int parentEntityId;

    public ParentEntityComponent() {
        super(Components.PARENT_ENTITY_COMPONENT_CODE);
    }
}
