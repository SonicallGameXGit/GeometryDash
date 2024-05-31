package me.sgx.gd.world.block;

import me.sgx.gd.world.math.Collider;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class Block {
	private static final HashMap<String, Vector4f> textures = new HashMap<>();
	private static byte lastId = -128;

	public final byte id;

	public final Collider resolveCollider, damageCollider, triggerCollider;
	public final Vector4f texture;
	public final Vector2f customScale;

	public Block() {
		this.texture = new Vector4f(0.0f, 0.0f, 1.0f, 1.0f);

		id = next();

		resolveCollider = new Collider();
		damageCollider = new Collider();
		triggerCollider = new Collider();
		customScale = new Vector2f(1.0f);

		Blocks.register(this);
	}
	public Block(Vector4f texture) {
		this.texture = texture;

		id = next();

		resolveCollider = new Collider();
		damageCollider = new Collider();
		triggerCollider = new Collider();
		customScale = new Vector2f(1.0f);

		Blocks.register(this);
	}
	public Block(Collider resolveCollider, Collider damageCollider, Collider triggerCollider, Vector4f texture) {
		this.resolveCollider = resolveCollider == null ? null : new Collider(resolveCollider);
		this.damageCollider = damageCollider == null ? null : new Collider(damageCollider);
		this.triggerCollider = triggerCollider == null ? null : new Collider(triggerCollider);

		this.texture = texture;
		customScale = new Vector2f(1.0f);

		id = next();
		Blocks.register(this);
	}
	public Block(Collider resolveCollider, Collider damageCollider, Collider triggerCollider, Vector4f texture, Vector2f customScale) {
		this.resolveCollider = resolveCollider == null ? null : new Collider(resolveCollider);
		this.damageCollider = damageCollider == null ? null : new Collider(damageCollider);
		this.triggerCollider = triggerCollider == null ? null : new Collider(triggerCollider);

		this.texture = texture;
		this.customScale = customScale;

		id = next();
		Blocks.register(this);
	}

	private record ImageInfo(String path, Image image) {}
	private static ArrayList<ImageInfo> loadImages(String prevPath, ArrayList<ImageInfo> prevImages) {
		ArrayList<ImageInfo> images = prevImages != null ? new ArrayList<>(prevImages) : new ArrayList<>();

		File baseFile = new File(prevPath);
		File[] files = baseFile.listFiles();

		if (files != null) {
			for (File file : files) {
                if (file.isDirectory()) {
					images = loadImages(file.getPath(), images);
				} else if (prevImages != null) {
					try {
						images.add(new ImageInfo(file.getPath(), ImageIO.read(file)));
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
            }
		}

		return images;
	}
	public static int buildTextureAtlas() {
		ArrayList<ImageInfo> images = loadImages("res/textures/world", null);

		int padding = 6;

		int width = 0;
		int height = 0;
		for (ImageInfo info : images) {
			width += info.image.getWidth(null) + padding;
			height = Math.max(height, info.image.getHeight(null));
		}

		// TODO: Add pixel size constant and set it's value here

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics graphics = image.getGraphics();

		int offset = 0;
		for (ImageInfo info : images) {
			String[] formats = info.path.split("\\.");
			textures.put(
					info.path
							.replace("\\", "/")
							.replace("res/textures/world/", "")
							.replace('.' + formats[formats.length - 1], ""),
					new Vector4f((float) offset / (float) width, 1.0f, (float) info.image.getWidth(null) / (float) width, -1.0f)
			);

			graphics.drawImage(info.image, offset, 0, null);
			offset += info.image.getWidth(null) + padding;
		}
		graphics.dispose();

		ByteBuffer buffer = ByteBuffer.allocateDirect(image.getWidth() * image.getHeight() * 4);

		int[] pixels = new int[image.getWidth() * image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

		for(int h = 0; h < image.getHeight(); h++) {
			for(int w = 0; w < image.getWidth(); w++) {
				int pixel = pixels[h * image.getWidth() + w];

				buffer.put((byte) ((pixel >> 16) & 0xFF));
				buffer.put((byte) ((pixel >> 8) & 0xFF));
				buffer.put((byte) (pixel & 0xFF));
				buffer.put((byte) ((pixel >> 24) & 0xFF));
			}
		}

		buffer.flip();

		int texture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL14.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL14.GL_CLAMP_TO_EDGE);

		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		return texture;
	}

	public void place(PlacedBlock placed) {}
	public void update(PlacedBlock placed) {}

	private byte next() {
		return lastId++;
	}

	public static Vector4f getTexture(String name) {
		return Block.textures.getOrDefault(name, new Vector4f());
	}
}