package me.sgx;

import imgui.ImGui;
import imgui.type.ImInt;
import me.sgx.engine.audio.AudioSystem;
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

	private record Structure(byte[][] info) {
		public void placeAt(World world, Vector2f position) {
			for(int y = 0; y < info.length; y++)
				for(int x = 0; x < info[y].length; x++)
					if(info[y][x] > -1) world.blocks.add(new Block(info[y][x], new Transform(new Vector2f(x, info.length - 1.0f - y).add(position), new Vector2f(1.0f), 0.0f)));
		}
	}

	private static final Structure michigunSpikes = new Structure(new byte[][] {
			{ 1, 1, 1 }
	});
	private static final Structure madnessPipes = new Structure(new byte[][] {
			{ -1, -1, -1, -1, -1, -1, 0 },
			{ -1, -1, -1,  0, -1, -1, 0 },
			{  0, -1, -1,  0, -1, -1, 0 },
			{  0, -1, -1,  0, -1, -1, 0 }
	});
	private static final Structure gapePipe = new Structure(new byte[][] {
			{  0 },
			{  1 },
			{ -1 },
			{  1 },
			{  0 }
	});

	private static void generateWorld(World world, int offset) {
		long seed = Main.seed + offset * 29423L;
		System.out.println("Seed: " + Main.seed);

		int passBuffer = 0;
		for(int x = -5; x <= 5; x++) {
			Vector2f position = new Vector2f(x + offset * 11.0f, offset);
			world.blocks.add(new Block((byte) 0, new Transform(position, new Vector2f(1.0f), 0.0f)));
			world.blocks.add(new Block((byte) 0, new Transform(new Vector2f(position).add(0.0f, 6.0f), new Vector2f(1.0f), 180.0f)));

			if(x < -3) continue;
			if(new Random(seed + x * 32893).nextFloat() <= 0.015f && passBuffer <= 0) {
				michigunSpikes.placeAt(world, new Vector2f(0.0f, 1.0f).add(position));
				passBuffer = michigunSpikes.info()[0].length;
			}
			if(new Random(seed + x * 32893 + 3724).nextFloat() <= 0.01f && passBuffer <= 0) {
				madnessPipes.placeAt(world, new Vector2f(0.0f, 1.0f).add(position));
				passBuffer = madnessPipes.info()[0].length;
			}
			if(new Random(seed + x * 32893 + 39240).nextFloat() <= 0.03f && passBuffer <= 0) {
				gapePipe.placeAt(world, new Vector2f(0.0f, 1.0f).add(position));
				passBuffer = madnessPipes.info()[0].length;
			}

			passBuffer--;
		}
	}
	public static void main(String[] args) throws InterruptedException {
		Window.create(1920, 1080, "Geometry Dash", true, false, false);
		Window.initImGui(true);

		AudioSystem.initialize();

		Drawable.initialize();

		GL11.glClearColor(0.23f, 0.63f, 1.0f, 1.0f);

		BlockInfo.register(new BlockInfo(new Vector4f(0.0f, 0.0f, 0.125f, 0.125f), new Collider(new Vector2f(), new Vector2f(1.0f)), new Collider(new Vector2f(0.1f, 0.0f), new Vector2f(0.8f, 1.0f))));
		BlockInfo.register(new BlockInfo(new Vector4f(0.125f, 0.0f, 0.125f, 0.125f), null, new Collider(new Vector2f(0.4f, 0.25f), new Vector2f(0.2f, 0.5f))));

		World.initialize("Electrodynamix");

		World world = new World();
		world.spawnPoint = new Vector2f(0.0f, 4.0f);

		int lastOffset = 0;
		generateWorld(world, lastOffset);

		Camera camera = new Camera(new Vector2f(), new Vector2f(0.25f), 0.0f);

		Player player = new CubePlayer();
		player.speed = 8.5f;
		player.transform.position.set(world.spawnPoint);

		int fps = 0;
		float fpsUpdateTime = 0.0f;

		ImInt playerType = new ImInt(0);
		String[] types = new String[] { "Cube", "Ship" };

		Time time = new Time();
		while(Window.isRunning()) {
			Window.update();
			time.update();

			//Thread.sleep(15);

			fps++;
			fpsUpdateTime += time.getActualDelta();
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
				seed = new Random().nextLong();

				world.clear();
				for(int i = -1; i <= 1; i++) generateWorld(world, lastOffset + i);
			}

			if(ImGui.combo("Mode", playerType, types)) {
				if(playerType.get() == 0) {
					player = new CubePlayer(player);
				} else {
					player = new ShipPlayer(player);
				}
			}

			ImGui.end();

			Window.imGuiEnd();

			player.update(time, world, camera);
			world.update(time);

			camera.position.x = player.transform.position.x() + 2.5f;
			camera.position.y = MathUtil.lerp(camera.position.y(), Math.round(player.transform.position.y() / 2.0f) * 2.0f, 2.0f * time.getDelta());

			if(Keyboard.isKeyJustPressed(GLFW.GLFW_KEY_F11)) Window.setFullscreen(!Window.isFullscreen());
			if(Keyboard.isKeyJustPressed(GLFW.GLFW_KEY_ESCAPE)) break;
		}

		world.clear();

		Texture.clearAll();
		ShaderProgram.clearAll();
		AudioSystem.clearAll();
		BlockInfo.clear();

		Window.closeImGui();
		Window.close();
	}
}