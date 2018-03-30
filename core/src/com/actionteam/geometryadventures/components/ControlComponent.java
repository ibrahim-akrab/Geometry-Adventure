package com.actionteam.geometryadventures.components;

import com.actionteam.geometryadventures.ecs.Component;

/**
 * Created by theartful on 3/27/18.
 */

public class ControlComponent extends Component {

    public float leftBigCircleRadius;
    public float leftInitialX;
    public float leftInitialY;
    public float leftX;
    public float leftY;
    public boolean isLeftTouchDown;

    public float rightBigCircleRadius;
    public float rightInitialX;
    public float rightInitialY;
    public float rightX;
    public float rightY;
    public boolean isRightTouchDown;

    public float maximumSpeed;

    public ControlComponent() {
        super(Components.CONTROL_COMPONENT_CODE);
        isLeftTouchDown = false;
        isRightTouchDown = false;
        maximumSpeed = 3.5f;
    }
}
