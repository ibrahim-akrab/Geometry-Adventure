package com.actionteam.geometryadventures.components;

import com.actionteam.geometryadventures.ecs.Component;

import java.util.ArrayList;

/**
 * Created by theartful on 3/27/18.
 */

public class CollisionComponent extends Component {
    public static final int CIRCLE = 0;
    public static final int RECTANGLE = 1;
    public static boolean blocksVision = true;

    public int shapeType;
    public float width;
    public float height;
    public float radius;

    public long mask;
    public int id;

    public CollisionComponent(int... ids) {
        super(Components.COLLISION_COMPONENT_CODE);
        for (int id : ids) {
            mask |= (1L << id);
        }
    }

}
