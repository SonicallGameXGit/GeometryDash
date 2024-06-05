package me.sgx.gd.world.math;

import org.joml.Vector2f;
import org.joml.Vector4f;

public class Collider {
	private static final float BIAS = 0.001f;
	public Vector2f scale = new Vector2f(1.0f);

	public Collider() {}
	public Collider(Vector2f scale) {
		this.scale.set(scale);
	}
	public Collider(Collider other) {
		scale.set(other.scale);
	}

	public enum ClipType {
		DOWN, UP, BOTH
	}

	private Vector4f getTrueRange(Transform transform) {
		Vector2f scaledSize = new Vector2f(transform.size).mul(scale).absolute();

		return new Vector4f(
				transform.position.x - scaledSize.x / 2.0f, transform.position.y - scaledSize.y / 2.0f,
				transform.position.x + scaledSize.x / 2.0f, transform.position.y + scaledSize.y / 2.0f
		);
	}

	public boolean intersects(Transform transform, Transform otherTransform, Collider other) {
		Vector4f a = getTrueRange(transform);
		Vector4f b = other.getTrueRange(otherTransform);

		return a.x < b.z && a.z > b.x &&
				a.y < b.w && a.w > b.y;
	}

	public float clipVelocityX(Transform transform, Transform otherTransform, Collider other, float velocity, ClipType clipType) {
		Vector4f a = getTrueRange(transform);
		Vector4f b = other.getTrueRange(otherTransform);

		if(a.w <= b.y || a.y >= b.w) return velocity;
		if(clipType == ClipType.DOWN || clipType == ClipType.BOTH) {
			if(velocity < 0.0f && a.x >= b.z) {
				float max = b.z - a.x + BIAS;
				if(max < velocity) velocity = max;
			}
		}
		if(clipType == ClipType.UP || clipType == ClipType.BOTH) {
			if(velocity > 0.0f && a.z <= b.x) {
				float max = b.x - a.z - BIAS;
				if(max < velocity) velocity = max;
			}
		}

		return velocity;
	}

	public float clipVelocityY(Transform transform, Transform otherTransform, Collider other, float velocity, ClipType clipType) {
		Vector4f a = getTrueRange(transform);
		Vector4f b = other.getTrueRange(otherTransform);

		if(a.z <= b.x || a.x >= b.z) return velocity;
		if(clipType == ClipType.DOWN || clipType == ClipType.BOTH) {
			if(velocity < 0.0f && a.y >= b.w) {
				float max = b.w - a.y + BIAS;
				if(max > velocity) velocity = max;
			}
		}
		if(clipType == ClipType.UP || clipType == ClipType.BOTH) {
			if(velocity > 0.0f && a.w <= b.y) {
				float max = b.y - a.w - BIAS;
				if(max < velocity) velocity = max;
			}
		}

		return velocity;
	}
}