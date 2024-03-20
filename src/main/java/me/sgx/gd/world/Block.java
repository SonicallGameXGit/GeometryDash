package me.sgx.gd.world;

import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;

public class Block {
	private static final ArrayList<Block> infos = new ArrayList<>();

	public final Vector4f uv;
	public final Collider resolveCollider, killCollider, triggerCollider;

	public Block(Vector4f uv, Collider resolveCollider, Collider killCollider, Collider triggerCollider) {
		this.uv = uv;
		this.resolveCollider = resolveCollider;
		this.killCollider = killCollider;
		this.triggerCollider = triggerCollider;
	}

	public void onJustPress(World world) {}
	public void onPress(World world) {}
	public void onTouch(World world) {}

	public static void initialize() {
		Block.register(new Block(new Vector4f(0.0f, 0.0f, 0.125f, 0.125f), new Collider(new Vector2f(), new Vector2f(1.0f)), new Collider(new Vector2f(0.1f, 0.0f), new Vector2f(0.8f, 1.0f)), null));
		Block.register(new Block(new Vector4f(0.125f, 0.0f, 0.125f, 0.125f), null, new Collider(new Vector2f(0.375f, 0.25f), new Vector2f(0.25f, 0.5f)), null));
		Block.register(new YellowOrbBlock());
		Block.register(new PinkOrbBlock());
		Block.register(new BlueOrbBlock());
		Block.register(new GreenOrbBlock());
		Block.register(new RedOrbBlock());
		Block.register(new BlackOrbBlock());
		Block.register(new CubePortal());
		Block.register(new ShipPortal());
		Block.register(new BallPortal());
		Block.register(new UpGravityPortal());
		Block.register(new DownGravityPortal());
	}
	public static void register(Block info) {
		infos.add(info);
	}

	public static void clear() {
		infos.clear();
	}

	public static Block getById(byte id) {
		return infos.get(id);
	}
}