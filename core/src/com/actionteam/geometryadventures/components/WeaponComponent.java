package com.actionteam.geometryadventures.components;

import com.actionteam.geometryadventures.ecs.Component;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.List;

/**
 * Created by ibrahim on 3/29/18.
 */

public class WeaponComponent extends Component {
    // weapon types
    public static final int MELEE       = 0;
    public static final int KNIFE       = 1;
    public static final int SWORD       = 2;
    public static final int RIFLE       = 3;
    public static final int RIOT_GUN    = 4;
    public static final int HAND_GUN    = 5;

    // weapon damage region types
    public static final int CIRCLE      = 0;
    public static final int SEMICIRCLE  = 1;

    public int weaponType;              // for graphics and drawing purposes
    public int magazineSize;            // maximum number of ammo
    public int currentMagazine;         // number of shots before reloading (if any)
    public int currentAmmo;             // number of bullets ready to be reloaded
    public int damage;
    public int weaponDamageRegion;
    public int numberOfLethalObjectsAtTime;
    public float radius;                // radius of the hit (if applicable)
    public long timeOfLastFire;
    public long coolDownTime;
    public float speed;
    public float angleOfSpreading;


    public WeaponComponent() {
        super(Components.WEAPON_COMPONENT_CODE);
        timeOfLastFire = TimeUtils.millis();
        speed = 0;
        numberOfLethalObjectsAtTime = 1;
        angleOfSpreading = 0.1f;
    }
}
