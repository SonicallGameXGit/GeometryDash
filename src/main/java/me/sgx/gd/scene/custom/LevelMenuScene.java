package me.sgx.gd.scene.custom;

import me.sgx.engine.audio.SoundSource;
import me.sgx.engine.graphics.Window;
import me.sgx.engine.io.Mouse;
import me.sgx.engine.math.MathUtil;
import me.sgx.engine.math.Time;
import me.sgx.gd.graphics.Camera;
import me.sgx.gd.graphics.Graphics;
import me.sgx.gd.graphics.Sprite;
import me.sgx.gd.graphics.Textures;
import me.sgx.gd.graphics.animation.Animation;
import me.sgx.gd.graphics.animation.InterpolationMode;
import me.sgx.gd.graphics.animation.Keyframe;
import me.sgx.gd.graphics.animation.interpolation.EaseInterpolation;
import me.sgx.gd.graphics.animation.interpolation.Interpolation;
import me.sgx.gd.graphics.gui.MenuLevel;
import me.sgx.gd.scene.Scene;
import me.sgx.gd.scene.SceneSystem;
import me.sgx.gd.world.math.Transform;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class LevelMenuScene extends Scene {
	private final Animation sceneChangeAnimation = new Animation(
			2.5f,
			new Keyframe<>(0.0f, new EaseInterpolation(InterpolationMode.IN_OUT, 2.0f)),
            new Keyframe<>(1.0f, new Interpolation(InterpolationMode.OUT))
	);
	private final Animation cameraBounceAnimation = new Animation(
			3.0f,
			new Keyframe<>(0.0f, new EaseInterpolation(InterpolationMode.OUT, 4.0f)),
			new Keyframe<>(0.5f, new EaseInterpolation(InterpolationMode.OUT, 4.0f)),
            new Keyframe<>(0.0f, new Interpolation(InterpolationMode.OUT))
	);

	private final Sprite edgeSprite = new Sprite(new Transform(new Vector2f(), new Vector2f(0.5f)));

	private final MenuLevel[] levels = {
			new MenuLevel("Active", new Vector3f(0.2f, 0.1f, 1.0f)),
	};
	private int currentLevel = 0, lastLevel = 0, difference = 0;

	private static class DragClass {
		private int clickPosition = 0;
		private float actualDragValue = 0.0f;

		private boolean dragging = false, sign = false;

		public void update() {
			if(
					Mouse.isButtonJustPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)
					|| !Mouse.isButtonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)
			) clickPosition = (int) Mouse.getPosition().x();

			actualDragValue = (Mouse.getPosition().x() - clickPosition) / Window.getWidth() * 2.0f *
					((float) Window.getWidth() / Window.getHeight())
					/ Camera.main.zoom.x();

			if(Math.abs(actualDragValue) >= 0.1f && !dragging) {
				dragging = true;
				sign = Math.signum(actualDragValue) > 0.0f;
			}
			if(!Mouse.isButtonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)) dragging = false;
		}

		public boolean isDragging() {
			return dragging;
		}

		public float getDragValue() {
			return dragging ? actualDragValue - (sign ? 0.1f : -0.1f) : 0.0f;
		}
	}

	private final DragClass dragClass = new DragClass();
	private float lastDragValue = 0.0f;

	private final Sprite groundSprite = new Sprite(new Transform(new Vector2f(), new Vector2f(0.8f)));
	private final Sprite groundHighlightSprite = new Sprite(new Transform());

	private final Camera levelOffsetCamera = new Camera(), globalCamera = new Camera();
	private final Vector3f backgroundColor = new Vector3f(1.0f, 1.0f, 1.0f);

	private final Time time = new Time();

	@Override
	public void initialize() {
		super.initialize();
		Camera.main = new Camera();

		GLFW.glfwSetInputMode(Window.getHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);

		Object menuLoop = SceneSystem.globalData.get(LevelMenuScene.class + "/menu_loop");
		if(menuLoop instanceof SoundSource menuLoopSource) menuLoopSource.unpause();

		edgeSprite.texture = Textures.GUI_LEVELMENU;
		edgeSprite.uv = new Vector4f(0.0f, 0.25f, 0.25f, 0.25f);

		groundSprite.texture = Textures.WORLD_GROUND;
		groundSprite.color.set(new Vector4f(backgroundColor, 1.0f));
		groundSprite.transform.position.y = -1.0f + groundSprite.transform.size.y() / 2.0f - 0.5f;

		groundHighlightSprite.texture = Textures.WORLD_GROUNDHIGHLIGHT;
		groundHighlightSprite.transform.size.set(3.0f, 0.01f);

		sceneChangeAnimation.play();
	}
	@Override
	public void update() {
		time.update();
		Animation.updateAll(time);

		float aspect = (float) Window.getWidth() / Window.getHeight();
		float lastLastDragValue = lastDragValue;

		dragClass.update();
		if(!dragClass.isDragging()) {
			float normalizedDrag = lastDragValue / (aspect * levelOffsetCamera.zoom.x()) / 2.0f;

			currentLevel -= Math.round(normalizedDrag);
			if(currentLevel >= levels.length) {
				currentLevel = 0;
				levelOffsetCamera.position.x = currentLevel * aspect / Camera.main.zoom.x() * 2.0f + lastDragValue;
			} else if(currentLevel < 0) {
				currentLevel = levels.length - 1;
				levelOffsetCamera.position.x = currentLevel * aspect / Camera.main.zoom.x() * 2.0f + lastDragValue;
			}

			lastDragValue = 0.0f;
		} else {
			lastDragValue = dragClass.getDragValue();
			for(MenuLevel level : levels) level.disabled = true;
		}

		for(MenuLevel level : levels) {
			level.update();
			level.disabled = false;
		}

		if(lastLevel != currentLevel) {
			cameraBounceAnimation.play();
			difference = (int) -Math.signum(lastLastDragValue);
			lastLevel = currentLevel;
		}

		levelOffsetCamera.position.x = MathUtil.lerp(
				levelOffsetCamera.position.x(),
				currentLevel * aspect / Camera.main.zoom.x() * 2.0f - (dragClass.isDragging() ? dragClass.getDragValue() : 0.0f) + cameraBounceAnimation.getValue() * difference,
				!dragClass.isDragging() ? 13.0f * time.getDelta() : 1.0f
		);

		float backgroundSize = (float) Window.getWidth() / Window.getHeight() / Camera.main.zoom.x() * 2.0f;

		groundSprite.transform.size.x = backgroundSize;
		groundSprite.uv.z = backgroundSize / groundSprite.transform.size.y();
		groundSprite.color.set(new Vector4f(backgroundColor, 1.0f));

		groundHighlightSprite.transform.position.y = groundSprite.transform.position.y() + groundSprite.transform.size.y() / 2.0f;
	}
	@Override
	public void render() {
		Vector3f currentColor = levels[currentLevel].getColor();
		int nextLevel = currentLevel + (int) Math.signum(lastDragValue);
		if(nextLevel < 0) nextLevel = levels.length - 1;
		else if(nextLevel >= levels.length) nextLevel = 0;

		Vector3f nextColor = levels[nextLevel].getColor();

		float colorDelta = Math.abs(
				lastDragValue / ((float) Window.getWidth() / Window.getHeight() * Camera.main.zoom.x()) / 2.0f
		);

		backgroundColor.set(new Vector3f(currentColor).lerp(nextColor, colorDelta));

		GL11.glClearColor(backgroundColor.x() - 0.1f, backgroundColor.y() - 0.1f, backgroundColor.z() - 0.1f, 1.0f);

		Camera.main = levelOffsetCamera;
		Graphics.updateCamera();

		for(int i = 0; i < levels.length; i++) {
			levels[i].render(i);
		}

		Camera.main = globalCamera;
		Graphics.updateCamera();

		Vector2f bounds = new Vector2f((float) Window.getWidth() / Window.getHeight(), 1.0f);
		bounds.div(Camera.main.zoom);

		groundSprite.render();
		groundHighlightSprite.render();

		edgeSprite.transform.size.x = Math.abs(edgeSprite.transform.size.x());
		edgeSprite.transform.position.set(
				-bounds.x() + Math.abs(edgeSprite.transform.size.x()) / 2.0f,
				-bounds.y() + Math.abs(edgeSprite.transform.size.y()) / 2.0f
		);
		edgeSprite.render();

		edgeSprite.transform.size.x = -Math.abs(edgeSprite.transform.size.x());
		edgeSprite.transform.position.set(
				bounds.x() - Math.abs(edgeSprite.transform.size.x()) / 2.0f,
				-bounds.y() + Math.abs(edgeSprite.transform.size.y()) / 2.0f
		);
		edgeSprite.render();
	}

	@Override
	public void postProcess() {
		Graphics.setPostColor(new Vector3f(sceneChangeAnimation.getValue()));
	}
}