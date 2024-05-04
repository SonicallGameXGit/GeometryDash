package me.sgx.gd.graphics.gui;

import me.sgx.engine.graphics.Window;
import me.sgx.engine.io.Mouse;
import me.sgx.gd.graphics.Camera;
import me.sgx.gd.graphics.Sprite;
import me.sgx.gd.world.math.Transform;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

public class Button extends Sprite {
	private boolean hovered = false, pressed = false, justPressed = false, released = false, canJustPress = false, canRelease = false;
	public boolean disabled = false;

	public Button(String texture, Transform transform) {
		super(transform);
		this.texture = texture;
	}
	public Button(String texture, Transform transform, Vector4f uv) {
		super(transform);
		this.texture = texture;
		this.uv = uv;
	}
	public Button(String texture, Transform holder, Transform transform) {
		super(holder, transform);
		this.texture = texture;
	}
	public Button(String texture, Transform holder, Transform transform, Vector4f uv) {
		super(holder, transform);

		this.texture = texture;
		this.uv = uv;
	}

	public void update() {
		Vector2f holderPos = holder == null ? new Vector2f() : holder.position;
		Vector2f holderSize = holder == null ? new Vector2f(1.0f) : holder.size;

		Vector2f size = new Vector2f(transform.size).mul(holderSize).div(2.0f);
		Vector2f min = new Vector2f(holderPos).add(transform.position).sub(size).sub(Camera.main.position);
		Vector2f max = new Vector2f(min).add(size.x() * 2.0f, size.y() * 2.0f);

		boolean mousePressed = Mouse.isButtonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT);

		Vector2f mousePos = new Vector2f(Mouse.getX(), Window.getHeight() - Mouse.getY());
		mousePos.div(new Vector2f(Window.getSize()));
		mousePos.mul(2.0f).sub(1.0f, 1.0f);
		mousePos.x *= (float) Window.getWidth() / Window.getHeight();
		mousePos.div(Camera.main.zoom);

		hovered = mousePos.x() >= min.x() && mousePos.x() <= max.x() && mousePos.y() >= min.y() && mousePos.y() <= max.y();
		if(!disabled) {
			pressed = hovered && mousePressed;

			justPressed = false;
			if(pressed) {
				if(canJustPress) {
					justPressed = true;
					canJustPress = false;
				}
			} else canJustPress = true;

			released = false;
			if(!pressed) {
				if(canRelease) {
					released = true;
					canRelease = false;
				}
			} else canRelease = true;
		} else {
			justPressed = false;
			pressed = false;
			released = false;
		}
	}

	public boolean isHovered() {
        return hovered;
    }
	public boolean isPressed() {
		return pressed;
	}
	public boolean isJustPressed() {
		return justPressed;
	}
	public boolean isReleased() {
		return released;
	}
}