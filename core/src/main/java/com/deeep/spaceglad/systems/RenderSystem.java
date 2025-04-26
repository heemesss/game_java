package com.deeep.spaceglad.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.math.Vector3;
import com.deeep.spaceglad.Core;
import com.deeep.spaceglad.components.GunComponent;
import com.deeep.spaceglad.components.ModelComponent;

public class RenderSystem extends EntitySystem {
    private static final float FOV = 67F;
    private ImmutableArray<Entity> entities;
    private ModelBatch batch;
    private Environment environment;
    private DirectionalShadowLight shadowLight;
    public PerspectiveCamera camera, gunCamera;
    public Entity gun;
    private Vector3 position;

    public RenderSystem(){
        camera = new PerspectiveCamera(FOV, Core.VIRTUAL_WIDTH, Core.VIRTUAL_HEIGHT);
        camera.far = 1000;
        camera.position.add(10, 10, 0);
//        camera.lookAt(0, 0, 0);

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.5f, 0.5f, 0.5f, 1f));
        shadowLight = new DirectionalShadowLight(1024 * 5, 1024 * 5, 200f, 200f, 1f, 300f);
        shadowLight.set(0.8f, 0.8f, 0.8f, 0, -0.1f, 0.1f);
        environment.add(shadowLight);
        environment.shadowMap = shadowLight;

        batch = new ModelBatch();

        position = new Vector3();
        position.add(50, 50, 50);

        gunCamera = new PerspectiveCamera(FOV, Core.VIRTUAL_WIDTH, Core.VIRTUAL_HEIGHT);
        gunCamera.far = 100;
//        System.out.println(entities.size());
    }

    // Event called when an entity is added to the engine
    public void addedToEngine(Engine e) {
        // Grabs all entities with desired components
        entities = e.getEntitiesFor(Family.all(ModelComponent.class).get());
    }

    public void update(float delta) {
        drawModels();
        drawShadows(delta);
//        camera.rotate(camera.up, -Gdx.input.getDeltaX() * 0.5f);
//        camera.direction.rotate(new Vector3().set(camera.direction).crs(camera.up).nor(), -Gdx.input.getDeltaY() * 0.5f);
//        camera.update(true);



    }

    private boolean isVisible(Camera cam, final ModelInstance instance) {
        return cam.frustum.pointInFrustum(instance.transform.getTranslation(position));
    }

    private void drawShadows(float delta) {
        shadowLight.begin(Vector3.Zero, camera.direction);
        batch.begin(shadowLight.getCamera());
        for (int x = 0; x < entities.size(); x++) {
            ModelComponent mod = entities.get(x).getComponent(ModelComponent.class);
            if (isVisible(camera, mod.instance)) batch.render(mod.instance);
        }
        batch.end();
        shadowLight.end();
    }

    private void drawModels() {
        batch.begin(camera);
        for (int i = 0; i < entities.size(); i++) {
            if (entities.get(i).getComponent(GunComponent.class) == null) {
                ModelComponent mod = entities.get(i).getComponent(ModelComponent.class);
                batch.render(mod.instance, environment);
            }
        }
        batch.end();

        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
        batch.begin(gunCamera);
        batch.render(gun.getComponent(ModelComponent.class).instance);
        batch.end();
    }

    public void resize(int width, int height) {
        camera.viewportHeight = height;
        camera.viewportWidth = width;
        gunCamera.viewportHeight = height;
        gunCamera.viewportWidth = width;
    }

    public void dispose() {
        batch.dispose();
        batch = null;
    }
}
