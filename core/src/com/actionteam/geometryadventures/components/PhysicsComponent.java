package com.actionteam.geometryadventures.components;

import com.actionteam.geometryadventures.ecs.Component;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by theartful on 3/27/18.
 */

public class PhysicsComponent extends Component {
    public Vector2 position;
    public Vector2 velocity;
    public Vector2 acceleration;
    public float rotationAngle;
    public Vector2 angularAcceleration;
    public Vector2 centerOfRotation;
    public boolean isFreezed;

    public PhysicsComponent() {
        super(Components.PHYSICS_COMPONENT_CODE);
        position = new Vector2(0, 0);
        velocity = new Vector2(0, 0);
        acceleration = new Vector2(0, 0);
        angularAcceleration = new Vector2(0, 0);
        rotationAngle = 0;
        centerOfRotation = new Vector2(0, 0);
        isFreezed = false;
    }
}
