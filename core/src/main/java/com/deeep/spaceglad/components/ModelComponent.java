package com.deeep.spaceglad.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;

public class ModelComponent implements Component {
    public Model model;
    public ModelInstance instance;
    public Matrix4 matrix4;

    public ModelComponent(Model model, float x, float y, float z) {
        this.matrix4 = new Matrix4();
        this.model = model;
        this.instance = new ModelInstance(model, matrix4.setToTranslation(x, y, z));
    }
}
