package me.sgx.gd.world;

public class Block {
	public final byte id;
	public Transform transform = new Transform();

	public Block(byte id, Transform transform) {
		this.id = id;
		this.transform = transform;
	}
	public Block(byte id) {
		this.id = id;
	}
}