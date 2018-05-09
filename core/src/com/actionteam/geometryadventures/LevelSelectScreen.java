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
 * Created by rka97 on 5/9/2018.
 */

public class LevelSelectScreen implements Screen {
    Stage stage;

    public LevelSelectScreen() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        Table table = new Table();
        stage.addActor(table);
        Texture background = new Texture(Gdx.files.internal("main-menu/main_menu_full.png"));
        //background.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        TextureRegion region = new TextureRegion(background);
        table.setBackground(new TextureRegionDrawable(region));
        table.setFillParent(true);
        table.setDebug(false);
        Texture startUp = new Texture(Gdx.files.internal("main-menu/firstLevel_Up.png"));
        TextureRegion startUpRegion = new TextureRegion(startUp);
        Texture startDown = new Texture(Gdx.files.internal("main-menu/firstLevel_Down.png"));
        TextureRegion startDownRegion = new TextureRegion(startDown);
        Button startButton = new Button(new TextureRegionDrawable(startUpRegion),
                new TextureRegionDrawable(startDownRegion));
        table.row();
        table.add(startButton).center().expand();
        


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
