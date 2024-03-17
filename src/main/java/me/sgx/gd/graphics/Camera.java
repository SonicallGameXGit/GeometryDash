package me.sgx.gd.graphics;

import org.joml.Vector2f;

public class Camera {
	public Vector2f position = new Vector2f(), zoom = new Vector2f(1.0f);
	public float rotation = 0.0f;

	public Camera(Vector2f position, Vector2f zoom, float rotation) {
		this.position = position;
		this.zoom = zoom;
		this.rotation = rotation;
	}
	public Camera() { }
}