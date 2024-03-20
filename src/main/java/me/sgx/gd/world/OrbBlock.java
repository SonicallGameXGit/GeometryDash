package me.sgx.gd.world;

import org.joml.Vector4f;

public class OrbBlock extends Block {
	public final float jumpHeight;

	public OrbBlock(Vector4f uv, Collider triggerCollider, float jumpHeight) {
		super(uv, null, null, triggerCollider);
		this.jumpHeight = jumpHeight;
	}

	@Override
	public void onJustPress(World world) {
		super.onJustPress(world);
		world.player.velocity.y = jumpHeight * world.player.physicsSpeed;
	}
}
