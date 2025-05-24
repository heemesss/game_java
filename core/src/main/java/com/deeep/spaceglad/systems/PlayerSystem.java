package com.deeep.spaceglad.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.deeep.spaceglad.Assets;
import com.deeep.spaceglad.GameWorld;
import com.deeep.spaceglad.Settings;
import com.deeep.spaceglad.UI.GameUI;
import com.deeep.spaceglad.components.CharacterComponent;
import com.deeep.spaceglad.components.EnemyComponent;
import com.deeep.spaceglad.components.ModelComponent;
import com.deeep.spaceglad.components.PlayerComponent;
import com.deeep.spaceglad.components.StatusComponent;
import com.deeep.spaceglad.managers.ControllerWidget;
import com.deeep.spaceglad.screens.GameScreen;

import java.util.Arrays;

public class PlayerSystem extends EntitySystem implements EntityListener, InputProcessor {
    private Entity player;
    public Entity dome;
    private Camera camera;
    ClosestRayResultCallback rayTestCB;
    private PlayerComponent playerComponent;
    private GameUI gameUI;
    private CharacterComponent characterComponent;
    private ModelComponent modelComponent;
    private Matrix4 ghost = new Matrix4();
    Vector3 rayFrom = new Vector3();
    Vector3 rayTo = new Vector3();
    private GameWorld gameWorld;

    private TextButton fireButton;

    public PlayerSystem(Camera camera, GameUI gameUI, GameWorld gameWorld) {
        this.camera = camera;
        this.gameUI = gameUI;
        this.gameWorld = gameWorld;
        rayTestCB = new ClosestRayResultCallback(Vector3.Zero, Vector3.Z);

        if (Gdx.app.getType() == Application.ApplicationType.Android){
            fireButton = new TextButton("fire", Assets.skin);
            fireButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    fire();
                }
            });
            fireButton.setSize(Gdx.graphics.getWidth() / 8f,
                Gdx.graphics.getWidth() / 8f);
            fireButton.setPosition(Gdx.graphics.getWidth() - fireButton.getWidth() * 1.5f,
                fireButton.getHeight());
            gameUI.stage.addActor(fireButton);
        }
//        characterComponent.characterController.setJumpSpeed(10);
//        characterComponent.characterController.setFallSpeed(10);
    }

    @Override
    public void update(float deltaTime) {
        if (player == null) return;
        updateMovement(deltaTime);
        updateStatus();
        checkGameOver();
    }

    private void updateMovement(float delta) {
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            for (int i = 0; Gdx.input.isTouched(i); i++){
                if (Gdx.input.getX(i) > Gdx.graphics.getWidth() / 2) {
                    System.out.println(Gdx.input.getX(i));
                    camera.rotate(camera.up, -Gdx.input.getDeltaX(i) * 0.25f);
                    camera.direction.rotate(new Vector3().set(camera.direction).crs(camera.up).nor(), -Gdx.input.getDeltaY(i) * 0.25f);
                    break;
                }
            }
        } else {
            camera.rotate(camera.up, -Gdx.input.getDeltaX() * 0.5f);
            camera.direction.rotate(new Vector3().set(camera.direction).crs(camera.up).nor(), -Gdx.input.getDeltaY() * 0.5f);
        }

        // Zero
        characterComponent.characterDirection.set(-1, 0, 0).rot(modelComponent.instance.transform).nor();
        characterComponent.walkDirection.set(0, 0, 0);

        // Move
        Vector3 tmp = new Vector3();
        Vector3 walk = camera.direction.cpy();
        walk.y = 0;
        walk.setLength(camera.direction.len());
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            tmp.set(walk).scl(ControllerWidget.getMovementVector().y * 2);
            characterComponent.walkDirection.add(tmp);

            tmp.set(walk).crs(camera.up).scl(ControllerWidget.getMovementVector().x * 2);
            characterComponent.walkDirection.add(tmp);
        } else {

            if (Gdx.input.isKeyPressed(Input.Keys.W)) characterComponent.walkDirection.add(walk);
            if (Gdx.input.isKeyPressed(Input.Keys.S)) characterComponent.walkDirection.sub(walk);
            if (Gdx.input.isKeyPressed(Input.Keys.A)) tmp.set(walk).crs(camera.up).scl(-1);
            if (Gdx.input.isKeyPressed(Input.Keys.D)) tmp.set(walk).crs(camera.up);
            characterComponent.walkDirection.add(tmp);
        }
        characterComponent.walkDirection.scl(20 * delta);
        characterComponent.characterController.setWalkDirection(characterComponent.walkDirection);

        // Walk
        ghost.set(0, 0, 0, 0);
        Vector3 translation = new Vector3();
        characterComponent.ghostObject.getWorldTransform(ghost);
        ghost.getTranslation(translation);
        modelComponent.instance.transform.set(translation.x, translation.y, translation.z, 0, 0, 0, 0);

        // Camera move
        camera.position.set(translation.x, translation.y + 3, translation.z);
        camera.update(true);

        dome.getComponent(ModelComponent.class).instance.transform.setToTranslation(translation.x, translation.y, translation.z);


//        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && characterComponent.characterController.getLinearVelocity().y == 0) {
//            characterComponent.characterController.setJumpSpeed(25);
//            characterComponent.characterController.jump();
//        }

        if (Gdx.input.justTouched() && Gdx.app.getType() == Application.ApplicationType.Desktop)
            fire();
    }

    private void updateStatus() {
        gameUI.healthWidget.setValue(playerComponent.health);
    }

    private void checkGameOver() {
        if (playerComponent.health <= 0 && !Settings.Paused) {
            Settings.Paused = true;
            gameUI.gameOverWidget.gameOver();
        }
    }

    private void fire() {
        Ray ray = camera.getPickRay(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
        rayFrom.set(ray.origin);
        rayTo.set(ray.direction).scl(150f).add(rayFrom);
        rayTestCB.setCollisionObject(null);
        rayTestCB.setClosestHitFraction(1f);
        rayTestCB.setRayFromWorld(rayFrom);
        rayTestCB.setRayToWorld(rayTo);
        gameWorld.bulletSystem.collisionWorld.rayTest(rayFrom, rayTo, rayTestCB);
        if (rayTestCB.hasHit()) {
            final btCollisionObject obj = rayTestCB.getCollisionObject();
            if (((Entity) obj.userData).getComponent(EnemyComponent.class) != null) {
                if(((Entity) obj.userData).getComponent(StatusComponent.class).alive) {
                    ((Entity) obj.userData).getComponent(StatusComponent.class).setAlive(false);
                    PlayerComponent.score += 100;
                    Assets.soundDeath.play(0.1f);
                }
            }
        }
        Assets.soundGun.play(0.1f);
//        gun.getComponent(AnimationComponent.class).animate("Armature|shoot", 1, 3);
    }

    @Override
    public void addedToEngine(Engine engine) {
        engine.addEntityListener(Family.all(PlayerComponent.class).get(), this);
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    @Override
    public void entityAdded(Entity entity) {
        player = entity;
        playerComponent = entity.getComponent(PlayerComponent.class);
        characterComponent = entity.getComponent(CharacterComponent.class);
        modelComponent = entity.getComponent(ModelComponent.class);
    }

    @Override
    public void entityRemoved(Entity entity) {}
}
