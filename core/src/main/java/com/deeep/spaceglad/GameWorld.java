package com.deeep.spaceglad;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.deeep.spaceglad.UI.GameUI;
import com.deeep.spaceglad.components.CharacterComponent;
import com.deeep.spaceglad.managers.EntityFactory;
import com.deeep.spaceglad.systems.BulletSystem;
import com.deeep.spaceglad.systems.EnemySystem;
import com.deeep.spaceglad.systems.PlayerSystem;
import com.deeep.spaceglad.systems.RenderSystem;
import com.deeep.spaceglad.systems.StatusSystem;

public class GameWorld {
    private static final boolean debug = false;
    private DebugDrawer debugDrawer;
    private Engine engine;
    private Entity character, gun;
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
        engine.addSystem(new StatusSystem(this));
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
        character = EntityFactory.createPlayer(bulletSystem, 10, 0, 0);
        engine.addEntity(character);
        engine.addEntity(EntityFactory.createEnemy(bulletSystem, 0, 10, 0));

        // need character
        engine.addSystem(new EnemySystem(this, character));

    }

    public void render(float delta) {
        engine.update(delta);
        if (debug) {
            debugDrawer.begin(renderSystem.camera);
            bulletSystem.collisionWorld.debugDrawWorld();
            debugDrawer.end();
        }
        checkPause();
    }

    private void checkPause() {
        if (Settings.Paused) {
            engine.getSystem(PlayerSystem.class).setProcessing(false);
            engine.getSystem(EnemySystem.class).setProcessing(false);
            engine.getSystem(StatusSystem.class).setProcessing(false);
            engine.getSystem(BulletSystem.class).setProcessing(false);
        } else {
            engine.getSystem(PlayerSystem.class).setProcessing(true);
            engine.getSystem(EnemySystem.class).setProcessing(true);
            engine.getSystem(StatusSystem.class).setProcessing(true);
            engine.getSystem(BulletSystem.class).setProcessing(true);
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

    public void remove(Entity entity) {
        engine.removeEntity(entity);
        bulletSystem.removeBody(entity);
    }

    public void dispose() {
        bulletSystem.collisionWorld.removeAction(character.getComponent(CharacterComponent.class).characterController);
        bulletSystem.collisionWorld.removeCollisionObject(character.getComponent(CharacterComponent.class).ghostObject);
        bulletSystem.dispose();

        bulletSystem = null;
        renderSystem.dispose();

        character.getComponent(CharacterComponent.class).characterController.dispose();
        character.getComponent(CharacterComponent.class).ghostObject.dispose();
        character.getComponent(CharacterComponent.class).ghostShape.dispose();
//        EntityFactory.dispose();
    }
}
