package com.actionteam.geometryadventures.components;

import com.actionteam.geometryadventures.ecs.Component;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/**
 * Created by theartful on 3/27/18.
 */

public class GraphicsComponent extends Component {
    public String textureName;
    public int textureIndex;
    public float width;
    public float height;
    public Array<TextureAtlas.AtlasRegion> regions;
    public boolean isAnimated;
    public int[] animationSequence;
    public int frames;
    public int interval; // in milliseconds
    public float offsetX;
    public float offsetY;
    public float rotationAngle;
    public boolean rotatable;

    public GraphicsComponent() {
        super(Components.GRAPHICS_COMPONENT_CODE);
        textureIndex = 0;
        width = 1.02f;
        height = 1.02f;
        isAnimated = false;
        animationSequence = null;
        frames = 0;
        interval = 200;
        offsetX = 0;
        offsetY = 0;
        rotatable = false;
    }

    @Override
    public String toString(){
        return "GraphicsComponent - textureName:" + textureName + ", width: " + width +
                ", height: " + height;
    }
}
