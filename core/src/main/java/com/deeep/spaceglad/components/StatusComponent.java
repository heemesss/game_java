package com.deeep.spaceglad.components;

import com.badlogic.ashley.core.Component;

public class StatusComponent implements Component {
    public boolean alive, running, attacking;
    public float aliveStateTime;

    public StatusComponent() {
        alive = true;
        running = true;
    }

    public void update(float delta) {
        if (!alive) aliveStateTime += delta;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

}
