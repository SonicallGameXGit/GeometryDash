package me.sgx.gd.world;

import org.joml.Vector2f;
import org.joml.Vector4f;

public class ShipPortal extends Block {
	public ShipPortal() {
		super(new Vector4f(0.125f, 0.125f, 0.125f, 0.25f), null, null, new Collider(new Vector2f(0.25f, 0.0f), new Vector2f(1.0f)));
	}

	@Override
	public void onTouch(World world) {
		super.onTouch(world);
		if(!(world.player instanceof ShipPlayer)) world.player = new ShipPlayer(world.player);
	}
}
