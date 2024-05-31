package me.sgx.gd.scene.custom;

import imgui.ImGui;
import me.sgx.engine.audio.SoundSource;
import me.sgx.engine.graphics.Window;
import me.sgx.engine.io.Keyboard;
import me.sgx.engine.io.Mouse;
import me.sgx.gd.graphics.Camera;
import me.sgx.gd.graphics.Graphics;
import me.sgx.gd.graphics.Sprite;
import me.sgx.gd.io.LevelParser;
import me.sgx.gd.scene.Scene;
import me.sgx.gd.scene.SceneSystem;
import me.sgx.gd.world.World;
import me.sgx.gd.world.block.Block;
import me.sgx.gd.world.block.Blocks;
import me.sgx.gd.world.block.PlacedBlock;
import me.sgx.gd.world.math.Collider;
import me.sgx.gd.world.math.Transform;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;

public class LevelEditorScene extends Scene {
	private String song;
	public boolean playing = false;

	private final Resources resources = new Resources();
	private byte selection = Byte.MIN_VALUE;

	private final Vector2f mouseClickPos = new Vector2f();
	private boolean dragging = false, canPlaceBlock = false, canDestroyBlock = false, hoveringImGui = false;

	private PlacedBlock selectedBlock = null;

	private float[] selectedBlockPosition = new float[2];
	private float[] selectedBlockSize = { 1.0f, 1.0f };

	private final float[] selectedBlockRotation = new float[1];

	private static class Resources {
		public final Sprite selectionPreview = new Sprite(new Transform());

		public void initialize() {
			selectionPreview.texture = World.getTextureAtlas();
		}
		public void update(Vector4f viewRange) {
			selectionPreview.transform.position.x = Camera.main.position.x() + viewRange.x() + selectionPreview.transform.size.x() / 2.0f + 0.1f / Camera.main.zoom.x();
			selectionPreview.transform.position.y = Camera.main.position.y() + viewRange.y() - selectionPreview.transform.size.y() / 2.0f - 0.1f / Camera.main.zoom.y();
			selectionPreview.transform.size.set(new Vector2f(0.05f).div(Camera.main.zoom));
		}
		public void render() {
			selectionPreview.render();
		}
	}

	@Override
	public void initialize() {
		super.initialize();

		Object menuLoop = SceneSystem.globalData.get(MainMenuScene.class + "/menu_loop");
		if(menuLoop instanceof SoundSource menuLoopSource) menuLoopSource.stop();

		JFileChooser chooser = new JFileChooser(System.getProperty("user.dir") + "/res/levels/");
		chooser.addChoosableFileFilter(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().endsWith(".bin");
			}
			@Override
			public String getDescription() {
				return ".bin";
			}
		});

		if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			String[] extensions = file.getName().split("\\.");

			song = extensions[Math.max(extensions.length - 2, 0)];
			LevelParser.load(file.toString());
		} else {
			JFileChooser songChooser = new JFileChooser(System.getProperty("user.dir") + "/res/music/");
			songChooser.addChoosableFileFilter(new FileFilter() {
				@Override
				public boolean accept(File f) {
					return f.isDirectory() || f.getName().endsWith(".ogg");
				}
				@Override
				public String getDescription() {
					return ".ogg";
				}
			});
			if(songChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				File file = songChooser.getSelectedFile();
                String[] extensions = file.getName().split("\\.");

				song = extensions[Math.max(extensions.length - 2, 0)];
				World.loadSong(song);
            }
		}

		Camera.main = new Camera();
		Camera.main.zoom.set(0.25f);

		World.stopMusic();

		resources.initialize();
	}

	@Override
	public void update() {
		if(playing) {
			World.update();
			Camera.main.follow(World.player);
		} else {
			editorUpdate();
		}

		if(Keyboard.isKeyJustPressed(GLFW.GLFW_KEY_F5)) {
			playing = !playing;
			if(playing) {
				World.time.update();
				World.respawn(false);

				Camera.main.zoom.set(0.25f);
			} else World.pauseMusic();
		}
		if(Keyboard.isKeyJustPressed(GLFW.GLFW_KEY_F6) && !playing) {
			playing = true;

			World.time.update();
			Camera.main.zoom.set(0.25f);

			World.unpauseMusic();
		}

		if(Keyboard.isKeyJustPressed(GLFW.GLFW_KEY_F1)) {
			LevelParser.save("res/levels/" + song + ".bin");
		}
	}
	@Override
	public void render() {
		World.render();
		if(!playing) resources.render();

		hoveringImGui = false;
		if(selectedBlock != null) {
			Window.imGuiBegin();

			ImGui.begin("Object");

			ImGui.text("Transform");
			ImGui.separator();

			ImGui.dragFloat2("Position", selectedBlockPosition, 0.1f);
			ImGui.dragFloat2("Size", selectedBlockSize, 0.1f);
			ImGui.dragFloat("Rotation", selectedBlockRotation, 0.1f);

			if(ImGui.isWindowHovered() || ImGui.isAnyItemHovered()) hoveringImGui = true;

			ImGui.end();

			Window.imGuiEnd();
		}
	}
	@Override
	public void postProcess() {
		Graphics.setPostColor(new Vector3f(1.0f));
	}

	private void editorUpdate() {
		Vector2f size = screenToHud(new Vector2f(Window.getSize()));
		resources.update(new Vector4f(-size.x(), -size.y(), size.x(), size.y()));

		if(playing) selectedBlock = null;

		cycleSelection();

		if(!hoveringImGui) {
			moveCamera();
			zoomCamera();

			build();
			destroy();
		}

		if(selectedBlock != null) {
			selectedBlock.transform.position.set(selectedBlockPosition);
			selectedBlock.transform.size.set(selectedBlockSize);
			selectedBlock.transform.rotation = selectedBlockRotation[0];
		}
	}

	private void cycleSelection() {
		if(Keyboard.isKeyJustPressed(GLFW.GLFW_KEY_EQUAL)) {
			selection = (byte) (selection + 1 >= Blocks.getMaxId() ? Byte.MIN_VALUE : selection + 1);
		}
		if(Keyboard.isKeyJustPressed(GLFW.GLFW_KEY_MINUS)) {
			selection = (byte) (selection - 1 < Byte.MIN_VALUE ? Blocks.getMaxId() - 1 : selection - 1);
		}

		Block block = Blocks.getById(selection);
		if(block != null) resources.selectionPreview.uv = block.texture;
	}
	private void moveCamera() {
		if(Mouse.isButtonJustPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)) mouseClickPos.set(screenToWorld(Mouse.getPosition()));
		if(Mouse.isButtonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
			Vector2f position = screenToWorld(Mouse.getPosition());
			float toClickDistance = mouseClickPos.distance(position);

			if(toClickDistance >= 0.3f || dragging) {
				Camera.main.position.add(normalizeScreen(Mouse.getVelocity().negate()));
				if(!dragging) dragging = true;
			}
		} else dragging = false;
	}
	private void zoomCamera() {
		Camera.main.zoom.add(new Vector2f(Mouse.getScrollVelocity().y() / 10.0f).mul(Camera.main.zoom));
		Camera.main.zoom.min(new Vector2f(10.0f));
		Camera.main.zoom.max(new Vector2f(0.01f));
	}
	private void build() {
		if(dragging) {
			canPlaceBlock = false;
			return;
		}

		if(Mouse.isButtonJustPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)) canPlaceBlock = true;
		if(!Mouse.isButtonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT) && canPlaceBlock) {
			canPlaceBlock = false;

			Block block = Blocks.getById(selection);
			Vector2f pos = screenToWorld(Mouse.getPosition()).round();

			if(block != null) {
				for(int i = World.blocks.size() - 1; i >= 0; i--) {
					PlacedBlock placedBlock = World.blocks.get(i);
                    if(new Collider().intersects(new Transform(pos, new Vector2f(0.001f)), placedBlock.transform, new Collider())) {
						selectedBlock = placedBlock;
						selectedBlockPosition = new float[] {
								placedBlock.transform.position.x(),
								placedBlock.transform.position.y()
						};
						selectedBlockSize = new float[] {
								placedBlock.transform.size.x(),
								placedBlock.transform.size.y()
						};
						selectedBlockRotation[0] = placedBlock.transform.rotation;

						return;
					}
				}

				World.blocks.add(new PlacedBlock(block, new Transform(pos, new Vector2f(selectedBlockSize), selectedBlockRotation[0])));
			}
		}
	}
	private void destroy() {
		if(dragging) {
			canDestroyBlock = false;
			return;
		}

		if(Mouse.isButtonJustPressed(GLFW.GLFW_MOUSE_BUTTON_RIGHT)) canDestroyBlock = true;
		if(!Mouse.isButtonPressed(GLFW.GLFW_MOUSE_BUTTON_RIGHT) && canDestroyBlock) {
			canDestroyBlock = false;
			selectedBlock = null;

			Vector2f pos = screenToWorld(Mouse.getPosition());
			for(int i = World.blocks.size() - 1; i >= 0; i--) {
				PlacedBlock placedBlock = World.blocks.get(i);
				if(new Collider().intersects(new Transform(pos, new Vector2f(0.001f)), placedBlock.transform, new Collider())) {

					World.blocks.remove(placedBlock);
					break;
				}
			}
		}
	}

	private Vector2f screenToWorld(Vector2f screen) {
		return screenToHud(screen).add(Camera.main.position);
	}
	private Vector2f screenToHud(Vector2f screen) {
		return new Vector2f(screen.x(), Window.getHeight() - screen.y())
				.div(Window.getWidth(), Window.getHeight())
				.mul(2.0f).sub(1.0f, 1.0f)
				.mul((float) Window.getWidth() / Window.getHeight(), 1.0f)
				.div(Camera.main.zoom);
	}
	private Vector2f normalizeScreen(Vector2f screen) {
		return new Vector2f(screen.x(), -screen.y())
				.div(Window.getWidth(), Window.getHeight())
				.mul((float) Window.getWidth() / Window.getHeight(), 1.0f)
				.div(Camera.main.zoom)
				.mul(2.0f);
	}
}