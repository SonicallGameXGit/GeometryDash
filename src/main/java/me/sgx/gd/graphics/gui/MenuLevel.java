package me.sgx.gd.graphics.gui;

import me.sgx.engine.graphics.Window;
import me.sgx.gd.graphics.Camera;
import me.sgx.gd.scene.SceneSystem;
import me.sgx.gd.scene.custom.LevelScene;
import me.sgx.gd.world.math.Transform;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class MenuLevel {
	public final String name;
	private final Vector3f color = new Vector3f(1.0f);

	public boolean disabled = false;

	private final float backgroundSize = 0.6f;
	private final BouncingButton background = new BouncingButton("gui/level_menu", new Transform(
			new Vector2f(),
            new Vector2f(backgroundSize / 0.28f, backgroundSize)
	), new Vector4f(0.0f, 0.0f, 0.895f, 0.25f));

	public MenuLevel(String name, Vector3f color) {
		this.name = name;
		this.color.set(color);

		background.texture = "gui/level_menu";
    }

	public void update() {
		background.disabled = disabled;

        background.update();
		background.transform.position.y = backgroundSize / 2.0f;
		background.color.set(background.color);

		if(background.isReleased() && background.isHovered()) SceneSystem.setScene(new LevelScene(name));
    }
	public void render(int index) {
		background.transform.position.x = index * 2.0f * ((float) Window.getWidth() / Window.getHeight() / Camera.main.zoom.x());
		background.render();
	}

	public Vector3f getColor() {
		return new Vector3f(color);
	}
}