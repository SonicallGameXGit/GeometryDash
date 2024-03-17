package me.sgx;

import imgui.ImGui;
import me.sgx.engine.audio.AudioSystem;
import me.sgx.engine.audio.SoundSource;
import me.sgx.engine.graphics.Window;
import me.sgx.engine.graphics.shader.ShaderProgram;
import me.sgx.engine.graphics.texture.Texture;
import me.sgx.engine.io.Keyboard;
import me.sgx.engine.math.MathUtil;
import me.sgx.engine.math.Time;
import me.sgx.gd.graphics.Camera;
import me.sgx.gd.graphics.Drawable;
import me.sgx.gd.world.*;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.Random;

public class Main {
	private static long seed = new Random().nextLong();

	private static void generateWorld(World world, int offset) {
		long seed = Main.seed + offset * 29423L;
		Random random = new Random(seed);
		for(int x = -5; x <= 5; x++) {
			world.blocks.add(new Block((byte) 0, new Transform(new Vector2f(x + offset * 11.0f, offset + random.nextInt(0, 2)), new Vector2f(1.0f), 0.0f)));

			/*int height = (int) Math.floor(Math.random() * 3.0);
			for(int y = 0; y < height; y++) {
				world.blocks.add(new Block((byte) 0, new Transform(new Vector2f(x + i * 11.0f, y + 1), new Vector2f(1.0f), 0.0f)));
			}*/

			if(new Random(seed + x).nextFloat() < 0.1) world.blocks.add(new Block((byte) 1, new Transform(new Vector2f(x + offset * 11.0f, offset + 1.0f), new Vector2f(1.0f), 0.0f)));
			if(new Random(seed + x + 328).nextFloat() < 0.1) world.blocks.add(new Block((byte) 1, new Transform(new Vector2f(x + offset * 11.0f, offset + 4.0f), new Vector2f(1.0f), 180.0f)));
		}
	}
	public static void main(String[] args) {
		Window.create(1920, 1080, "Geometry Dash", true, false, false);
		Window.initImGui(true);

		AudioSystem.initialize();

		int music = AudioSystem.loadSound("res/music/847287.ogg");
		SoundSource musicSource = new SoundSource();
		musicSource.setSound(music);
		//musicSource.play(1.0f, 1.0f, false);

		Drawable.initialize();

		GL11.glClearColor(0.23f, 0.63f, 1.0f, 1.0f);

		BlockInfo.register(new BlockInfo(new Vector4f(0.0f, 0.0f, 0.125f, 0.125f), new Collider(new Vector2f(), new Vector2f(1.0f)), new Collider(new Vector2f(0.25f), new Vector2f(0.5f))));
		BlockInfo.register(new BlockInfo(new Vector4f(0.125f, 0.0f, 0.125f, 0.125f), null, new Collider(new Vector2f(0.25f, 0.375f), new Vector2f(0.5f, 0.175f))));

		World.initialize();

		World world = new World();
		world.spawnPoint = new Vector2f(0.0f, 4.0f);

		int lastOffset = 0;
		generateWorld(world, lastOffset);

		Camera camera = new Camera(new Vector2f(), new Vector2f(0.25f), 0.0f);

		Player player = new Player(Texture.create("res/textures/player.png", GL11.GL_NEAREST));
		player.info.speed = 8.5f;
		player.info.gravity = 75.0f;
		player.info.jumpHeight = 17.32f;
		player.transform.position.set(world.spawnPoint);
		player.killCollider = new Collider(new Vector2f(0.25f), new Vector2f(0.5f));
		player.info.coyoteTime = 0.15f;
		player.info.jumpBufferTime = 0.15f;

		int fps = 0;
		float fpsUpdateTime = 0.0f;

		Time time = new Time();
		while(Window.isRunning()) {
			Window.update();
			time.update();

			fps++;
			fpsUpdateTime += time.getDelta();
			if(fpsUpdateTime >= 1.0f) {
				fpsUpdateTime = 0.0f;

				Window.setTitle("Geometry Dash | FPS: " + fps);
				fps = 0;
			}

			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

			Drawable.begin(camera.position, camera.zoom, camera.rotation);

			player.render();
			world.render();

			Drawable.end();

			Window.imGuiBegin();

			ImGui.begin("Debug");

			float[] timeScale = new float[] { time.scale };

			ImGui.dragFloat("Time Scale", timeScale, 0.001f, 0.0f, 2.0f);
			time.scale = timeScale[0];

			int offset = (int) Math.floor((player.transform.position.x() + 11.0f) / 11.0f);
			if(offset != lastOffset) {
				lastOffset = offset;

				world.clear();
				for(int i = -1; i <= 1; i++) generateWorld(world, lastOffset + i);
			} else if(ImGui.button("Regenerate World")) {
				world.clear();
				seed = new Random().nextLong();

				lastOffset = offset;

				world.clear();
				for(int i = -1; i <= 1; i++) generateWorld(world, lastOffset + i);
			}

			ImGui.end();

			Window.imGuiEnd();

			player.update(time, world);

			camera.position.x = player.transform.position.x() + 2.5f;
			camera.position.y = MathUtil.lerp(camera.position.y(), Math.round(player.transform.position.y() / 2.0f) * 2.0f + 1.0f, 2.0f * time.getDelta());

			if(Keyboard.isKeyJustPressed(GLFW.GLFW_KEY_F11)) Window.setFullscreen(!Window.isFullscreen());
			if(Keyboard.isKeyJustPressed(GLFW.GLFW_KEY_ESCAPE)) break;
		}

		world.clear();

		Texture.clearAll();
		ShaderProgram.clearAll();
		AudioSystem.clearAll();

		Window.closeImGui();
		Window.close();
	}
}