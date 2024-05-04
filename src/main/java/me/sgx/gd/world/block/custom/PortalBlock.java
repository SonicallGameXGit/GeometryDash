package me.sgx.gd.world.block.custom;

import me.sgx.gd.player.Player;
import me.sgx.gd.world.block.Block;
import me.sgx.gd.world.block.Interactable;
import me.sgx.gd.world.block.PlacedBlock;
import me.sgx.gd.world.math.Collider;
import org.joml.Vector4f;

public abstract class PortalBlock extends Block implements Interactable {
	public static final int USES_CD_ID = 0;

	public PortalBlock(Collider triggerCollider, Vector4f uv) {
		super(null, null, triggerCollider, uv);
	}

	@Override
	public void initialize(PlacedBlock placed) {
		super.initialize(placed);
		placed.cd.add(false);
	}

	@Override
	public void update(PlacedBlock placed) {
		super.update(placed);
	}

	@Override
	public void onTouch(PlacedBlock placed, Player player) {
		Interactable.super.onTouch(placed, player);

		if(!(boolean) placed.cd.get(USES_CD_ID)) {
			placed.cd.set(USES_CD_ID, true);
			use(placed, player);
		}
	}

	protected abstract void use(PlacedBlock placed, Player player);
}