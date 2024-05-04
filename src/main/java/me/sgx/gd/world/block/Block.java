package me.sgx.gd.world.block;

import me.sgx.gd.world.math.Collider;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class Block {
	private static byte lastId = -128;
	public final byte id;

	public final Collider resolveCollider, damageCollider, triggerCollider;
	public final Vector4f uv;
	public final Vector2f customScale;

	public Block() {
		this.uv = new Vector4f(0.0f, 0.0f, 1.0f, 1.0f);

		id = next();

		resolveCollider = new Collider();
		damageCollider = new Collider();
		triggerCollider = new Collider();
		customScale = new Vector2f(1.0f);

		Blocks.register(this);
	}
	public Block(Vector4f uv) {
		this.uv = uv;

		id = next();

		resolveCollider = new Collider();
		damageCollider = new Collider();
		triggerCollider = new Collider();
		customScale = new Vector2f(1.0f);

		Blocks.register(this);
	}
	public Block(Collider resolveCollider, Collider damageCollider, Collider triggerCollider, Vector4f uv) {
		this.resolveCollider = resolveCollider == null ? null : new Collider(resolveCollider);
		this.damageCollider = damageCollider == null ? null : new Collider(damageCollider);
		this.triggerCollider = triggerCollider == null ? null : new Collider(triggerCollider);

		this.uv = uv;
		customScale = new Vector2f(1.0f);

		id = next();
		Blocks.register(this);
	}
	public Block(Collider resolveCollider, Collider damageCollider, Collider triggerCollider, Vector4f uv, Vector2f customScale) {
		this.resolveCollider = resolveCollider == null ? null : new Collider(resolveCollider);
		this.damageCollider = damageCollider == null ? null : new Collider(damageCollider);
		this.triggerCollider = triggerCollider == null ? null : new Collider(triggerCollider);

		this.uv = uv;
		this.customScale = customScale;

		id = next();
		Blocks.register(this);
	}

	public void initialize(PlacedBlock placed) {}
	public void update(PlacedBlock placed) {}

	private byte next() {
		return (byte) ((lastId += 1) - 1);
	}
}