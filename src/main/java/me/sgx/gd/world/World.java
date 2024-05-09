package me.sgx.gd.world;

import lombok.extern.log4j.Log4j2;
import me.sgx.engine.audio.Audio;
import me.sgx.engine.audio.AudioSystem;
import me.sgx.engine.audio.SoundSource;
import me.sgx.engine.graphics.Window;
import me.sgx.engine.math.Time;
import me.sgx.gd.graphics.Camera;
import me.sgx.gd.graphics.Graphics;
import me.sgx.gd.graphics.Sprite;
import me.sgx.gd.player.Player;
import me.sgx.gd.world.block.PlacedBlock;
import me.sgx.gd.world.block.custom.OrbBlock;
import me.sgx.gd.world.math.Collider;
import me.sgx.gd.world.math.Transform;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;

@Log4j2
public class World {
	public static final ArrayList<PlacedBlock> blocks = new ArrayList<>();
	public static final Time time = new Time();

	public static Player player = new Player(new Transform()), ghostPlayer = null;

	private static Audio audio = null;

	public static final String TEXTURE_ATLAS = "world";
	private static final SoundSource musicSource = new SoundSource(), explodeSoundSource = new SoundSource();

	public static final Sprite ground = new Sprite(new Transform(new Vector2f(), new Vector2f(1.0f, 4.0f)));

	private static final Sprite groundHighlight = new Sprite(new Transform(new Vector2f(), new Vector2f(12.0f, 0.04f)));
	private static final Sprite background = new Sprite(new Transform());

	public static Vector3f groundColor = new Vector3f(0.15f, 0.1f, 1.0f), backgroundColor = new Vector3f(groundColor);

	public static void initialize() {
		log.info("Initializing world");
		explodeSoundSource.setAudio(AudioSystem.loadAudio("res/sounds/explode.ogg"));

		ground.texture = "ground";
		groundHighlight.texture = "ground_highlight";

		background.texture = "background";
	}
	public static void loadSong(String id) {
		if(audio != null) AudioSystem.clear(audio);
        log.info("Loading audio {}.ogg", id);

		audio = AudioSystem.loadAudio("res/music/" + id + ".ogg");
		musicSource.setAudio(audio);

		respawn(false);
	}

	public static void setMusicVolume(float volume) {
		musicSource.setVolume(volume);
	}
	public static void stopMusic() {
		musicSource.stop();
	}
	public static void playMusic() {
		musicSource.play(false);
	}
	public static void pauseMusic() {
		musicSource.pause();
	}
	public static void unpauseMusic() {
		musicSource.unpause();
	}

	public static void update() {
		time.update();
		musicSource.setPitch(time.scale);

		player.update();
		if(ghostPlayer != null) ghostPlayer.update();

		// Update custom blocks
		OrbBlock.update();
	}
	public static void render() {
		{
			float backgroundSize = (float) Window.getWidth() / Window.getHeight() / Camera.main.zoom.x() * 2.0f;

			ground.transform.position.x = Camera.main.position.x();
			background.transform.position.x = Camera.main.position.x();
			groundHighlight.transform.position.x = Camera.main.position.x();

			ground.transform.size.x = backgroundSize * ground.transform.size.y();
			ground.transform.position.y = -ground.transform.size.y() / 2.0f - 0.5f;

			ground.uv.x = Camera.main.position.x() / ground.transform.size.y();
			ground.uv.z = backgroundSize;

			groundHighlight.transform.position.y = groundHighlight.transform.size.y() / 2.0f - 0.5f - groundHighlight.transform.size.y() / 2.0f;

			background.transform.size.set(backgroundSize * 2.0f);

			background.transform.position.y = Camera.main.position.y() + ground.transform.size.y();

			background.uv.x = Camera.main.position.x() / background.transform.size.x() * 0.1f;
			background.uv.y = Camera.main.position.y() / background.transform.size.y() * 0.1f;

			ground.color.set(new Vector4f(groundColor, 1.0f));
			background.color.set(new Vector4f(backgroundColor, 1.0f));
		}

		background.render();

		Graphics.setTexture("world");

		Vector2f size = new Vector2f((float) Window.getWidth() / Window.getHeight() * 2.0f, 2.0f).div(Camera.main.zoom);
		for(PlacedBlock placedBlock : blocks) {
			if(new Collider(new Vector2f(1.0f)).intersects(
					placedBlock.transform, new Transform(new Vector2f(Camera.main.position), size),
					new Collider(new Vector2f(1.0f))
			)) placedBlock.sprite.render();

			placedBlock.block.update(placedBlock);
		}

		player.sprite.render();
		if(ghostPlayer != null) ghostPlayer.sprite.render();

		ground.render();
		groundHighlight.render();
	}
	public static void respawn(boolean playSound) {
		player = new Player(new Transform(new Vector2f(0.0f, 1.0f)));
		ghostPlayer = null;

		if(playSound) explodeSoundSource.play(false);
		playMusic();

		for(PlacedBlock placedBlock : blocks) {
			placedBlock.cd.clear();
			placedBlock.block.initialize(placedBlock);
		}
	}

	public static float getMusicVolume() {
		return Math.abs(musicSource.getSample(musicSource.getCurrentSampleOffset()));
	}
}