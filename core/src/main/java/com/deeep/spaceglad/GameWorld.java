package com.deeep.spaceglad;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.deeep.spaceglad.UI.GameUI;
import com.deeep.spaceglad.managers.EntityFactory;
import com.deeep.spaceglad.systems.BulletSystem;
import com.deeep.spaceglad.systems.EnemySystem;
import com.deeep.spaceglad.systems.PlayerSystem;
import com.deeep.spaceglad.systems.RenderSystem;

public class GameWorld {
    private static final boolean debug = true;
    private DebugDrawer debugDrawer;
    private Engine engine;
    private Entity gun;
    public RenderSystem renderSystem;
    private PlayerSystem playerSystem;
    public BulletSystem bulletSystem;

    public GameWorld(GameUI gameUI){
        Bullet.init();
        setDebug();
        addSystems(gameUI);
        addEntities();
    }

    private void addSystems(GameUI gameUI) {
        engine = new Engine();
        engine.addSystem(renderSystem = new RenderSystem());
        engine.addSystem(bulletSystem = new BulletSystem());
        engine.addSystem(playerSystem = new PlayerSystem(renderSystem.camera, gameUI, this));
        engine.addSystem(new EnemySystem(this));
        if (debug) bulletSystem.collisionWorld.setDebugDrawer(this.debugDrawer);
    }

    private void addEntities(){
        engine.addEntity(EntityFactory.loadScene(0, 0, 0));
        Entity dome = EntityFactory.loadDome(0, 0, 0);
//        engine.addEntity(EntityFactory.loadDome(0, 0, 0));
        engine.addEntity(dome);
        engine.addEntity(gun = EntityFactory.loadGun(2.5f, -1.9f, -4));
        playerSystem.dome = dome;
//        playerSystem.gun = gun;
        renderSystem.gun = gun;
        engine.addEntity(EntityFactory.createPlayer(bulletSystem, 0, 10, 0));
        engine.addEntity(EntityFactory.createEnemy(bulletSystem, 0, 0, 10));
    }

    public void render(float delta) {
        engine.update(delta);
        if (debug) {
            debugDrawer.begin(renderSystem.camera);
            bulletSystem.collisionWorld.debugDrawWorld();
            debugDrawer.end();
        }
    }

    private void setDebug() {
        if (debug) {
            debugDrawer = new DebugDrawer();
            debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE);
        }
    }

    public void resize(int width, int height) {
        renderSystem.resize(width, height);
    }

}
