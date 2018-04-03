package com.actionteam.geometryadventures.components;

import com.badlogic.gdx.graphics.g3d.particles.ParticleSorter;

import java.util.ArrayList;

/**
 * Created by Ibrahim on 3/29/18.
 */

public class WeaponFactory {

    public static WeaponComponent createWeapon(int weaponType){
        switch (weaponType){
            case WeaponComponent.MELEE:
                return createMelee();
            case WeaponComponent.KNIFE:
                return createKnife();
            case WeaponComponent.SWORD:
                return createSword();
            case WeaponComponent.RIFLE:
                return createRifle();
            case WeaponComponent.RIOT_GUN:
                return createRiotGun();
            case WeaponComponent.HAND_GUN:
                return createHandGun();
            default:
                return null;
        }
    }

    public static WeaponComponent createMelee(){
        WeaponComponent weaponComponent = new WeaponComponent();
        weaponComponent.weaponType = WeaponComponent.MELEE;
        weaponComponent.magazineSize = 0;
        weaponComponent.damage = 1;
        weaponComponent.weaponDamageRegion = WeaponComponent.SEMICIRCLE;
        weaponComponent.weaponDamageRegionParameters = new ArrayList<Integer>();
        weaponComponent.weaponDamageRegionParameters.add(5);    // radius of the semicircle
        weaponComponent.cooldownTime = 2;
        return weaponComponent;
    }

    public static WeaponComponent createKnife(){
        WeaponComponent weaponComponent = new WeaponComponent();
        weaponComponent.weaponType = WeaponComponent.KNIFE;
        weaponComponent.magazineSize = 0;
        weaponComponent.damage = 2;
        weaponComponent.weaponDamageRegion = WeaponComponent.SEMICIRCLE;
        weaponComponent.weaponDamageRegionParameters = new ArrayList<Integer>();
        weaponComponent.weaponDamageRegionParameters.add(5);    // radius of the semicircle
        weaponComponent.cooldownTime = 2;
        return weaponComponent;
    }

    public static WeaponComponent createSword(){
        WeaponComponent weaponComponent = new WeaponComponent();
        weaponComponent.weaponType = WeaponComponent.SWORD;
        weaponComponent.magazineSize = 0;
        weaponComponent.damage = 2;
        weaponComponent.weaponDamageRegion = WeaponComponent.SEMICIRCLE;
        weaponComponent.weaponDamageRegionParameters = new ArrayList<Integer>();
        weaponComponent.weaponDamageRegionParameters.add(15);    // radius of the semicircle
        weaponComponent.cooldownTime = 4;
        return weaponComponent;
    }

    public static WeaponComponent createRifle(){
        WeaponComponent weaponComponent = new WeaponComponent();
        weaponComponent.weaponType = WeaponComponent.RIFLE;
        weaponComponent.magazineSize = 4;
        weaponComponent.damage = 2;
        weaponComponent.weaponDamageRegion = WeaponComponent.LINE;
        weaponComponent.cooldownTime = 5;
        return weaponComponent;
    }

    public static WeaponComponent createRiotGun(){
        WeaponComponent weaponComponent = new WeaponComponent();
        weaponComponent.weaponType = WeaponComponent.RIOT_GUN;
        weaponComponent.magazineSize = 6;
        weaponComponent.damage = 2;
        weaponComponent.weaponDamageRegion = WeaponComponent.LINES;
        weaponComponent.weaponDamageRegionParameters = new ArrayList<Integer>();
        weaponComponent.weaponDamageRegionParameters.add(4);    // number of lines of damage
        weaponComponent.cooldownTime = 5;
        return weaponComponent;
    }
    public static WeaponComponent createHandGun(){
        WeaponComponent weaponComponent = new WeaponComponent();
        weaponComponent.weaponType = WeaponComponent.HAND_GUN;
        weaponComponent.magazineSize = 24;
        weaponComponent.damage = 2;
        weaponComponent.weaponDamageRegion = WeaponComponent.LINE;
        weaponComponent.cooldownTime = 4;
        return weaponComponent;
    }

}
