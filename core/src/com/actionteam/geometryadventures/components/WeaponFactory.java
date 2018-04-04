package com.actionteam.geometryadventures.components;

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
        weaponComponent.currentMagazine = weaponComponent.magazineSize;
        weaponComponent.currentAmmo= weaponComponent.magazineSize;
        weaponComponent.damage = 1;
        weaponComponent.weaponDamageRegion = WeaponComponent.SEMICIRCLE;
        weaponComponent.radius = 5;
        weaponComponent.coolDownTime = 2;
        return weaponComponent;
    }

    public static WeaponComponent createKnife(){
        WeaponComponent weaponComponent = new WeaponComponent();
        weaponComponent.weaponType = WeaponComponent.KNIFE;
        weaponComponent.magazineSize = 0;
        weaponComponent.currentMagazine = weaponComponent.magazineSize;
        weaponComponent.currentAmmo= weaponComponent.magazineSize;
        weaponComponent.damage = 2;
        weaponComponent.weaponDamageRegion = WeaponComponent.SEMICIRCLE;
        weaponComponent.radius = 5;
        weaponComponent.coolDownTime = 2;
        return weaponComponent;
    }

    public static WeaponComponent createSword(){
        WeaponComponent weaponComponent = new WeaponComponent();
        weaponComponent.weaponType = WeaponComponent.SWORD;
        weaponComponent.magazineSize = 0;
        weaponComponent.currentMagazine = weaponComponent.magazineSize;
        weaponComponent.currentAmmo= weaponComponent.magazineSize;
        weaponComponent.damage = 2;
        weaponComponent.weaponDamageRegion = WeaponComponent.SEMICIRCLE;
        weaponComponent.radius = 15;
        weaponComponent.coolDownTime = 4;
        return weaponComponent;
    }

    public static WeaponComponent createRifle(){
        WeaponComponent weaponComponent = new WeaponComponent();
        weaponComponent.weaponType = WeaponComponent.RIFLE;
        weaponComponent.magazineSize = 4;
        weaponComponent.currentMagazine = weaponComponent.magazineSize;
        weaponComponent.currentAmmo= weaponComponent.magazineSize;
        weaponComponent.damage = 2;
        weaponComponent.weaponDamageRegion = WeaponComponent.CIRCLE;
        weaponComponent.numberOfLethalObjectsAtTime = 3;
        weaponComponent.coolDownTime = 500;
        weaponComponent.speed = 20;
        return weaponComponent;
    }

    public static WeaponComponent createRiotGun(){
        WeaponComponent weaponComponent = new WeaponComponent();
        weaponComponent.weaponType = WeaponComponent.RIOT_GUN;
        weaponComponent.magazineSize = 6;
        weaponComponent.currentMagazine = weaponComponent.magazineSize;
        weaponComponent.currentAmmo= weaponComponent.magazineSize;
        weaponComponent.damage = 2;
        weaponComponent.weaponDamageRegion = WeaponComponent.CIRCLE;
        weaponComponent.numberOfLethalObjectsAtTime = 5;
        weaponComponent.coolDownTime = 500;
        weaponComponent.speed = 20;
        return weaponComponent;
    }

    public static WeaponComponent createHandGun(){
        WeaponComponent weaponComponent = new WeaponComponent();
        weaponComponent.weaponType = WeaponComponent.HAND_GUN;
        weaponComponent.magazineSize = 24;
        weaponComponent.currentMagazine = weaponComponent.magazineSize;
        weaponComponent.currentAmmo= weaponComponent.magazineSize;
        weaponComponent.damage = 2;
        weaponComponent.weaponDamageRegion = WeaponComponent.CIRCLE;
        weaponComponent.coolDownTime = 400;
        weaponComponent.speed = 20;
        return weaponComponent;
    }

}
