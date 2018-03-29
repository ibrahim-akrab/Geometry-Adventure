package com.actionteam.geometryadventures.components;

import com.actionteam.geometryadventures.ecs.Component;

import java.util.List;

/**
 * Created by ibrahim on 3/29/18.
 */

public class WeaponComponent extends Component {
    // weapon types
    public static final int MELEE= 0;
    public static final int KNIFE = 1;
    public static final int SWORD = 2;
    public static final int RIFLE = 3;
    public static final int RIOT_GUN = 4;
    public static final int HAND_GUN = 5;

    // weapon damage region types
    public static final int LINE = 0;
    public static final int LINES = 1;
    public static final int SEMICIRCLE= 2;

    public int magazineSize;
    public int currentMagazine;
    public int currentAmmo;
    public int damage;
    public int weaponType;
    public int weaponDamageRegion;
    public List<Integer> weaponDamageRegionParameters;
    public int timeOfLastFire;
    public int cooldownTime;


    public WeaponComponent() {
        super(Components.WEAPON_COMPONENT_CODE);
    }
}
