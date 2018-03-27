package com.actionteam.geometryadventures;

import android.os.Bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		GameUtils gameUtils = new GameUtils() {
			@Override
			public InputStream openFile(String fileName) throws FileNotFoundException,
					IOException {
				return getAssets().open(fileName);
			}

			@Override
			public File getFile(String fileName) {
				return Gdx.files.internal(fileName).file();
			}
		};
		initialize(new GeometryAdventuresGame(gameUtils), config);
	}
}
