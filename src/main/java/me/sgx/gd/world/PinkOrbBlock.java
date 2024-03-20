package me.sgx.gd.world;

import org.joml.Vector2f;
import org.joml.Vector4f;

public class PinkOrbBlock extends OrbBlock {
	protected PinkOrbBlock(Vector4f uv, Collider triggerCollider) {
		super(uv, triggerCollider, 10.392f);
	}
	public PinkOrbBlock() {
		super(new Vector4f(0.375f, 0.0f, 0.125f, 0.125f), new Collider(new Vector2f(-0.1f), new Vector2f(1.2f)), 10.392f);
	}
}
