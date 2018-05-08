package com.actionteam.geometryadventures.components;

import com.actionteam.geometryadventures.ecs.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by theartful on 5/5/18.
 */

public class LightComponent extends Component {

    public float radius;
    public float lightIntensity;

    public LightComponent() {
        super(Components.LIGHT_COMPONENT_CODE);
        lightIntensity = 1;
        radius = 0.5f;
    }
}
