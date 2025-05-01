package com.deeep.spaceglad.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.deeep.spaceglad.GameWorld;
import com.deeep.spaceglad.Settings;
import com.deeep.spaceglad.UI.GameUI;
import com.deeep.spaceglad.components.CharacterComponent;
import com.deeep.spaceglad.components.EnemyComponent;
import com.deeep.spaceglad.components.ModelComponent;
import com.deeep.spaceglad.components.PlayerComponent;
import com.deeep.spaceglad.components.StatusComponent;

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

    public PlayerSystem(Camera camera, GameUI gameUI, GameWorld gameWorld) {
        this.camera = camera;
        this.gameUI = gameUI;
        this.gameWorld = gameWorld;
        rayTestCB = new ClosestRayResultCallback(Vector3.Zero, Vector3.Z);
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
        // Camera
        camera.rotate(camera.up, -Gdx.input.getDeltaX() * 0.5f);
        camera.direction.rotate(new Vector3().set(camera.direction).crs(camera.up).nor(), -Gdx.input.getDeltaY() * 0.5f);

        // Zero
        characterComponent.characterDirection.set(-1, 0, 0).rot(modelComponent.instance.transform).nor();
        characterComponent.walkDirection.set(0, 0, 0);

        // Move
        Vector3 tmp = new Vector3();
        if (Gdx.input.isKeyPressed(Input.Keys.W))
            characterComponent.walkDirection.add(new Vector3(camera.direction.x, 0, camera.direction.z).setLength(camera.direction.len()));
        if (Gdx.input.isKeyPressed(Input.Keys.S))
            characterComponent.walkDirection.sub(new Vector3(camera.direction.x, 0, camera.direction.z).setLength(camera.direction.len()));
        if (Gdx.input.isKeyPressed(Input.Keys.A))
            tmp.set(camera.direction).crs(camera.up).setLength(camera.direction.len()).scl(-1);
        if (Gdx.input.isKeyPressed(Input.Keys.D))
            tmp.set(camera.direction).crs(camera.up).setLength(camera.direction.len());

        characterComponent.walkDirection.add(tmp);
        characterComponent.walkDirection.scl(10f * delta);
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

        if (Gdx.input.justTouched()) fire();
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
        Ray ray = camera.getPickRay(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        rayFrom.set(ray.origin);
        rayTo.set(ray.direction).scl(50f).add(rayFrom);
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
                }
            }
        }
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
