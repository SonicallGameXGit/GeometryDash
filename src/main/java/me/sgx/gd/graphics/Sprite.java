package me.sgx.gd.graphics;

import me.sgx.gd.world.math.Transform;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class Sprite {
	public Transform holder = new Transform();
	public Transform transform;

	public String texture = "none";
	public Vector4f uv = new Vector4f(0.0f, 0.0f, 1.0f, 1.0f), color = new Vector4f(1.0f);

	public Sprite(Transform holder, Transform transform) { // TODO: Implement Z-Index support using sorting
		this.holder = holder;
		this.transform = new Transform(transform);

		// TODO: Implement sorting by getting middle sprite, then, if Z-Index less than right element, then divide right half, else left half, but if equal, end sorting and but there
	}
	public Sprite(Transform transform) {
		this.transform = new Transform(transform);
	}

	public void render() {
		Graphics.setTexture(texture);
		Graphics.setUv(uv);
		Graphics.setColor(color);
		Graphics.setTransform(new Transform(
				new Vector2f(holder.position).add(new Vector2f(transform.position).mul(holder.size)),
				new Vector2f(holder.size).mul(transform.size),
				holder.rotation + transform.rotation
		));

		Graphics.render();
	}
}