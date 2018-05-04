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
    public TextureRegion[] region;
    public boolean isAnimated;
    public int[] indices;
    public int frames;
    public float animationSpeed;

    public GraphicsComponent() {
        super(Components.GRAPHICS_COMPONENT_CODE);
        textureIndex = 0;
        width = 1.02f;
        height = 1.02f;
        isAnimated = false;
        indices = null;
        frames = 0;
        animationSpeed = 1;
    }

    @Override
    public String toString(){
        return "GraphicsComponent - textureName:" + textureName + ", width: " + width +
                ", height: " + height;
    }
}
