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

	public Player player = null;

	public final ArrayList<PlacedBlock> blocks = new ArrayList<>();

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

	public void restart(Camera camera) {
		player = new CubePlayer(player);
		player.transform.rotation = 0.0f;

		player.resetVelocity();

		player.transform.position.set(spawnPoint);
		player.physicsSpeed = 1.0f;
		
		camera.position.set(spawnPoint);

		playMusic();
	}
	public void update(Time time) {
		musicSource.setPitch(time.scale);
	}

	public void setMusicVolume(float volume) {
		musicSource.setVolume(volume);
	}
	public void setMusicPitch(float pitch) {
		musicSource.setPitch(pitch);
	}
	public void stopMusic() {
		musicSource.stop();
	}
	public void pauseMusic() {
		musicSource.pause();
	}
	public void unpauseMusic() {
		musicSource.unpause();
	}
	public void playMusic() {
		musicSource.play(false);
	}

	@Override
	public void render() {
		Vector4f bounds = getBounds();

		loadTexture(textureAtlas);
		for(PlacedBlock block : blocks)
			if(new Collider(new Vector2f(-0.5f), new Vector2f(1.0f)).intersects(block.transform, new Transform(), new Collider(new Vector2f(bounds.x(), bounds.y()), new Vector2f(bounds.z() - bounds.x(), bounds.w() - bounds.y()))))
				render(block.transform, Block.getById(block.id).uv);
	}
}