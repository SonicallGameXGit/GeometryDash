package me.sgx.gd.world;

import org.joml.Vector2f;
import org.joml.Vector4f;

public class YellowOrbBlock extends OrbBlock {
	protected YellowOrbBlock(Vector4f uv, Collider triggerCollider) {
		super(uv, triggerCollider, 17.32f);
	}
	public YellowOrbBlock() {
		super(new Vector4f(0.25f, 0.0f, 0.125f, 0.125f), new Collider(new Vector2f(), new Vector2f(1.0f)), 17.32f);
	}
}
