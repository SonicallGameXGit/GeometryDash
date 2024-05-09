package me.sgx.gd.scene;

import imgui.ImGui;
import lombok.extern.log4j.Log4j2;
import me.sgx.GeometryDash;
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

@Log4j2
public class SceneSystem {
	public static final String TITLE = String.format("%s %s", GeometryDash.NAME, GeometryDash.VERSION.getAsString());
	public static final HashMap<String, Object> globalData = new HashMap<>();

	public static boolean running = true;
	private static Scene scene = null;

	public static void setScene(Scene scene) {
		if(SceneSystem.scene != null) {
			SceneSystem.scene.close();
			scene.initialize();
		}

        log.info("New scene: {}", scene.getClass().getSimpleName());
		SceneSystem.scene = scene;
	}

	public static void run(boolean enableImGui) {
		log.info("Creating window");
		Window.create(1, 1, TITLE, true, false, false);
		Window.setIcon("res/icon.png");

		if(enableImGui) {
			log.info("Initializing ImGui");
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

		Graphics.initialize();
		Textures.initialize();
		Sounds.initialize();
		World.initialize();

		scene.initialize();

		float lastFpsUpdateTime = 0.0f;
		int fps = 0;

		Time time = new Time();
		while(Window.isRunning() && running) {
			Window.update();
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

			Graphics.end();

			Graphics.postBegin();
			scene.postProcess();
			Graphics.postEnd();

			if(Keyboard.isKeyJustPressed(GLFW.GLFW_KEY_F11)) Window.setFullscreen(!Window.isFullscreen());
		}

		scene.close();

		Graphics.clear();
		AudioSystem.clearAll();

		if(enableImGui) Window.closeImGui();
		Window.close();

		System.exit(0);
	}
}