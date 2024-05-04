package me.sgx.gd.scene.custom;

import me.sgx.engine.audio.SoundSource;
import me.sgx.engine.graphics.Window;
import me.sgx.engine.math.Time;
import me.sgx.gd.audio.Sounds;
import me.sgx.gd.graphics.Camera;
import me.sgx.gd.graphics.Graphics;
import me.sgx.gd.graphics.Sprite;
import me.sgx.gd.graphics.animation.Animation;
import me.sgx.gd.graphics.animation.InterpolationMode;
import me.sgx.gd.graphics.animation.Keyframe;
import me.sgx.gd.graphics.animation.interpolation.EaseInterpolation;
import me.sgx.gd.graphics.animation.interpolation.Interpolation;
import me.sgx.gd.graphics.gui.BouncingButton;
import me.sgx.gd.player.Player;
import me.sgx.gd.scene.Scene;
import me.sgx.gd.scene.SceneSystem;
import me.sgx.gd.world.math.Transform;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class MainMenuScene extends Scene {
	private final BouncingButton playButton = new BouncingButton(
			"gui/main_menu",
			new Transform(new Vector2f(), new Vector2f(0.6f)),
			new Vector4f(
					0.0f, 0.0f,
					0.25f, 0.25f
			)
	);
	private final BouncingButton editorButton = new BouncingButton(
			"gui/main_menu",
			new Transform(new Vector2f(playButton.transform.size.x() + 0.1f, 0.0f), new Vector2f(0.425f)),
			new Vector4f(
					0.25f, 0.0f,
					0.25f, 0.25f
			)
	);
	private final Sprite titleSprite = new Sprite(new Transform(new Vector2f(), new Vector2f(2.75f, 2.75f * 0.127f)));

	private final Animation sceneChangeAnimation = new Animation(
			2.5f,
			new Keyframe<>(0.0f, new EaseInterpolation(InterpolationMode.IN_OUT, 2.0f)),
            new Keyframe<>(1.0f, new Interpolation(InterpolationMode.OUT))
	);

	private final Sprite backgroundSprite = new Sprite(new Transform(new Vector2f(0.0f, 0.5f)));
	private final Sprite groundSprite = new Sprite(new Transform(new Vector2f(), new Vector2f(0.8f)));
	private final Sprite groundHighlightSprite = new Sprite(new Transform());

	private final Vector3f backgroundStaticColor = new Vector3f(0.15f, 0.1f, 1.0f);
	private float backgroundColorChangeTimer = 0.0f, backgroundColorChangeTime = getBackgroundColorChangeTime();

	private final Vector2f exitButtonSize = new Vector2f(0.225f * 1.0323f, 0.225f);
	private final BouncingButton exitButton = new BouncingButton("gui/main_menu", new Transform(
			new Vector2f(),
			exitButtonSize
	));

	private final Time time = new Time();

	private static float getBackgroundColorChangeTime() {
		return (float) Math.random() * 20.0f + 10.0f;
	}

	@Override
	public void initialize() {
		super.initialize();
		Camera.main = new Camera();

		SoundSource menuLoopSource = new SoundSource();
		menuLoopSource.setAudio(Sounds.getAudio("menu_loop"));
		menuLoopSource.play(true);

		SceneSystem.globalData.put(MainMenuScene.class + "/menu_loop", menuLoopSource);

		titleSprite.texture = "gui/main_menu";
		titleSprite.uv = new Vector4f(0.0f, 1.0f - 0.125f, 0.99f, 0.125f);

		backgroundSprite.texture = "background";
		backgroundSprite.color.set(new Vector4f(backgroundStaticColor, 1.0f));

		groundSprite.texture = "ground";
		groundSprite.color.set(new Vector4f(backgroundStaticColor.x() - 0.2f, backgroundStaticColor.y() - 0.2f, backgroundStaticColor.z() - 0.2f, 1.0f));
		groundSprite.transform.position.y = -1.0f + groundSprite.transform.size.y() / 2.0f - 0.325f;

		groundHighlightSprite.texture = "ground_highlight";
		groundHighlightSprite.transform.size.set(3.0f, 0.01f);

		exitButton.uv.set(0.5f, 0.0f, 0.129f, 0.125f);

		sceneChangeAnimation.play();
	}
	@Override
	public void update() {
		time.update();
		Animation.updateAll(time);

		playButton.update();
		editorButton.update();
		exitButton.update();

		if(exitButton.isHovered() && exitButton.isReleased()) SceneSystem.running = false;

		if(playButton.isHovered() && playButton.isReleased() && !editorButton.disabled) {
			sceneChangeAnimation.time = 1.0f;
			sceneChangeAnimation.speed = -Math.abs(sceneChangeAnimation.speed);
			sceneChangeAnimation._continue();

			playButton.disabled = true;
		}
		if(editorButton.isHovered() && editorButton.isReleased() && !playButton.disabled) {
			sceneChangeAnimation.speed = -Math.abs(sceneChangeAnimation.speed);
			sceneChangeAnimation._continue();

			editorButton.disabled = true;
		}

		if(!sceneChangeAnimation.isPlaying()) {
			if(playButton.disabled) SceneSystem.setScene(new LevelMenuScene());
			if(editorButton.disabled) SceneSystem.setScene(new LevelEditorScene());
		}

		titleSprite.transform.position.y = 1.0f - titleSprite.transform.size.y() / 2.0f - 0.1f;

		float backgroundSize = (float) Window.getWidth() / Window.getHeight() / Camera.main.zoom.x() * 2.0f;

		backgroundSprite.transform.size.x = backgroundSize * 3.0f;
		backgroundSprite.transform.size.y = 3.0f;

		backgroundSprite.uv.z = backgroundSize;

		backgroundSprite.uv.x += 0.1f * time.getDelta();
		if(backgroundSprite.uv.x() >= 1.0f) backgroundSprite.uv.x = 0.0f;

		groundSprite.transform.size.x = backgroundSize;
		groundSprite.uv.z = backgroundSize / groundSprite.transform.size.y();

		groundSprite.uv.x += Player.SLOW_SPEED / 2.0f * time.getDelta();
		if(groundSprite.uv.x() >= 1.0f) groundSprite.uv.x = 0.0f;

		groundHighlightSprite.transform.position.y = groundSprite.transform.position.y() + groundSprite.transform.size.y() / 2.0f;

		backgroundColorChangeTimer += time.getDelta();
		if(backgroundColorChangeTimer >= backgroundColorChangeTime) {
            backgroundColorChangeTimer = 0.0f;
            backgroundColorChangeTime = getBackgroundColorChangeTime();

			backgroundStaticColor.set((float) Math.random(), (float) Math.random(), (float) Math.random()).normalize();
        }

		backgroundSprite.color.lerp(new Vector4f(backgroundStaticColor, 1.0f), 0.1f * time.getDelta());
		groundSprite.color.lerp(new Vector4f(backgroundStaticColor.x() - 0.2f, backgroundStaticColor.y() - 0.2f, backgroundStaticColor.z() - 0.2f, 1.0f), 0.1f * time.getDelta());

		exitButton.transform.position.set(-backgroundSize / 2.0f + 0.015f + exitButtonSize.x() / 2.0f, 1.0f - exitButtonSize.y() / 2.0f - 0.015f);
	}
	@Override
	public void render() {
		backgroundSprite.render();
		groundSprite.render();
		groundHighlightSprite.render();

		exitButton.render();

		playButton.render();
		editorButton.render();

		titleSprite.render();
	}

	@Override
	public void postProcess() {
		Graphics.setPostColor(new Vector3f(sceneChangeAnimation.getValue()));
	}
}