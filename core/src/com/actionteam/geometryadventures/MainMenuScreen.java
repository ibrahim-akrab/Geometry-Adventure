package com.actionteam.geometryadventures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Created by rka97 on 4/10/2018.
 */

public class MainMenuScreen implements Screen {
    Stage stage;

    /**
     *  Constructs the main menu and its buttons.
     */
    public MainMenuScreen() {
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
        Texture startUp = new Texture(Gdx.files.internal("main-menu/Start_Up.png"));
        TextureRegion startUpRegion = new TextureRegion(startUp);
        Texture startDown = new Texture(Gdx.files.internal("main-menu/Start_Down.png"));
        TextureRegion startDownRegion = new TextureRegion(startDown);
        Button startButton = new Button(new TextureRegionDrawable(startUpRegion),
                new TextureRegionDrawable(startDownRegion));
        table.row();
        table.add(startButton).center().bottom().expand();

        Texture quitUp = new Texture(Gdx.files.internal("main-menu/Quit_Up.png"));
        TextureRegion quitUpRegion = new TextureRegion(quitUp);
        Texture quitDown = new Texture(Gdx.files.internal("main-menu/Quit_Down.png"));
        TextureRegion quitDownRegion = new TextureRegion(quitDown);
        Button quitButton = new Button(new TextureRegionDrawable(quitUpRegion),
                new TextureRegionDrawable(quitDownRegion));
        table.row().pad(20.0f);
        table.add(quitButton).center().top().expand();

        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
               GeometryAdventuresGame.currentScreen = GeometryAdventuresGame.ChosenScreen.SCREEN_LEVEL_SELECT;
            }
        });
    }


    /**
     *  Renders to the screen.
     *  @param  dt time step value.
     */
    @Override
    public void render(float dt) {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }


    /**
     *  The following classes are Scene boilerplate.
     */
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
