package me.sgx.gd.world.block;

import lombok.extern.log4j.Log4j2;
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

@Log4j2
public class Blocks {
	private static final ArrayList<Block> blocks = new ArrayList<>();

	public static final Block DEFAULT = new Block(
			new Collider(), new Collider(new Vector2f(0.5f, 1.0f)), null,
			new Vector4f(0.0f, 0.0f, 0.125f, 0.125f)
	);
	public static final Block DEFAULT_SPIKE = new Block(
			null, new Collider(new Vector2f(0.1f)), null,
			new Vector4f(0.125f, 0.0f, 0.125f, 0.125f)
	);

	public static final OrbBlock YELLOW_ORB = new OrbBlock(
			new Collider(new Vector2f(1.2f)),
			new Vector4f(0.25f, 0.0f, 0.125f, 0.125f)
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
	public static final OrbBlock PINK_ORB = new OrbBlock(
			new Collider(new Vector2f(1.2f)),
			new Vector4f(0.375f, 0.0f, 0.125f, 0.125f)
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
	public static final OrbBlock BLUE_ORB = new OrbBlock(
			new Collider(new Vector2f(1.2f)),
			new Vector4f(0.5f, 0.0f, 0.125f, 0.125f)
	) {
		@Override
		public void onClick(PlacedBlock placed, Player player) {
			super.onClick(placed, player);

			if(MultipleUseModule.canUse(OrbBlock.MULTIPLE_USE_MODULE_CD_ID, placed)) {
				MultipleUseModule.use(OrbBlock.MULTIPLE_USE_MODULE_CD_ID, placed);

				player.jump(PlayerMode.DEFAULT_JUMP_HEIGHT * 0.638f * 0.5f);
				player.direction = !player.direction;
			}
		}
	};
	public static final OrbBlock GREEN_ORB = new OrbBlock(
			new Collider(new Vector2f(1.2f)),
			new Vector4f(0.625f, 0.0f, 0.125f, 0.125f)
	) {
		@Override
		public void onClick(PlacedBlock placed, Player player) {
			super.onClick(placed, player);

			if(MultipleUseModule.canUse(OrbBlock.MULTIPLE_USE_MODULE_CD_ID, placed)) {
				MultipleUseModule.use(OrbBlock.MULTIPLE_USE_MODULE_CD_ID, placed);

				player.direction = !player.direction;
				player.jump(PlayerMode.DEFAULT_JUMP_HEIGHT * 1.13f);
			}
		}
	};
	public static final OrbBlock RED_ORB = new OrbBlock(
			new Collider(new Vector2f(1.2f)),
			new Vector4f(0.75f, 0.0f, 0.125f, 0.125f)
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
	public static final OrbBlock BLACK_ORB = new OrbBlock(
			new Collider(new Vector2f(1.2f)),
			new Vector4f(0.875f, 0.0f, 0.125f, 0.125f)
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

	public static final ModePortalBlock CUBE_PORTAL = new ModePortalBlock(
			PlayerMode.CUBE, new Collider(),
			new Vector4f(0.0f, 0.125f, 0.125f, 0.25f)
	);
	public static final ModePortalBlock SHIP_PORTAL = new ModePortalBlock(
			PlayerMode.SHIP, new Collider(),
			new Vector4f(0.125f, 0.125f, 0.125f, 0.25f)
	);
	public static final ModePortalBlock BALL_PORTAL = new ModePortalBlock(
			PlayerMode.BALL, new Collider(),
			new Vector4f(0.25f, 0.125f, 0.125f, 0.25f)
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

	public static final Block DEFAULT_DECORATION_POINT = new Block(
			null, null, null,
			new Vector4f(0.75f, 0.125f, 0.125f, 0.125f)
	);
	public static final Block DEFAULT_DECORATION_MIDDLE = new Block(
			null, null, null,
			new Vector4f(0.875f, 0.125f, 0.125f, 0.125f)
	);
	public static final Block DEFAULT_DECORATION_TOP = new Block(
			null, null, null,
			new Vector4f(0.625f, 0.25f, 0.125f, 0.125f)
	);
	public static final Block DEFAULT_DECORATION_EDGE = new Block(
			null, null, null,
			new Vector4f(0.75f, 0.25f, 0.125f, 0.125f)
	);
	public static final Block DEFAULT_DECORATION_EDGES = new Block(
			null, null, null,
			new Vector4f(0.875f, 0.25f, 0.125f, 0.125f)
	);
	public static final Block DEFAULT_DECORATION_BLOCKED = new Block(
			null, null, null,
			new Vector4f(0.0f, 0.375f, 0.125f, 0.125f)
	);

	public static void register(Block block) {
		blocks.add(block);
	}

	public static Block getById(byte id) {
		boolean flag = id - Byte.MIN_VALUE >= blocks.size();
		if(flag)
		{
			log.warn("Can't get the block by id {} because it doesn't exist", id);
			return null;
		}

		return blocks.get(id + 128);
	}

	public static int getMaxId() {
		return (byte) (blocks.size() + Byte.MIN_VALUE);
	}
}