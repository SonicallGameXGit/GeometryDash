package me.sgx.gd.world.math;

import org.joml.Vector2f;

public class Transform {
	public Vector2f position = new Vector2f(), size = new Vector2f(1.0f), anchor = new Vector2f(0.0f);
	public float rotation = 0.0f;

	public Transform() {}
	public Transform(Vector2f position) {
		this.position = new Vector2f(position);
	}
	public Transform(Vector2f position, Vector2f size) {
		this.position = new Vector2f(position);
		this.size = new Vector2f(size);
	}
	public Transform(Vector2f position, Vector2f size, float rotation) {
		this.position = new Vector2f(position);
		this.size = new Vector2f(size);

		this.rotation = rotation;
	}
	public Transform(Vector2f position, Vector2f size, float rotation, Vector2f anchor) {
		this.position = new Vector2f(position);
		this.size = new Vector2f(size);
		this.anchor = anchor;

		this.rotation = rotation;
	}
	public Transform(Transform other) {
		this.position = new Vector2f(other.position);
		this.size = new Vector2f(other.size);
		this.anchor.set(other.anchor);

		this.rotation = other.rotation;
	}
}