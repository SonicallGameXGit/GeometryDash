package me.sgx.gd.world.block;

import me.sgx.gd.player.Player;
import me.sgx.gd.player.PlayerMode;
import me.sgx.gd.world.block.custom.OrbBlock;
import me.sgx.gd.world.block.custom.ModePortalBlock;
import me.sgx.gd.world.block.custom.PortalBlock;
import me.sgx.gd.world.block.module.MultipleUseModule;
import me.sgx.gd.world.math.Collider;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;

public class Blocks {
	private static final ArrayList<Block> blocks = new ArrayList<>();

	public static final Block BLOCK_BASIC = new Block(
			new Collider(), new Collider(new Vector2f(0.5f, 1.0f)), null,
			Block.getTexture("block/basic")
	);
	public static final Block SPIKE_BASIC = new Block(
			null, new Collider(new Vector2f(0.1f)), null,
			Block.getTexture("spike/basic")
	);

	public static final OrbBlock ORB_JUMPNORMAL = new OrbBlock(
			new Collider(new Vector2f(1.2f)),
			Block.getTexture("orb/jump_normal")
	) {
		@Override
		public void onClick(PlacedBlock placed, Player player) {
			super.onClick(placed, player);

			if(MultipleUseModule.canUse(OrbBlock.MULTIPLE_USE_MODULE_CD_ID, placed)) {
				MultipleUseModule.use(OrbBlock.MULTIPLE_USE_MODULE_CD_ID, placed);
				player.jump(PlayerMode.DEFAULT_JUMP_HEIGHT * 1.13f);
			}
		}
	};
	public static final OrbBlock ORB_JUMPLOW = new OrbBlock(
			new Collider(new Vector2f(1.2f)),
			Block.getTexture("orb/jump_low")
	) {
		@Override
		public void onClick(PlacedBlock placed, Player player) {
			super.onClick(placed, player);

			if(MultipleUseModule.canUse(OrbBlock.MULTIPLE_USE_MODULE_CD_ID, placed)) {
				MultipleUseModule.use(OrbBlock.MULTIPLE_USE_MODULE_CD_ID, placed);
				player.jump(PlayerMode.DEFAULT_JUMP_HEIGHT * 0.638f);
			}
		}
	};
	public static final OrbBlock ORB_FLIPNORMAL = new OrbBlock(
			new Collider(new Vector2f(1.2f)),
			Block.getTexture("orb/flip_normal")
	) {
		@Override
		public void onClick(PlacedBlock placed, Player player) {
			super.onClick(placed, player);

			if(MultipleUseModule.canUse(OrbBlock.MULTIPLE_USE_MODULE_CD_ID, placed)) {
				MultipleUseModule.use(OrbBlock.MULTIPLE_USE_MODULE_CD_ID, placed);

				player.jump(PlayerMode.DEFAULT_JUMP_HEIGHT * 0.638f);
				player.direction = !player.direction;
			}
		}
	};
	public static final OrbBlock ORB_JUMPFLIPNORMAL = new OrbBlock(
			new Collider(new Vector2f(1.2f)),
			Block.getTexture("orb/jumpflip_normal")
	) {
		@Override
		public void onClick(PlacedBlock placed, Player player) {
			super.onClick(placed, player);

			if(MultipleUseModule.canUse(OrbBlock.MULTIPLE_USE_MODULE_CD_ID, placed)) {
				MultipleUseModule.use(OrbBlock.MULTIPLE_USE_MODULE_CD_ID, placed);

				player.direction = !player.direction;
				player.jump(PlayerMode.DEFAULT_JUMP_HEIGHT * 0.889f);
			}
		}
	};
	public static final OrbBlock ORB_JUMPHIGH = new OrbBlock(
			new Collider(new Vector2f(1.2f)),
			Block.getTexture("orb/jump_high")
	) {
		@Override
		public void onClick(PlacedBlock placed, Player player) {
			super.onClick(placed, player);

			if(MultipleUseModule.canUse(OrbBlock.MULTIPLE_USE_MODULE_CD_ID, placed)) {
				MultipleUseModule.use(OrbBlock.MULTIPLE_USE_MODULE_CD_ID, placed);
				player.jump(PlayerMode.DEFAULT_JUMP_HEIGHT * 1.25f);
			}
		}
	};
	public static final OrbBlock ORB_FALLNORMAL = new OrbBlock(
			new Collider(new Vector2f(1.2f)),
			Block.getTexture("orb/fall_normal")
	) {
		@Override
		public void onClick(PlacedBlock placed, Player player) {
			super.onClick(placed, player);

			if(MultipleUseModule.canUse(OrbBlock.MULTIPLE_USE_MODULE_CD_ID, placed)) {
				MultipleUseModule.use(OrbBlock.MULTIPLE_USE_MODULE_CD_ID, placed);
				player.jump(-PlayerMode.DEFAULT_JUMP_HEIGHT * 1.21f);
			}
		}
	};

	// TODO: Draw textures for portals
	public static final ModePortalBlock CUBE_PORTAL = new ModePortalBlock(
			PlayerMode.CUBE, new Collider(),
			new Vector4f()
	);
	public static final ModePortalBlock SHIP_PORTAL = new ModePortalBlock(
			PlayerMode.SHIP, new Collider(),
			new Vector4f()
	);
	public static final ModePortalBlock BALL_PORTAL = new ModePortalBlock(
			PlayerMode.BALL, new Collider(),
			new Vector4f()
	);

	public static final PortalBlock YELLOW_GRAVITY_PORTAL = new PortalBlock(
			new Collider(), new Vector4f(0.375f, 0.125f, 0.125f, 0.25f)
	) {
		@Override
		protected void use(PlacedBlock placed, Player player) {
			player.direction = false;
		}
	};
	public static final PortalBlock BLUE_GRAVITY_PORTAL = new PortalBlock(
			new Collider(), new Vector4f(0.5f, 0.125f, 0.125f, 0.25f)
	) {
		@Override
		protected void use(PlacedBlock placed, Player player) {
			player.direction = true;
		}
	};

	public static final Block BLOCK_GRIDTLPOINT = new Block(
			null, null, null,
			Block.getTexture("block/grid_tlpoint")
	);
	public static final Block BLOCK_GRIDNONE = new Block(
			null, null, null,
			Block.getTexture("block/grid_none")
	);
	public static final Block BLOCK_GRIDTOP = new Block(
			null, null, null,
			Block.getTexture("block/grid_top")
	);
	public static final Block BLOCK_GRIDTL = new Block(
			null, null, null,
			Block.getTexture("block/grid_tl")
	);
	public static final Block BLOCK_GRIDTLR = new Block(
			null, null, null,
			Block.getTexture("block/grid_tlr")
	);
	public static final Block BLOCK_GRIDALL = new Block(
			null, null, null,
			Block.getTexture("block/grid_all")
	);

	public static final ModePortalBlock SWINGCOPTER_PORTAL = new ModePortalBlock(
			PlayerMode.SWING_COPTER, new Collider(),
			new Vector4f()
	);
	public static final ModePortalBlock ROBOT_PORTAL = new ModePortalBlock(
			PlayerMode.ROBOT, new Collider(),
			new Vector4f()
	);

	public static void register(Block block) {
		blocks.add(block);
	}

	public static Block getById(byte id) {
		return id - Byte.MIN_VALUE >= blocks.size() ? null : blocks.get(id + 128);
	}

	public static int getMaxId() {
		return (byte) (blocks.size() + Byte.MIN_VALUE);
	}
}