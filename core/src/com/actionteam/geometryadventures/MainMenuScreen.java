package com.actionteam.geometryadventures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Created by rka97 on 4/10/2018.
 */

public class MainMenuScreen implements Screen {
    Stage stage;

    public MainMenuScreen() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        Table table = new Table();
        stage.addActor(table);
        Texture background = new Texture(Gdx.files.internal("main-menu/bg2.png"));
        TextureRegion region = new TextureRegion(background, 0, 0, 512, 512);
        table.setBackground(new TextureRegionDrawable(region));
        table.setFillParent(true);
        table.setDebug(false);
        Texture startUp = new Texture(Gdx.files.internal("main-menu/Start_Down.png"));
        TextureRegion startUpRegion = new TextureRegion(startUp, 0, 0, 512, 512);
        Texture startDown = new Texture(Gdx.files.internal("main-menu/Start_Up.png"));
        TextureRegion startDownRegion = new TextureRegion(startDown, 0, 0, 512, 512);
        Button startButton = new Button(new TextureRegionDrawable(startUpRegion),
                new TextureRegionDrawable(startDownRegion));
        table.add(startButton).maxSize(512*6/10, 254*6/10).right();

        Texture quitUp = new Texture(Gdx.files.internal("main-menu/Quit_Down.png"));
        TextureRegion quitUpRegion = new TextureRegion(quitUp, 0, 0, 512, 512);
        Texture quitDown = new Texture(Gdx.files.internal("main-menu/Quit_Up.png"));
        TextureRegion quitDownRegion = new TextureRegion(quitDown, 0, 0, 512, 512);
        Button quitButton = new Button(new TextureRegionDrawable(quitUpRegion),
                new TextureRegionDrawable(quitDownRegion));
        table.add(quitButton).maxSize(512*6/10, 254*6/10).left();

        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GeometryAdventuresGame.currentScreen = GeometryAdventuresGame.ChosenScreen.SCREEN_GAME_LEVEL;
            }
        });
    }

    @Override
    public void render(float dt) {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void show()
    {

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
        stage = null;
    }
}
