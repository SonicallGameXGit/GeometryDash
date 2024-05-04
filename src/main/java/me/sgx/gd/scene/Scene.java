package me.sgx.gd.scene;

import org.lwjgl.opengl.GL11;

public abstract class Scene {
	public Scene() {}

	public void initialize() {
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
	}
	public void update() {}
	public void render() {}
	public void postProcess() {}
	public void close() {}
}