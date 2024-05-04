package me.sgx.gd.world.block;

import me.sgx.gd.graphics.Sprite;
import me.sgx.gd.world.World;
import me.sgx.gd.world.math.Transform;
import org.joml.Vector4f;

import java.util.ArrayList;

public class PlacedBlock {
	public ArrayList<Object> cd = new ArrayList<>();

	public final Block block;
	public Transform transform = new Transform();
	public Sprite sprite = new Sprite(transform, new Transform());

	public Vector4f color = new Vector4f(1.0f);

	public PlacedBlock(Block block) {
		this.block = block;
		this.transform.size.mul(block.customScale);
	}
	public PlacedBlock(Block block, Transform transform) {
		this.block = block;
		this.transform = new Transform(transform);
		this.transform.size.mul(block.customScale);

		initializeSprite();
		block.initialize(this);
	}
	public PlacedBlock(Block block, Transform transform, Vector4f color) {
		this.block = block;
		this.transform = new Transform(transform);
		this.transform.size.mul(block.customScale);

		this.color = new Vector4f(color);

		initializeSprite();
		block.initialize(this);
	}

	private void initializeSprite() {
		sprite.uv = block.uv;
		sprite.texture = World.TEXTURE_ATLAS;
		sprite.holder = this.transform;
    }
}