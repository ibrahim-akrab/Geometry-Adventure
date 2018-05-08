package com.actionteam.geometryadventures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1024;
		config.height = 512;
		new LwjglApplication(new GeometryAdventuresGame(new GameUtils() {
			@Override
			public FileInputStream openFile(String fileName) throws FileNotFoundException {
				return new FileInputStream(Gdx.files.internal(fileName).file());
			}

			@Override
			public File getFile(String fileName) {
				return Gdx.files.internal(fileName).file();
			}
		}), config);
	}
}
