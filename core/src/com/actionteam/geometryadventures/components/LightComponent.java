package com.actionteam.geometryadventures.components;

import com.actionteam.geometryadventures.ecs.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by theartful on 5/5/18.
 */

public class LightComponent extends Component {

    public Vector2 lightPosition;
    public Vector3 lightColor;
    public Vector2 radius;

    public LightComponent() {
        super(Components.LIGHT_COMPONENT_CODE);
        lightPosition = new Vector2();
        lightColor = new Vector3();
        radius = new Vector2();
    }
}
