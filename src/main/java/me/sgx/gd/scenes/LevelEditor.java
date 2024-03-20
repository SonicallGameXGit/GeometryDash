package me.sgx.gd.scenes;

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
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class LevelEditor {
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
		Window.create(1920, 1080, "Geometry Dash [Editor]", true, false, false);
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
		//world.blocks.add(new PlacedBlock((byte) 0, new Transform(new Vector2f(), new Vector2f(1.0f), 0.0f)));

		//System.exit(0);

		Camera camera = new Camera(new Vector2f(), new Vector2f(0.25f), 0.0f);

		int fps = 0;
		float fpsUpdateTime = 0.0f;

		ImInt selectedBlockId = new ImInt(0);
		String[] blockTypes = new String[] { "Block", "Spike", "Yellow Orb", "Pink Orb", "Blue Orb", "Green Orb", "Red Orb", "Black Orb", "Cube Portal", "Ship Portal", "Ball Portal", "Up Gravity Portal", "Down Gravity Portal" };

		Transform selectionTransform = new Transform(new Vector2f(), new Vector2f(1.0f), 0.0f);
		Drawable selection = new Drawable() {
			@Override
			public void render() {
				render(selectionTransform, Block.getById(selectedBlockId.byteValue()).uv);
			}
		};

		Vector2f cameraClickPosition = new Vector2f();
		boolean canMouseRelease = true, canChangeBlockId = true;

		Time time = new Time();
		while(Window.isRunning()) {
			Window.update();
			time.update();

			if(Keyboard.isKeyJustPressed(GLFW.GLFW_KEY_E)) selectionTransform.rotation += 90.0f;
			if(Keyboard.isKeyJustPressed(GLFW.GLFW_KEY_Q)) selectionTransform.rotation -= 90.0f;

			int blockIdChange = (int) Math.signum(Mouse.getScrollVelocity().x());
			if(blockIdChange != 0) {
				if(canChangeBlockId) canChangeBlockId = false;
				selectedBlockId.set(Math.max(Math.min(selectedBlockId.get() + blockIdChange, blockTypes.length - 1), 0));
			} else canChangeBlockId = true;

			float aspect = (float) Window.getWidth() / Window.getHeight();

			fps++;
			fpsUpdateTime += time.getActualDelta();
			if(fpsUpdateTime >= 1.0f) {
				fpsUpdateTime = 0.0f;

				Window.setTitle("Geometry Dash [Editor] | FPS: " + fps);
				fps = 0;
			}

			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

			Drawable.begin(camera.position, camera.zoom, camera.rotation);

			if(world.player != null) world.player.render();
			world.render();

			if(world.player != null) world.player.update(time, world, camera);
			world.update(time);

			if(world.player != null) {
				camera.position.x = world.player.transform.position.x() + 2.5f;
				camera.position.y = MathUtil.lerp(camera.position.y(), Math.round(world.player.transform.position.y() / 2.0f) * 2.0f, 2.0f * time.getDelta());
			} else {
				world.stopMusic();

				Vector2f mousePosition = Mouse.getPosition();
				mousePosition.div(new Vector2f(Window.getSize()));
				mousePosition.mul(2.0f).sub(new Vector2f(1.0f));

				mousePosition.x *= aspect;
				mousePosition.y *= -1.0f;

				mousePosition.div(camera.zoom);

				Vector2f position = new Vector2f(camera.position).add(mousePosition);
				position = Keyboard.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT) ? position.round() : position;

				selectionTransform.position.set(position);

				if(Mouse.isButtonJustPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)) cameraClickPosition.set(Mouse.getPosition());
				if((Mouse.isButtonJustPressed(GLFW.GLFW_MOUSE_BUTTON_RIGHT) || Mouse.isButtonPressed(GLFW.GLFW_MOUSE_BUTTON_RIGHT) && Keyboard.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT)) && Mouse.getPosition().distance(new Vector2f(0.0f)) > new Vector2f(Window.getSize()).div(4.0f).length()) {
					int i = 0;
					for(PlacedBlock placedBlock : world.blocks) {
						if(new Collider().intersects(placedBlock.transform, new Transform(position, new Vector2f(1.0f), 0.0f), new Collider(new Vector2f(0.5f), new Vector2f(0.0f))))
							break;
						i++;
					}

					if(i < world.blocks.size()) world.blocks.remove(i);
				}
				if(Mouse.isButtonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
					if(cameraClickPosition.distance(Mouse.getPosition()) > 50) {
						Vector2f velocity = Mouse.getVelocity().div(new Vector2f(Window.getSize())).mul(2.0f).div(camera.zoom);
						camera.position.add(-velocity.x() * aspect, velocity.y());
					}

					canMouseRelease = true;
				} else {
					if(canMouseRelease) {
						if(cameraClickPosition.distance(Mouse.getPosition()) <= 50 && Mouse.getPosition().distance(new Vector2f(0.0f)) > new Vector2f(Window.getSize()).div(4.0f).length()) {
							PlacedBlock placedBlock = new PlacedBlock(selectedBlockId.byteValue(), new Transform(position, new Vector2f(1.0f), 0.0f));
							Block block = Block.getById(placedBlock.id);

							if(block instanceof CubePortal || block instanceof ShipPortal || block instanceof BallPortal || block instanceof UpGravityPortal || block instanceof DownGravityPortal)
								placedBlock.transform.scale.y = 2.0f;
							placedBlock.transform.rotation = selectionTransform.rotation;

							world.blocks.add(placedBlock);
						}

						canMouseRelease = false;
					}
				}

				camera.zoom.add(new Vector2f(Mouse.getScrollVelocity().y()).mul(camera.zoom.length() * 0.05f));
			}

			if(world.player == null) selection.render();
			if(Keyboard.isKeyJustPressed(GLFW.GLFW_KEY_F5)) {
				if(world.player == null) {
					world.player = new CubePlayer();
					camera.zoom = new Vector2f(0.25f);

					world.restart(camera);
				}
				else world.player = null;
			}
			if(Keyboard.isKeyJustPressed(GLFW.GLFW_KEY_F1)) saveWorld(world);

			Drawable.end();

			Window.imGuiBegin();

			ImGui.begin("Debug");

			float[] timeScale = new float[] { time.scale };

			ImGui.dragFloat("Time Scale", timeScale, 0.001f, 0.0f, 2.0f);
			time.scale = timeScale[0];

			ImGui.combo("Block", selectedBlockId, blockTypes);

			ImGui.end();

			Window.imGuiEnd();

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