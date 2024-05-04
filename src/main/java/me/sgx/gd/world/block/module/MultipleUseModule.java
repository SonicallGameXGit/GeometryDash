package me.sgx.gd.world.block.module;

import me.sgx.gd.world.block.PlacedBlock;

public class MultipleUseModule {
	public static final int NBT_SIZE = 2;

	public static void initialize(PlacedBlock placed) {
		placed.cd.add(false);
		placed.cd.add(false);
	}
	public static void use(int nbtIndexOffset, PlacedBlock placed) {
		placed.cd.set(nbtIndexOffset, true);
	}

	public static boolean canUse(int nbtIndexOffset, PlacedBlock placed) {
		return !(boolean) placed.cd.get(nbtIndexOffset) || (boolean) placed.cd.get(nbtIndexOffset + 1);
	}
}