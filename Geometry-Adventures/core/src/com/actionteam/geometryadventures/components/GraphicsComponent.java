package com.actionteam.geometryadventures.components;

import com.actionteam.geometryadventures.ecs.Component;

/**
 * Created by theartful on 3/27/18.
 */

public class GraphicsComponent extends Component {
    public String textureName;
    public int textureIndex;
    public float width;
    public float height;

    public GraphicsComponent() {
        super(Components.GRAPHICS_COMPONENT_CODE);
    }

    @Override
    public String toString(){
        return "GraphicsComponent - textureName:" + textureName + ", width: " + width +
                ", height: " + height;
    }
}
