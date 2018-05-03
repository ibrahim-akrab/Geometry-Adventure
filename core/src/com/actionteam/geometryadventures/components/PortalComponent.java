package com.actionteam.geometryadventures.components;
import com.actionteam.geometryadventures.ecs.Component;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Omniia- on 03/05/2018.
 */

public class PortalComponent extends Component {

    // position of portal we're ariving at.
    public Vector2 position;
    public PortalComponent(float x,float y) {
        super(Components.PORTAL_COMPONENT_CODE);
        position = new Vector2(x,y);

    }
}
