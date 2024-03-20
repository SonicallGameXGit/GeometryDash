package me.sgx.gd.world;

import org.joml.Vector4f;

public class BlackOrbBlock extends Block {
	public BlackOrbBlock() {
		super(new Vector4f(0.875f, 0.0f, 0.125f, 0.125f), null, null, new Collider());
	}

	@Override
	public void onJustPress(World world) {
		super.onJustPress(world);
		world.player.velocity.y = -80.0f * world.player.physicsSpeed;
	}
}
