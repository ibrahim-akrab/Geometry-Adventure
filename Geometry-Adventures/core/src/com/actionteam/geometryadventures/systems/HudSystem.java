package com.actionteam.geometryadventures.systems;

import com.actionteam.geometryadventures.components.Components;
import com.actionteam.geometryadventures.components.ControlComponent;
import com.actionteam.geometryadventures.ecs.ECSEventListener;
import com.actionteam.geometryadventures.ecs.System;
import com.actionteam.geometryadventures.events.ECSEvents;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Created by theartful on 3/28/18.
 */

public class HudSystem extends System implements ECSEventListener{

    private ControlComponent controlComponent;
    private ShapeRenderer shapeRenderer;
    private ScreenViewport viewport;
    private float smallCircleRadius;

    public HudSystem() {
        super(Components.CONTROL_COMPONENT_CODE);
        shapeRenderer = new ShapeRenderer();
        viewport = new ScreenViewport();
    }

    @Override
    protected void entityAdded(int entityId){
        controlComponent = (ControlComponent)
                ecsManager.getComponent(entityId, Components.CONTROL_COMPONENT_CODE);
    }

    @Override
    protected void ecsManagerAttached() {
        ecsManager.subscribe(ECSEvents.RESIZE_EVENT, this);
    }

    @Override
    public void update(float dt) {
        Gdx.gl20.glLineWidth(3);
        Gdx.gl20.glEnable(GL20.GL_BLEND);
        Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        if(controlComponent.isLeftTouchDown) {
            drawController(controlComponent.leftInitialX, controlComponent.leftInitialY,
                    controlComponent.leftX, controlComponent.leftY);
        }
        if(controlComponent.isRightTouchDown) {
            drawController(controlComponent.rightInitialX, controlComponent.rightInitialY,
                    controlComponent.rightX, controlComponent.rightY);
        }
    }

    private void drawController(float initialX, float initialY,
                                float x, float y) {
        float deltaX = x - initialX;
        float deltaY = y - initialY;
        float r = deltaX * deltaX + deltaY * deltaY;
        float alpha = 1;
        if(r > controlComponent.bigCircleRadius * controlComponent.bigCircleRadius) {
            alpha = (float)Math.sqrt(controlComponent.bigCircleRadius *
                    controlComponent.bigCircleRadius / r);
        }
        x = x - deltaX + alpha * deltaX;
        y = y - deltaY + alpha * deltaY;

        viewport.apply();
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.circle(initialX,
                viewport.getScreenHeight() - initialY,
                controlComponent.bigCircleRadius);
        shapeRenderer.circle(x,
                viewport.getScreenHeight() - y,
                smallCircleRadius);
        shapeRenderer.end();

        shapeRenderer.setColor(new Color(1,1,1,0.1f));
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.circle(initialX,
                viewport.getScreenHeight() - initialY, controlComponent.bigCircleRadius);
        shapeRenderer.circle(x,viewport.getScreenHeight() - y, smallCircleRadius);
        shapeRenderer.end();
    }

    @Override
    public boolean handle(int eventCode, Object message) {
        switch(eventCode) {
            case ECSEvents.RESIZE_EVENT:
                int[] size = (int[]) message;
                resize(size[0], size[1]);
                return true;
        }
        return false;
    }

    private void resize(int width, int height){
        viewport.update(width, height, true);
        smallCircleRadius = 0.7f * controlComponent.bigCircleRadius;
    }
}
