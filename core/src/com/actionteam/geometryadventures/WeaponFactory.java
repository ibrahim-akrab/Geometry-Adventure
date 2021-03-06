package com.actionteam.geometryadventures;

import com.actionteam.geometryadventures.components.WeaponComponent;

/**
 * Created by Ibrahim on 3/29/18.
 */

public class WeaponFactory {

    public static WeaponComponent createWeapon(int weaponType) {
        switch (weaponType) {
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

    public static WeaponComponent createMelee() {
        WeaponComponent weaponComponent = new WeaponComponent();
        weaponComponent.weaponType = WeaponComponent.MELEE;
        weaponComponent.magazineSize = 0;
        weaponComponent.currentMagazine = weaponComponent.magazineSize;
        weaponComponent.currentAmmo = weaponComponent.magazineSize;
        weaponComponent.damage = 1;
        weaponComponent.weaponDamageRegion = WeaponComponent.SEMICIRCLE;
        weaponComponent.radiusOfDamageRegion = 1;
        weaponComponent.angleOfSpreading = 0.75f;
        weaponComponent.radius = 0.2f;
        weaponComponent.coolDownTime = 500;
        weaponComponent.speed = 10;
        weaponComponent.lifetimeOfLethalObject = 100;
        weaponComponent.hasGraphics = false;
        return weaponComponent;
    }

    public static WeaponComponent createKnife() {
        WeaponComponent weaponComponent = new WeaponComponent();
        weaponComponent.weaponType = WeaponComponent.KNIFE;
        weaponComponent.magazineSize = 0;
        weaponComponent.currentMagazine = weaponComponent.magazineSize;
        weaponComponent.currentAmmo = weaponComponent.magazineSize;
        weaponComponent.damage = 2;
        weaponComponent.weaponDamageRegion = WeaponComponent.SEMICIRCLE;
        weaponComponent.radiusOfDamageRegion = 1;
        weaponComponent.angleOfSpreading = 0.75f;
        weaponComponent.radius = 0.3f;
        weaponComponent.coolDownTime = 500;
        weaponComponent.speed = 10;
        weaponComponent.lifetimeOfLethalObject = 100;
        return weaponComponent;
    }

    public static WeaponComponent createSword() {
        WeaponComponent weaponComponent = new WeaponComponent();
        weaponComponent.weaponType = WeaponComponent.SWORD;
        weaponComponent.magazineSize = 0;
        weaponComponent.currentMagazine = weaponComponent.magazineSize;
        weaponComponent.currentAmmo = weaponComponent.magazineSize;
        weaponComponent.damage = 2;
        weaponComponent.weaponDamageRegion = WeaponComponent.SEMICIRCLE;
        weaponComponent.radiusOfDamageRegion = 1.3f;
        weaponComponent.radius = 0.4f;
        weaponComponent.coolDownTime = 700;
        weaponComponent.speed = 10;
        weaponComponent.lifetimeOfLethalObject = 150;
        return weaponComponent;
    }

    public static WeaponComponent createRifle() {
        WeaponComponent weaponComponent = new WeaponComponent();
        weaponComponent.weaponType = WeaponComponent.RIFLE;
        weaponComponent.magazineSize = 4;
        weaponComponent.currentMagazine = weaponComponent.magazineSize;
        weaponComponent.currentAmmo = weaponComponent.magazineSize;
        weaponComponent.damage = 2;
        weaponComponent.weaponDamageRegion = WeaponComponent.CIRCLE;
        weaponComponent.numberOfLethalObjectsAtTime = 3;
        weaponComponent.radius = 0.15f;
        weaponComponent.coolDownTime = 500;
        weaponComponent.speed = 20;
        weaponComponent.lifetimeOfLethalObject = 1000;
        return weaponComponent;
    }

    public static WeaponComponent createRiotGun() {
        WeaponComponent weaponComponent = new WeaponComponent();
        weaponComponent.weaponType = WeaponComponent.RIOT_GUN;
        weaponComponent.magazineSize = 6;
        weaponComponent.currentMagazine = weaponComponent.magazineSize;
        weaponComponent.currentAmmo = weaponComponent.magazineSize;
        weaponComponent.damage = 2;
        weaponComponent.weaponDamageRegion = WeaponComponent.CIRCLE;
        weaponComponent.numberOfLethalObjectsAtTime = 5;
        weaponComponent.radius = 0.15f;
        weaponComponent.coolDownTime = 500;
        weaponComponent.speed = 20;
        weaponComponent.lifetimeOfLethalObject = 1000;
        return weaponComponent;
    }

    public static WeaponComponent createHandGun() {
        WeaponComponent weaponComponent = new WeaponComponent();
        weaponComponent.weaponType = WeaponComponent.HAND_GUN;
        weaponComponent.magazineSize = 24;
        weaponComponent.currentMagazine = weaponComponent.magazineSize;
        weaponComponent.currentAmmo = weaponComponent.magazineSize;
        weaponComponent.damage = 1;
        weaponComponent.weaponDamageRegion = WeaponComponent.CIRCLE;
        weaponComponent.radius = 0.7f;
        weaponComponent.coolDownTime = 800;
        weaponComponent.castTime = 570;
        weaponComponent.speed = 7;
        weaponComponent.lifetimeOfLethalObject = 5000;
        return weaponComponent;
    }

}
