package me.sgx.gd.world;

import me.sgx.engine.graphics.texture.Texture;
import me.sgx.gd.graphics.Drawable;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class World extends Drawable {
	private static final int textureAtlas = Texture.create("res/textures/world.png", GL11.GL_NEAREST);

	public final ArrayList<Block> blocks = new ArrayList<>();
	public Vector2f spawnPoint = new Vector2f();

	public static void initialize() {}
	public void clear() {
		blocks.clear();
	}

	@Override
	public void render() {
		Vector4f bounds = getBounds();

		loadTexture(textureAtlas);
		for(Block block : blocks)
			if(new Collider(new Vector2f(-0.5f), new Vector2f(1.0f)).intersects(block.transform, new Transform(), new Collider(new Vector2f(bounds.x(), bounds.y()), new Vector2f(bounds.z() - bounds.x(), bounds.w() - bounds.y()))))
				render(block.transform, BlockInfo.getById(block.id).uv());
	}
}