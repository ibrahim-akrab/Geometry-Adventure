package com.actionteam.geometryadventures.model;

import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Created by theartful on 3/27/18.
 */

public class LevelLoader {

    public static Map loadLevel(String levelName){
        try {
            File file = Gdx.files.internal(levelName).file();
            if(!file.exists()) throw new FileNotFoundException();
            Gson gson = new Gson();
            return gson.fromJson(new FileReader(file), Map.class);
        } catch (FileNotFoundException e) {
            Gdx.app.log("Error in Level Loader", "File not found");
        }
        return null;
    }
}
