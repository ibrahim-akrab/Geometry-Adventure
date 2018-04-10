package com.actionteam.geometryadventures.components;

import com.actionteam.geometryadventures.ecs.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by theartful on 3/27/18.
 */

public class GraphicsComponent extends Component {
    public String textureName;
    public int textureIndex;
    public float width;
    public float height;
    public TextureRegion region;

    public GraphicsComponent() {
        super(Components.GRAPHICS_COMPONENT_CODE);
        textureIndex = 0;
        width = 1.05f;
        height = 1.05f;
    }

    @Override
    public String toString(){
        return "GraphicsComponent - textureName:" + textureName + ", width: " + width +
                ", height: " + height;
    }
}
