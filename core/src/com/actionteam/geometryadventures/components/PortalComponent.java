package com.actionteam.geometryadventures.components;
import com.actionteam.geometryadventures.ecs.Component;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Omniia- on 03/05/2018.
 */

public class PortalComponent extends Component {

    // position of portal we're arriving at.
    public Vector2 position;
    public PortalComponent() {
        super(Components.PORTAL_COMPONENT_CODE);
        position = new Vector2(0,0);

    }
}
