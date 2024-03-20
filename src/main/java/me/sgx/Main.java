package me.sgx;

import imgui.ImGui;
import imgui.type.ImInt;
import me.sgx.engine.audio.AudioSystem;
import me.sgx.engine.graphics.Window;
import me.sgx.engine.graphics.shader.ShaderProgram;
import me.sgx.engine.graphics.texture.Texture;
import me.sgx.engine.io.Keyboard;
import me.sgx.engine.io.Mouse;
import me.sgx.engine.math.MathUtil;
import me.sgx.engine.math.Time;
import me.sgx.gd.graphics.Camera;
import me.sgx.gd.graphics.Drawable;
import me.sgx.gd.world.*;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.io.*;
import java.util.Random;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class Main {
	private static long seed = new Random().nextLong();

	private record Structure(byte[][] info) {
		public void placeAt(World world, Vector2f position) {
			for(int y = 0; y < info.length; y++)
				for(int x = 0; x < info[y].length; x++)
					if(info[y][x] > -1) world.blocks.add(new PlacedBlock(info[y][x], new Transform(new Vector2f(x, info.length - 1.0f - y).add(position), new Vector2f(1.0f), 0.0f)));
		}

	}
	private static final Structure michigunSpikes = new Structure(new byte[][] {
			{ 1, 1, 1 }
	});
	private static final Structure madnessPipes = new Structure(new byte[][] {
			{ -1, -1, -1, -1, -1, -1, 0 },
			{ -1, -1, -1,  0,  3, -1, 0 },
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
			world.blocks.add(new PlacedBlock((byte) 0, new Transform(position, new Vector2f(1.0f), 0.0f)));
			world.blocks.add(new PlacedBlock((byte) 0, new Transform(new Vector2f(position).add(0.0f, 6.0f), new Vector2f(1.0f), 180.0f)));

			for(int i = 0; i < 5; i++) {
				if(new Random(seed + x * 3964 + i * 3728).nextFloat() <= 0.035f && passBuffer < 0) {
					world.blocks.add(new PlacedBlock((byte) (2 + i), new Transform(new Vector2f(0.0f, 2.0f).add(position), new Vector2f(1.0f), 0.0f)));
					passBuffer = 1;
				}
			}

			if(x < -3) continue;

			if(new Random(seed + x * 32893).nextFloat() <= 0.015f && passBuffer < 0) {
				michigunSpikes.placeAt(world, new Vector2f(0.0f, 1.0f).add(position));
				passBuffer = michigunSpikes.info()[0].length;
			}
			if(new Random(seed + x * 32893 + 3724).nextFloat() <= 0.01f && passBuffer < 0) {
				madnessPipes.placeAt(world, new Vector2f(0.0f, 1.0f).add(position));
				passBuffer = madnessPipes.info()[0].length;
			}
			if(new Random(seed + x * 32893 + 39240).nextFloat() <= 0.03f && passBuffer < 0) {
				gapePipe.placeAt(world, new Vector2f(0.0f, 1.0f).add(position));
				passBuffer = madnessPipes.info()[0].length;
			}

			passBuffer--;
		}
	}

	private static byte[] floatToByteArray(float value) {
		int bits = Float.floatToIntBits(value);
		return new byte[] { (byte) (bits >> 24), (byte) (bits >> 16), (byte) (bits >> 8), (byte) bits };
	}

	private static float byteArrayToFloat(byte[] bytes) {
		return Float.intBitsToFloat(bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF));
	}

	private static byte[] byteArrayFromVector2f(Vector2f value) {
		byte[] x = floatToByteArray(value.x());
		byte[] y = floatToByteArray(value.y());

		byte[] result = new byte[x.length + y.length];
		System.arraycopy(x, 0, result, 0, x.length);
		System.arraycopy(y, 0, result, x.length, y.length);

		return result;
	}
	private static Vector2f byteArrayToVector2f(byte[] bytes) {
		byte[] x = new byte[4];
		byte[] y = new byte[4];

		System.arraycopy(bytes, 0, x, 0, x.length);
		System.arraycopy(bytes, 4, y, 0, y.length);

		return new Vector2f(byteArrayToFloat(x), byteArrayToFloat(y));
	}

	private static void saveWorld(World world) throws IOException {
		File file = new File("res/levels/test.bin");
		file.getParentFile().mkdirs();
		file.createNewFile();

		DeflaterOutputStream fileOutputStream = new DeflaterOutputStream(new FileOutputStream(file));
		for(PlacedBlock block : world.blocks) {
			byte[] result = new byte[21]; // 1[id] + 8[position] + 8[scale] + 4[rotation]
			result[0] = block.id;

			System.arraycopy(byteArrayFromVector2f(block.transform.position), 0, result, 1, 8);
			System.arraycopy(byteArrayFromVector2f(block.transform.scale), 0, result, 9, 8);
			System.arraycopy(floatToByteArray(block.transform.rotation), 0, result, 17, 4);

			fileOutputStream.write(result);
		}

		fileOutputStream.flush();
		fileOutputStream.close();
	}

	private static void loadWorld(World world) {
		try(InflaterInputStream fileInputStream = new InflaterInputStream(new FileInputStream("res/levels/test.bin"))) {
			byte[] bytes = fileInputStream.readAllBytes();
			for(int i = 0; i < bytes.length / 21; i++) {
				byte[] position = new byte[8], scale = new byte[8], rotation = new byte[4];

				System.arraycopy(bytes, i * 21 + 1, position, 0, position.length);
				System.arraycopy(bytes, i * 21 + 9, scale, 0, scale.length);
				System.arraycopy(bytes, i * 21 + 17, rotation, 0, rotation.length);

				world.blocks.add(new PlacedBlock(bytes[i * 21], new Transform(byteArrayToVector2f(position), byteArrayToVector2f(scale), byteArrayToFloat(rotation))));
			}
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	public static void main(String[] args) throws IOException {
		Window.create(1920, 1080, "Geometry Dash", true, false, false);
		Window.initImGui(true);

		Keyboard.initialize();
		Mouse.initialize();

		AudioSystem.initialize();

		Drawable.initialize();

		GL11.glClearColor(0.23f, 0.63f, 1.0f, 1.0f);

		Block.initialize();
		World.initialize("Electrodynamix");

		World world = new World();
		world.spawnPoint = new Vector2f(0.0f, 4.0f);

		//for(int i = 0; i < 128; i++) generateWorld(world, i);
		//saveWorld(world);
		loadWorld(world);

		//System.exit(0);

		Camera camera = new Camera(new Vector2f(), new Vector2f(0.25f), 0.0f);

		world.player = new CubePlayer();
		world.restart(camera);

		int fps = 0;
		float fpsUpdateTime = 0.0f;

		Time time = new Time();
		while(Window.isRunning()) {
			Window.update();
			time.update();

			fps++;
			fpsUpdateTime += time.getActualDelta();
			if(fpsUpdateTime >= 1.0f) {
				fpsUpdateTime = 0.0f;

				Window.setTitle("Geometry Dash | FPS: " + fps);
				fps = 0;
			}

			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

			Drawable.begin(camera.position, camera.zoom, camera.rotation);

			world.player.render();
			world.render();

			Drawable.end();

			Window.imGuiBegin();

			ImGui.begin("Debug");

			float[] timeScale = new float[] { time.scale };

			ImGui.dragFloat("Time Scale", timeScale, 0.001f, 0.0f, 2.0f);
			time.scale = timeScale[0];

			ImGui.end();

			Window.imGuiEnd();

			world.player.update(time, world, camera);
			world.update(time);

			camera.position.x = world.player.transform.position.x() + 2.5f;
			camera.position.y = MathUtil.lerp(camera.position.y(), Math.round(world.player.transform.position.y() / 2.0f) * 2.0f, 2.0f * time.getDelta());

			if(Keyboard.isKeyJustPressed(GLFW.GLFW_KEY_F11)) Window.setFullscreen(!Window.isFullscreen());
			if(Keyboard.isKeyJustPressed(GLFW.GLFW_KEY_ESCAPE)) break;
		}

		world.clear();

		Texture.clearAll();
		ShaderProgram.clearAll();
		AudioSystem.clearAll();
		Block.clear();

		Window.closeImGui();
		Window.close();
	}
}