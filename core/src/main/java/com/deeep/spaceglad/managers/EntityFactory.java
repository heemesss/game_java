package com.deeep.spaceglad.managers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseProxy;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btPairCachingGhostObject;
import com.badlogic.gdx.physics.bullet.dynamics.btKinematicCharacterController;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.UBJsonReader;
import com.deeep.spaceglad.bullet.MotionState;
import com.deeep.spaceglad.components.BulletComponent;
import com.deeep.spaceglad.components.CharacterComponent;
import com.deeep.spaceglad.components.ModelComponent;
import com.deeep.spaceglad.components.PlayerComponent;
import com.deeep.spaceglad.systems.BulletSystem;

/**
 * Created by Elmar on 7-8-2015.
 */
public class EntityFactory {
    private static Model playerModel, enemyModel;
    private static Texture playerTexture;
    private static ModelBuilder modelBuilder;

    static {
        modelBuilder = new ModelBuilder();
        playerTexture = new Texture(Gdx.files.internal("data/badlogic.jpg"));
        Material material = new Material(TextureAttribute.createDiffuse(playerTexture),
            ColorAttribute.createSpecular(1, 1, 1, 1), FloatAttribute.createShininess(8f));
        playerModel = modelBuilder.createCapsule(2f, 6f, 16, material, VertexAttributes.Usage.Position |
            VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
    }

    private static Entity createCharacter(BulletSystem bulletSystem, float x, float y, float z) {
        // init
        Entity entity = new Entity();

        // Model
        ModelComponent modelComponent = new ModelComponent(playerModel, x, y, z);
        entity.add(modelComponent);

        // Character
        CharacterComponent characterComponent = new CharacterComponent();
        characterComponent.ghostObject = new btPairCachingGhostObject();
        characterComponent.ghostObject.setWorldTransform(modelComponent.instance.transform);
        characterComponent.ghostShape = new btCapsuleShape(2f, 2f);
        characterComponent.ghostObject.setCollisionShape(characterComponent.ghostShape);
        characterComponent.ghostObject.setCollisionFlags(btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT);
        characterComponent.characterController = new btKinematicCharacterController(characterComponent.ghostObject, characterComponent.ghostShape, .35f);
        characterComponent.ghostObject.userData = entity;
        characterComponent.characterController.setGravity(new Vector3(0, -10, 0));
        entity.add(characterComponent);

        // add to bullet
        bulletSystem.collisionWorld.addCollisionObject(entity.getComponent(CharacterComponent.class).ghostObject,
            (short) btBroadphaseProxy.CollisionFilterGroups.CharacterFilter,
            (short) (btBroadphaseProxy.CollisionFilterGroups.AllFilter));
        bulletSystem.collisionWorld.addAction(entity.getComponent(CharacterComponent.class).characterController);
        return entity;
    }

    public static Entity createPlayer(BulletSystem bulletSystem, float x, float y, float z) {
        Entity entity = createCharacter(bulletSystem, x, y, z);
        entity.add(new PlayerComponent());
        return entity;
    }

    public static Entity loadDome(int x, int y, int z) {
        UBJsonReader jsonReader = new UBJsonReader();
        G3dModelLoader modelLoader = new G3dModelLoader(jsonReader);
        Model model = modelLoader.loadModel(Gdx.files.getFileHandle("data/spacedome.g3db", Files.FileType.Internal));
        ModelComponent modelComponent = new ModelComponent(model, x, y, z);
        Entity entity = new Entity();
        entity.add(modelComponent);
        return entity;
    }

    public static Entity loadScene(int x, int y, int z) {
        Entity entity = new Entity();
        ModelLoader<?> modelLoader = new G3dModelLoader(new JsonReader());
        ModelData modelData = modelLoader.loadModelData(Gdx.files.internal("data/arena.g3dj"));
        Model model = new Model(modelData, new TextureProvider.FileTextureProvider());
        ModelComponent modelComponent = new ModelComponent(model, x, y, z);
        entity.add(modelComponent);

        BulletComponent bulletComponent = new BulletComponent();
        btCollisionShape shape = Bullet.obtainStaticNodeShape(model.nodes);
        bulletComponent.bodyInfo = new btRigidBody.btRigidBodyConstructionInfo(0, null, shape, Vector3.Zero);
        bulletComponent.body = new btRigidBody(bulletComponent.bodyInfo);
        bulletComponent.body.userData = entity;
        bulletComponent.motionState = new MotionState(modelComponent.instance.transform);
        ((btRigidBody) bulletComponent.body).setMotionState(bulletComponent.motionState);
        entity.add(bulletComponent);
        return entity;
    }
}
