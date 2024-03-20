package me.sgx.gd.world;

import org.joml.Vector2f;
import org.joml.Vector4f;

public class RedOrbBlock extends OrbBlock {
	public RedOrbBlock() {
		super(new Vector4f(0.625f, 0.0f, 0.125f, 0.125f), new Collider(new Vector2f(), new Vector2f(1.0f)), 25.98f);
	}
}
