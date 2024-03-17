package me.sgx.gd.world;

import org.joml.Vector2f;

public class Transform {
	public Vector2f position = new Vector2f(), scale = new Vector2f(1.0f);
	public float rotation = 0.0f;

	public Transform(Vector2f position, Vector2f scale, float rotation) {
		this.position = position;
		this.scale = scale;
		this.rotation = rotation;
	}
	public Transform() { }
}
