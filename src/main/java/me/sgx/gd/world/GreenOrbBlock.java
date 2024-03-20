package me.sgx.gd.world;

import org.joml.Vector2f;
import org.joml.Vector4f;

public class GreenOrbBlock extends YellowOrbBlock {
	public GreenOrbBlock() {
		super(new Vector4f(0.75f, 0.0f, 0.125f, 0.125f), new Collider(new Vector2f(), new Vector2f(1.0f)));
	}

	@Override
	public void onJustPress(World world) {
		world.player.physicsSpeed *= -1.0f;
		super.onJustPress(world);
	}
}
