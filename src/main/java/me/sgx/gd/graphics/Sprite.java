package me.sgx.gd.graphics;

import me.sgx.gd.world.math.Transform;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class Sprite {
	public Transform holder = new Transform();
	public Transform transform;

	public int texture = 0, zIndex = 0;
	public Vector4f uv = new Vector4f(0.0f, 0.0f, 1.0f, 1.0f), color = new Vector4f(1.0f);

	public Sprite(Transform holder, Transform transform) {
		this.holder = holder;
		this.transform = new Transform(transform);
	}
	public Sprite(Transform transform) {
		this.transform = new Transform(transform);
	}

	public void render() {
		Graphics.render(zIndex, texture, uv, color, new Transform(
				new Vector2f(holder.position).add(new Vector2f(transform.position).mul(holder.size)),
				new Vector2f(holder.size).mul(transform.size),
				holder.rotation + transform.rotation
		));
	}
}