package com.deeep.spaceglad;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.deeep.spaceglad.systems.BulletSystem;

public class World {
    public BulletSystem bulletSystem = null;
    public Engine engine = null;

    public void remove(Entity entity) {
        engine.removeEntity(entity);
        bulletSystem.removeBody(entity);
    }
}
