package me.sgx.gd.graphics;

import me.sgx.engine.graphics.texture.Texture;
import org.lwjgl.opengl.GL11;

public class Textures {
	public static final int GUI_LEVELMENU = load("gui/level_menu");
	public static final int GUI_MAINMENU = load("gui/main_menu");

	public static final int PLAYERMODE_CUBE = load("player/cube2");
	public static final int PLAYERMODE_BALL = load("player/ball");
	public static final int PLAYERMODE_SHIP = load("player/ship2");
	public static final int PLAYERMODE_SWINGCOPTER = load("player/swing_copter");
	public static final int PLAYERMODE_ROBOT = load("player/robot");
	public static final int PLAYERMODE_SPIDER = load("player/spider");

	public static final int WORLD_GROUND = load("world/ground");
	public static final int WORLD_GROUNDHIGHLIGHT = load("world/ground_highlight");
	public static final int WORLD_BACKGROUND = load("world/background");

	private static int load(String path) {
		return Texture.loadFromFile("res/textures/" + path + ".png", GL11.GL_LINEAR);
	}
	public static void initialize() {}
}