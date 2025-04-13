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
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.deeep.spaceglad.components.CharacterComponent;
import com.deeep.spaceglad.components.ModelComponent;
import com.deeep.spaceglad.components.PlayerComponent;

public class PlayerSystem extends EntitySystem implements EntityListener, InputProcessor {
    private Entity player;
    public Entity dome;
    private Camera camera;
    ClosestRayResultCallback rayTestCB;
    private PlayerComponent playerComponent;
    private CharacterComponent characterComponent;
    private ModelComponent modelComponent;
    private Matrix4 ghost = new Matrix4();

    public PlayerSystem(Camera camera) {
        this.camera = camera;
        rayTestCB = new ClosestRayResultCallback(Vector3.Zero, Vector3.Z);
    }

    @Override
    public void update(float deltaTime) {
        if (player == null) return;
        updateMovement(deltaTime);
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
        if (Gdx.input.isKeyPressed(Input.Keys.W)) characterComponent.walkDirection.add(camera.direction);
        if (Gdx.input.isKeyPressed(Input.Keys.S)) characterComponent.walkDirection.sub(camera.direction);
        if (Gdx.input.isKeyPressed(Input.Keys.A)) tmp.set(camera.direction).crs(camera.up).scl(-1);
        if (Gdx.input.isKeyPressed(Input.Keys.D)) tmp.set(camera.direction).crs(camera.up);
        characterComponent.walkDirection.add(tmp);
        characterComponent.walkDirection.scl(10f * delta);
        characterComponent.characterController.setWalkDirection(characterComponent.walkDirection);

        // Walk
        ghost.set(0, 0, 0, 0);
        Vector3 translation = new Vector3();
        characterComponent.ghostObject.getWorldTransform(ghost);   //TODO export this
        ghost.getTranslation(translation);
        modelComponent.instance.transform.set(translation.x, translation.y, translation.z, camera.direction.x, camera.direction.y, camera.direction.z, 0);

        // Camera move
        camera.position.set(translation.x, translation.y, translation.z);
        camera.update(true);

        dome.getComponent(ModelComponent.class).instance.transform.setToTranslation(translation.x, translation.y, translation.z);
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
