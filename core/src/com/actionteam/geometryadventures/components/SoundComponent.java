package com.actionteam.geometryadventures.components;
import com.actionteam.geometryadventures.ecs.Component;
import com.badlogic.gdx.audio.Sound;

/**
 * Created by Omniia - on 27/04/2018.
 */

public class SoundComponent extends Component {

  public SoundComponent ()
   {
        super(Components.SOUND_COMPONENT_CODE);

   }
//    public String getFilePath() { return filePath; }
//    public float getVolume() { return volume; }
//    public void setFilePath(String filePath) { this.filePath = filePath; }
//    public void setVolume(float volume ){ this.volume = volume <=0 ? 5.0f : volume; }
    public static final String WEAPON_FIRED= "0953.ogg";
    public static final String PLAYER_DEAD ="0953.ogg";
    public static final String ENEMY_DEAD ="0953.ogg";





}
