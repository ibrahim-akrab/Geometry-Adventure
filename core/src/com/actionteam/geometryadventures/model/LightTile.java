package com.actionteam.geometryadventures.model;

import com.badlogic.gdx.math.Vector3;

/**
 * Created by theartful on 5/6/18.
 */

public class LightTile extends Tile {
    public Vector3 lightColor;
    public float innerRadius;
    public float outerRadius;
    public float lightIntensity;

    public LightTile() {
        lightColor = new Vector3(1, 1, 1);
        innerRadius = 2;
        outerRadius = 3;
        lightIntensity = 0.7f;
    }
}
