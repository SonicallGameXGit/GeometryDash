package me.sgx.gd.scene.custom;

import me.sgx.engine.audio.SoundSource;
import me.sgx.engine.graphics.Window;
import me.sgx.gd.graphics.Camera;
import me.sgx.gd.graphics.Graphics;
import me.sgx.gd.io.LevelParser;
import me.sgx.gd.scene.Scene;
import me.sgx.gd.scene.SceneSystem;
import me.sgx.gd.world.World;
import org.joml.Vector3f;

public class LevelScene extends Scene {
	private final String level;

	private static class FpsRecorder {
		private float fpsUpdateTimer = 0.0f;
		private int fps = 0;

		public void update(float delta) {
			fpsUpdateTimer += delta;
            fps++;
			if (fpsUpdateTimer >= 1.0f) {
				Window.setTitle(SceneSystem.TITLE + " | FPS: " + fps);

				fpsUpdateTimer = 0.0f;
                fps = 0;
            }
		}
	}

	private final FpsRecorder fpsRecorder = new FpsRecorder();

	public LevelScene(String level) {
		super();
		this.level = level;
	}

	@Override
	public void initialize() {
		super.initialize();

		Object menuLoop = SceneSystem.globalData.get(MainMenuScene.class + "/menu_loop");
		if(menuLoop instanceof SoundSource menuLoopSource) menuLoopSource.stop();

		LevelParser.load("res/levels/" + level + ".bin");

		Camera.main = new Camera();
		Camera.main.zoom.set(0.25f);
	}

	@Override
	public void update() {
		fpsRecorder.update(World.time.getDelta());

		World.update();
		Camera.main.follow(World.player);
	}
	@Override
	public void render() {
		World.render();
	}
	@Override
	public void postProcess() {
		Graphics.setPostColor(new Vector3f(1.0f));
	}
}