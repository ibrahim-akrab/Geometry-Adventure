package com.actionteam.geometryadventures.systems;

import com.actionteam.geometryadventures.Clock;
import com.actionteam.geometryadventures.GameUtils;
import com.actionteam.geometryadventures.components.CacheComponent;
import com.actionteam.geometryadventures.components.Components;
import com.actionteam.geometryadventures.components.LightComponent;
import com.actionteam.geometryadventures.components.PhysicsComponent;
import com.actionteam.geometryadventures.ecs.System;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by theartful on 5/5/18.
 */

public class LightSystem extends System {

    private ShaderProgram shader;
    private List<CompEnt> entityList;

    private int ulightPos;
    private int ulightIntensity;
    private int uradius;
    private int ulightSources;
    private int uambientLight;
    private int uambientIntensity;
    private int utime;

    private Vector3 ambientLight;
    private float ambientIntensity;

    private class CompEnt {
        LightComponent lc;
        PhysicsComponent pc;
        CacheComponent cc;
        int entity;

        CompEnt(LightComponent lc, PhysicsComponent pc, CacheComponent cc, int entity) {
            this.lc = lc;
            this.pc = pc;
            this.cc = cc;
            this.entity = entity;
        }
    }

    public LightSystem(GameUtils gameUtils) throws IOException {
        super(Components.LIGHT_COMPONENT_CODE, Components.PHYSICS_COMPONENT_CODE,
                Components.CACHE_COMPONENT_CODE);

        InputStream fis = gameUtils.openFile("vertex.glsl");
        StringBuilder vertex = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        String line;
        while ((line = br.readLine()) != null) {
            vertex.append(line);
            vertex.append('\n');
        }
        fis = gameUtils.openFile("fragment.glsl");
        StringBuilder fragment = new StringBuilder();
        br = new BufferedReader(new InputStreamReader(fis));
        while ((line = br.readLine()) != null) {
            fragment.append(line);
            fragment.append('\n');
        }

        shader = new ShaderProgram(vertex.toString(), fragment.toString());
        if (shader.isCompiled()) {
            ShaderProgram.pedantic = false;
        } else {
            Gdx.app.log("SA", "FUC");
            Gdx.app.log("Shader", shader.getLog());
        }

        ulightPos = shader.getUniformLocation("u_lightPos[0]");
        ulightIntensity = shader.getUniformLocation("u_lightIntensity[0]");
        uradius = shader.getUniformLocation("u_radius[0]");
        ulightSources = shader.getUniformLocation("u_lightSources");
        uambientLight = shader.getUniformLocation("u_ambientLight");
        uambientIntensity = shader.getUniformLocation("u_ambientIntensity");
        utime = shader.getUniformLocation("u_time");
        entityList = new ArrayList<CompEnt>();
    }

    @Override
    protected void entityAdded(int entityId) {
        LightComponent lc = (LightComponent) ecsManager.getComponent(entityId,
                Components.LIGHT_COMPONENT_CODE);
        PhysicsComponent pc = (PhysicsComponent) ecsManager.getComponent(entityId,
                Components.PHYSICS_COMPONENT_CODE);
        CacheComponent cc = (CacheComponent) ecsManager.getComponent(entityId,
                Components.CACHE_COMPONENT_CODE);
        entityList.add(new CompEnt(lc, pc, cc, entityId));
    }

    @Override
    protected void entityRemoved(int entityId, int index) {
        entityList.remove(index);
    }


    @Override
    protected void ecsManagerAttached() {

    }

    int xx = 1;

    @Override
    public void update(float dt) {
        shader.begin();
        shader.setUniformf(uambientLight, ambientLight);
        shader.setUniformf(uambientIntensity, ambientIntensity);
        shader.setUniformf(utime, Clock.clock);
        int i = 0;
        for (CompEnt e : entityList) {
            if (e.cc.isCached) {
                shader.setUniformf(ulightPos + i, e.pc.position.x + 0.5f,
                        e.pc.position.y + 0.5f);
                shader.setUniformf(ulightIntensity + i, e.lc.lightIntensity);
                shader.setUniformf(uradius + i, e.lc.radius);
                i++;
            }
            if (i > 10) break;
        }
        shader.setUniformi(ulightSources, i);
        // Gdx.app.log("Light Sources: ", i + "");
        shader.end();
    }

    ShaderProgram getShaderProgram() {
        return shader;
    }

    public void setAmbientLight(Vector3 ambientLight) {
        this.ambientLight = ambientLight;
    }

    public void setAmbientIntensity(float ambientIntensity) {
        this.ambientIntensity = ambientIntensity;
    }
}
