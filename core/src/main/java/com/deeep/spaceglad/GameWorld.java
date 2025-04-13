package com.deeep.spaceglad;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.deeep.spaceglad.managers.EntityFactory;
import com.deeep.spaceglad.systems.BulletSystem;
import com.deeep.spaceglad.systems.PlayerSystem;
import com.deeep.spaceglad.systems.RenderSystem;

public class GameWorld {
    private Engine engine;
    public RenderSystem renderSystem;
    private PlayerSystem playerSystem;
    private BulletSystem bulletSystem;

    public GameWorld(){
        Bullet.init();
        addSystems();
        addEntities();
    }

    private void addSystems() {
        engine = new Engine();
        engine.addSystem(renderSystem = new RenderSystem());
        engine.addSystem(bulletSystem = new BulletSystem());
        engine.addSystem(playerSystem = new PlayerSystem(renderSystem.camera));
    }

    private void addEntities(){
        engine.addEntity(EntityFactory.createPlayer(bulletSystem, 0, 15, 0));
        engine.addEntity(EntityFactory.loadScene(0, 0, 0));
        Entity dome;
        engine.addEntity(dome = EntityFactory.loadDome(0, 0, 0));
        playerSystem.dome = dome;
    }

    public void render(float delta) {
        engine.update(delta);
    }

    public void resize(int width, int height) {
        renderSystem.resize(width, height);
    }

}
