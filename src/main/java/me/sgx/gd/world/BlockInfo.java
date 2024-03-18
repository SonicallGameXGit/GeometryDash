package me.sgx.gd.world;

import org.joml.Vector4f;

import java.util.ArrayList;

public record BlockInfo(Vector4f uv, Collider resolveCollider, Collider killCollider) {
	private static final ArrayList<BlockInfo> infos = new ArrayList<>();

	public static void register(BlockInfo info) {
		infos.add(info);
	}
	public static void clear() {
		infos.clear();
	}

	public static BlockInfo getById(byte id) {
		return infos.get(id);
	}
	public static int getBlocksCount() {
		return infos.size();
	}
}