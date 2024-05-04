package me.sgx.gd.world.block.custom;

import me.sgx.gd.player.Player;
import me.sgx.gd.player.PlayerMode;
import me.sgx.gd.world.block.Interactable;
import me.sgx.gd.world.block.PlacedBlock;
import me.sgx.gd.world.math.Collider;
import org.joml.Vector4f;

public class ModePortalBlock extends PortalBlock implements Interactable {
	private final PlayerMode mode;

	public ModePortalBlock(PlayerMode mode, Collider triggerCollider, Vector4f uv) {
		super(triggerCollider, uv);
		this.mode = mode;
	}

	@Override
	protected void use(PlacedBlock placed, Player player) {
		player.setMode(mode);
	}
}