package me.sgx.gd.world;

import me.sgx.engine.audio.AudioSystem;
import me.sgx.engine.audio.SoundSource;
import me.sgx.engine.graphics.texture.Texture;
import me.sgx.engine.math.Time;
import me.sgx.gd.graphics.Camera;
import me.sgx.gd.graphics.Drawable;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class World extends Drawable {
	private static final int textureAtlas = Texture.create("res/textures/world.png", GL11.GL_NEAREST);
	private static int music = 0;

	public final ArrayList<Block> blocks = new ArrayList<>();

	private final SoundSource musicSource = new SoundSource();
	public Vector2f spawnPoint = new Vector2f();

	public static void initialize(String songName) {
		music = AudioSystem.loadSound("res/music/" + songName + ".ogg");
	}
	public void clear() {
		blocks.clear();
	}

	public World() {
		musicSource.setSound(music);
		musicSource.play(false);
	}

	public void restart(Player player, Camera camera) {
		player.resetVelocity();

		player.transform.position.set(spawnPoint);
		camera.position.set(spawnPoint);

		musicSource.play(false);
	}
	public void update(Time time) {
		musicSource.setPitch(time.scale);
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