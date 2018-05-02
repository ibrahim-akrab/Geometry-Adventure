package com.actionteam.geometryadventures.systems;
import com.actionteam.geometryadventures.components.Components;
import com.actionteam.geometryadventures.components.SoundComponent;
import com.actionteam.geometryadventures.ecs.ECSEventListener;
import com.actionteam.geometryadventures.ecs.System;
import com.actionteam.geometryadventures.events.ECSEvents;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.*;

/**
 * Created by Omniia - on 27/04/2018.
 */

public class SoundSystem extends System implements ECSEventListener{

    Sound S;
    public SoundSystem() {
        super (Components.SOUND_COMPONENT_CODE);
    }


    @Override
    public void update(float dt) {
     // nothing gets updated, only sounds are played if an event was fired.
    }
    @Override
    public void ecsManagerAttached() {
        ecsManager.subscribe(ECSEvents.ENEMY_DEAD, this);
        ecsManager.subscribe(ECSEvents.PLAYER_DEAD,this);
        ecsManager.subscribe(ECSEvents.LOUD_WEAPON_FIRED_EVENT,this);
    }


    @Override
    public boolean handle(int eventCode, Object message) {
        switch (eventCode) {
            case ECSEvents.LOUD_WEAPON_FIRED_EVENT:
                S = Gdx.audio.newSound(Gdx.files.internal(SoundComponent.WEAPON_FIRED));
                break;
            case ECSEvents.PLAYER_DEAD:
                S = Gdx.audio.newSound(Gdx.files.internal(SoundComponent.PLAYER_DEAD));
                break;
            case ECSEvents.ENEMY_DEAD:
                S = Gdx.audio.newSound(Gdx.files.internal(SoundComponent.ENEMY_DEAD));
                break;
            default: return false;
        }
        play();
        return true;

    }
    void play()
    {
        S.play();
    }
}
