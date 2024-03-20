package me.sgx.gd.world;

public class PlacedBlock {
	public final byte id;
	public Transform transform = new Transform();

	public PlacedBlock(byte id, Transform transform) {
		this.id = id;
		this.transform = transform;
	}
	public PlacedBlock(byte id) {
		this.id = id;
	}
}