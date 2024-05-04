package me.sgx.gd.world.block;

import me.sgx.gd.player.Player;

public interface Interactable {
	default void onClick(PlacedBlock placedBlock, Player player) {}
	default void onTouch(PlacedBlock placed, Player player) {}
	default void onRelease(PlacedBlock placed, Player player) {}
}