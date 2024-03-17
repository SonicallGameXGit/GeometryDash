package me.sgx.gd.world;

import org.joml.Vector2f;

public class Collider {
	public Vector2f position = new Vector2f(), size = new Vector2f(1.0f);

	public Collider(Vector2f position, Vector2f size) {
		this.position = position;
		this.size = size;
	}
	public Collider() { }

	public boolean intersects(Transform thisTransform, Transform otherTransform, Collider otherCollider) {
		Vector2f minA = new Vector2f(thisTransform.position).add(new Vector2f(position).mul(thisTransform.scale));
		Vector2f minB = new Vector2f(otherTransform.position).add(new Vector2f(otherCollider.position).mul(otherTransform.scale));
		Vector2f maxA = new Vector2f(minA).add(new Vector2f(size).mul(thisTransform.scale));
		Vector2f maxB = new Vector2f(minB).add(new Vector2f(otherCollider.size).mul(otherTransform.scale));

		if(maxA.x() <= minB.x() || minA.x() >= maxB.x()) return false;
		if(maxA.y() <= minB.y() || minA.y() >= maxB.y()) return false;

		return true;
	}
	public float clipVelocityBottom(Transform thisTransform, Transform otherTransform, Collider otherCollider, float velocity) {
		Vector2f minA = new Vector2f(thisTransform.position).add(new Vector2f(position).mul(thisTransform.scale));
		Vector2f minB = new Vector2f(otherTransform.position).add(new Vector2f(otherCollider.position).mul(otherTransform.scale));
		Vector2f maxA = new Vector2f(minA).add(new Vector2f(size).mul(thisTransform.scale));
		Vector2f maxB = new Vector2f(minB).add(new Vector2f(otherCollider.size).mul(otherTransform.scale));

		if(maxA.x() <= minB.x() || minA.x() >= maxB.x()) return velocity;
		if(velocity < 0.0f && minA.y() >= maxB.y()) {
			float max = maxB.y() - minA.y();
			if(max > velocity) velocity = max;
		}

		return velocity;
	}
}