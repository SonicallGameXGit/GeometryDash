package me.sgx.gd.world;

import org.joml.Vector2f;

public class Collider {
	public Vector2f position = new Vector2f(), size = new Vector2f(1.0f);

	public enum ClipType {
		UP, DOWN, BOTH
	}

	public Collider(Vector2f position, Vector2f size) {
		this.position = position;
		this.size = size;
	}
	public Collider() { }

	public boolean intersects(Transform thisTransform, Transform otherTransform, Collider otherCollider) {
		Vector2f thisTransformScale = new Vector2f(thisTransform.scale).absolute();
		Vector2f otherTransformScale = new Vector2f(otherTransform.scale).absolute();

		Vector2f minA = new Vector2f(thisTransform.position).add(new Vector2f(position).mul(thisTransformScale));
		Vector2f minB = new Vector2f(otherTransform.position).add(new Vector2f(otherCollider.position).mul(otherTransformScale));
		Vector2f maxA = new Vector2f(minA).add(new Vector2f(size).mul(thisTransformScale));
		Vector2f maxB = new Vector2f(minB).add(new Vector2f(otherCollider.size).mul(otherTransformScale));

		if(maxA.x() <= minB.x() || minA.x() >= maxB.x()) return false;
		return !(maxA.y() <= minB.y()) && !(minA.y() >= maxB.y());
	}
	public float clipVelocityY(Transform thisTransform, Transform otherTransform, Collider otherCollider, float velocity, ClipType clipType) {
		Vector2f thisTransformScale = new Vector2f(thisTransform.scale).absolute();
		Vector2f otherTransformScale = new Vector2f(otherTransform.scale).absolute();

		Vector2f minA = new Vector2f(thisTransform.position).add(new Vector2f(position).mul(thisTransformScale));
		Vector2f minB = new Vector2f(otherTransform.position).add(new Vector2f(otherCollider.position).mul(otherTransformScale));
		Vector2f maxA = new Vector2f(minA).add(new Vector2f(size).mul(thisTransformScale));
		Vector2f maxB = new Vector2f(minB).add(new Vector2f(otherCollider.size).mul(otherTransformScale));

		if(maxA.x() <= minB.x() || minA.x() >= maxB.x()) return velocity;
		if(clipType == ClipType.DOWN || clipType == ClipType.BOTH) {
			if(velocity < 0.0f && minA.y() >= maxB.y()) {
				float max = maxB.y() - minA.y();
				if(max > velocity) velocity = max;
			}
		}
		if(clipType == ClipType.UP || clipType == ClipType.BOTH) {
			if(velocity > 0.0f && maxA.y() <= minB.y()) {
				float max = minB.y() - maxA.y();
				if(max < velocity) velocity = max;
			}
		}

		return velocity;
	}
}