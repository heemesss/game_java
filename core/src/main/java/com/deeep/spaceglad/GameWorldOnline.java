package com.deeep.spaceglad;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.deeep.spaceglad.UI.GameUI;
import com.deeep.spaceglad.UI.ScoreWidget;
import com.deeep.spaceglad.WiFi.MyClient;
import com.deeep.spaceglad.WiFi.MyRequest;
import com.deeep.spaceglad.WiFi.MyResponse;
import com.deeep.spaceglad.WiFi.MyServer;
import com.deeep.spaceglad.components.CharacterComponent;
import com.deeep.spaceglad.components.ModelComponent;
import com.deeep.spaceglad.components.PlayerComponent;
import com.deeep.spaceglad.managers.EntityFactory;
import com.deeep.spaceglad.screens.MainMenuScreen;
import com.deeep.spaceglad.systems.BulletSystem;
import com.deeep.spaceglad.systems.EnemySystem;
import com.deeep.spaceglad.systems.PlayerSystem;
import com.deeep.spaceglad.systems.RenderSystem;
import com.deeep.spaceglad.systems.StatusSystem;

import java.util.Objects;

import javax.security.auth.login.AccountLockedException;

public class GameWorldOnline extends World {
    private static final boolean debug = false;
    private DebugDrawer debugDrawer;
    private Entity character, gun, enemy;
    private RenderSystem renderSystem;
    private PlayerSystem playerSystem;
    private MyServer server;
    private MyClient client;
    private MyRequest request;
    private MyResponse response;

    private GameUI gameUI;

    public GameWorldOnline(GameUI gameUI, MyServer server, MyClient client, MyRequest request,
                           MyResponse response){
        this.server = server;
        this.client = client;
        this.request = request;
        this.response = response;
        Bullet.init();
        setDebug();
        addSystems(gameUI);
        gameUI.scoreWidget.player = 0;
        gameUI.scoreWidget.enemy = 0;
        this.gameUI = gameUI;
        addEntities();
    }

    private void addSystems(GameUI gameUI) {
        engine = new Engine();
        engine.addSystem(renderSystem = new RenderSystem());
        engine.addSystem(bulletSystem = new BulletSystem(this));
        engine.addSystem(playerSystem = new PlayerSystem(renderSystem.camera, gameUI, this));
        engine.addSystem(new StatusSystem(this));
        if (debug) bulletSystem.collisionWorld.setDebugDrawer(this.debugDrawer);
    }

    private void addEntities(){
        engine.addEntity(EntityFactory.loadScene(0, 0, 0, "arena2"));
        Entity dome = EntityFactory.loadDome(0, 0, 0);
        engine.addEntity(dome);
        engine.addEntity(gun = EntityFactory.loadGun(2.5f, -1.9f, -4));
        playerSystem.dome = dome;
        renderSystem.gun = gun;
        if (client != null){
            engine.addEntity(character = EntityFactory.createPlayer(bulletSystem, 44, 0, 44));

            engine.addEntity(enemy = EntityFactory.createPlayer(bulletSystem, -44, 0, -44));
            renderSystem.enemy = enemy;
        }
        if (server != null){
            engine.addEntity(character = EntityFactory.createPlayer(bulletSystem, -44, 0, -44));
            setPositionCharacter(character);

            engine.addEntity(enemy = EntityFactory.createPlayer(bulletSystem, 44, 0, 44));
            renderSystem.enemy = enemy;
        }
    }

    private void setPositionCharacter(Entity entity){
        entity.getComponent(ModelComponent.class).instance.transform.set(
            MathUtils.random(1) == 0 ? -44 : 44, 0,
            MathUtils.random(1) == 0 ? -44 : 44, 0, 0, 0, 0);
        entity.getComponent(CharacterComponent.class).ghostObject.setWorldTransform(entity.getComponent(ModelComponent.class).instance.transform);
    }

    public void render(float delta) {
        engine.update(delta);
        if (debug) {
            debugDrawer.begin(renderSystem.camera);
            bulletSystem.collisionWorld.debugDrawWorld();
            debugDrawer.end();
        }
        checkPause();
        if (client != null){
            Vector3 translation = new Vector3();
            character.getComponent(CharacterComponent.class).ghostObject.getWorldTransform().getTranslation(translation);
            request.x = translation.x;
            request.y = translation.y;
            request.z = translation.z;
            request.rotate = playerSystem.getX;

            if (enemy.getComponent(PlayerComponent.class).health <= 0 && !Objects.equals(request.text, "DIE")){
                request.text = "DIE";
                enemy.getComponent(PlayerComponent.class).health = 100;
                gameUI.scoreWidget.player += 1;
                gameUI.deathWidget.setKill();
            } else if (Objects.equals(request.text, "DEATH")) {
                request.text = "";
            }

            client.send();

            // hitbox
            enemy.getComponent(CharacterComponent.class).ghostObject.setWorldTransform(new Matrix4(new Vector3(client.getResponse().x,
                client.getResponse().y, client.getResponse().z), new Quaternion(), new Vector3(1, 1, 1)));
            //position
            enemy.getComponent(ModelComponent.class).instance.transform.set(new Matrix4(new Vector3(client.getResponse().x,
                client.getResponse().y - 2, client.getResponse().z), new Quaternion(), new Vector3(1, 1, 1)));
            // rotate
            enemy.getComponent(ModelComponent.class).instance.transform.rotate(0, 1, 0, -client.getResponse().rotate - 90);
            if (Objects.equals(client.getResponse().text, "DIE") && !Objects.equals(request.text, "DEATH")){
                setPositionCharacter(character);
                gameUI.scoreWidget.enemy += 1;
                request.text = "DEATH";
                gameUI.deathWidget.setDeath();
            }


            if (Objects.equals(client.getResponse().text, "EXIT")){
                Gdx.app.exit();
            }
        }
        else if (server != null){
            Vector3 translation = new Vector3();
            character.getComponent(CharacterComponent.class).ghostObject.getWorldTransform().getTranslation(translation);
            response.x = translation.x;
            response.y = translation.y;
            response.z = translation.z;
            response.rotate = playerSystem.getX;

            if (enemy.getComponent(PlayerComponent.class).health <= 0 && !Objects.equals(response.text, "DIE")){
                response.text = "DIE";
                enemy.getComponent(PlayerComponent.class).health = 100;
                gameUI.scoreWidget.player += 1;
                gameUI.deathWidget.setKill();
            } else if (Objects.equals(response.text, "DIE")) {
                response.text = "";
            }
            // hitbox
            enemy.getComponent(CharacterComponent.class).ghostObject.setWorldTransform(new Matrix4(new Vector3(server.getRequest().x,
                server.getRequest().y, server.getRequest().z), new Quaternion(), new Vector3(1, 1, 1)));
            // position
            enemy.getComponent(ModelComponent.class).instance.transform.set(new Matrix4(new Vector3(server.getRequest().x,
                server.getRequest().y - 2, server.getRequest().z), new Quaternion(), new Vector3(1, 1, 1)));
            // rotate
            enemy.getComponent(ModelComponent.class).instance.transform.rotate(0, 1, 0, -server.getRequest().rotate - 90);
            if (Objects.equals(server.getRequest().text, "DIE") && !Objects.equals(response.text, "DEATH")){
                setPositionCharacter(character);
                gameUI.scoreWidget.enemy += 1;
                response.text = "DEATH";
                gameUI.deathWidget.setDeath();
            }

            if (Objects.equals(server.getRequest().text, "EXIT")) {
                Gdx.app.exit();
            }
        }

    }

    private void checkPause() {
        if (Settings.Paused) {
            engine.getSystem(PlayerSystem.class).setProcessing(false);
//            engine.getSystem(EnemySystem.class).setProcessing(false);
            engine.getSystem(StatusSystem.class).setProcessing(false);
            engine.getSystem(BulletSystem.class).setProcessing(false);
        } else {
            engine.getSystem(PlayerSystem.class).setProcessing(true);
//            engine.getSystem(EnemySystem.class).setProcessing(true);
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

    public void dispose() {
        bulletSystem.collisionWorld.removeAction(character.getComponent(CharacterComponent.class).characterController);
        bulletSystem.collisionWorld.removeCollisionObject(character.getComponent(CharacterComponent.class).ghostObject);
        bulletSystem.dispose();

        bulletSystem = null;
        renderSystem.dispose();

        if (server != null){
            response.text = "EXIT";
        }

        if (client != null){
            request.text = "EXIT";
            client.send();
        }

        character.getComponent(CharacterComponent.class).characterController.dispose();
        character.getComponent(CharacterComponent.class).ghostObject.dispose();
        character.getComponent(CharacterComponent.class).ghostShape.dispose();
//        EntityFactory.dispose();
        Gdx.app.exit();
    }
}
