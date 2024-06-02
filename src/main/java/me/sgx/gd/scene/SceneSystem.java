package me.sgx.gd.scene;

import imgui.ImGui;
import me.sgx.engine.audio.AudioSystem;
import me.sgx.engine.graphics.Window;
import me.sgx.engine.io.Keyboard;
import me.sgx.engine.math.Time;
import me.sgx.gd.audio.Sounds;
import me.sgx.gd.graphics.Graphics;
import me.sgx.gd.graphics.Textures;
import me.sgx.gd.world.World;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

import java.util.HashMap;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

public class SceneSystem {
	public static final String TITLE = "Geometry Dash 0.1";
	public static final HashMap<String, Object> globalData = new HashMap<>();

	public static boolean running = true;
	private static Scene scene = null;

	public static void setScene(Scene scene) {
		if(SceneSystem.scene != null) {
			SceneSystem.scene.close();
			scene.initialize();
		}

		SceneSystem.scene = scene;
	}

	public static void run(Class<? extends Scene> defaultScene, boolean enableImGui) {
		Window.create(1, 1, TITLE, true, false, false);
		Window.setIcon("res/icon.png");

		GLFW.glfwSetWindowAspectRatio(Window.getHandle(), 16, 9);

		if(enableImGui) {
			Window.initImGui(true);
			ImGui.getIO().setFontGlobalScale(2.0f);
		}

		GLFWVidMode videoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		if(videoMode != null) {
			Window.setSize(new Vector2i(videoMode.width() / 2, videoMode.height() / 2));
			Window.setPosition(new Vector2i(
					(videoMode.width() - Window.getWidth()) / 2,
					(videoMode.height() - Window.getHeight()) / 2
			));
		}

		Window.maximize();

		AudioSystem.initialize();

		Textures.initialize();
		Graphics.initialize();
		Sounds.initialize();
		World.initialize();

		try {
			//noinspection deprecation
			setScene(defaultScene.newInstance());
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		scene.initialize();

		float lastFpsUpdateTime = 0.0f;
		int fps = 0;

		Time time = new Time();
		while(Window.isRunning() && running) {
			Window.pollEvents();
			glClear(GL_COLOR_BUFFER_BIT);

			time.update();

			lastFpsUpdateTime += time.getDelta();
			fps++;

			if(lastFpsUpdateTime >= 1.0f) {
				lastFpsUpdateTime = 0.0f;
				Window.setTitle(TITLE + " | FPS: " + fps);

				fps = 0;
			}

			Graphics.begin();

			scene.update();
			scene.render();

			Graphics.renderAll();
			Graphics.end();

			Graphics.postBegin();
			scene.postProcess();
			Graphics.postEnd();

			if(Keyboard.isKeyJustPressed(GLFW.GLFW_KEY_F11)) Window.setFullscreen(!Window.isFullscreen());
			Window.swapBuffers();
		}

		scene.close();

		Graphics.clear();
		AudioSystem.clearAll();

		if(enableImGui) Window.closeImGui();
		Window.close();

		System.exit(0);
	}
}