package me.sgx.gd.world;

import me.sgx.engine.io.Keyboard;
import me.sgx.engine.io.Mouse;
import me.sgx.engine.math.MathUtil;
import me.sgx.engine.math.Time;
import me.sgx.gd.graphics.Camera;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

public class CubePlayer extends Player {
	private static final float GRAVITY = 75.0f, JUMP_HEIGHT = 17.32f, COYOTE_TIME = 0.15f, JUMP_BUFFER_TIME = 0.075f;

	private float coyoteTimer = 0.0f, jumpBufferTimer = 0.0f;
	private boolean onGround = false, jumping = false, jumpingOnOrb = false;

	public CubePlayer() {
		super(Player.CUBE_TEXTURE, new Collider(), new Collider(new Vector2f(0.25f), new Vector2f(0.5f)));
	}
	public CubePlayer(Player origin) {
		super(Player.CUBE_TEXTURE, new Collider(), new Collider(new Vector2f(0.25f), new Vector2f(0.5f)));

		copyFrom(origin);
		transform.scale.set(1.0f);
	}

	@Override
	protected void onTriggerTouch(World world, Block block) {
		super.onTriggerTouch(world, block);
		if(jumpingOnOrb || Keyboard.isKeyJustPressed(GLFW.GLFW_KEY_SPACE) || Mouse.isButtonJustPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
			block.onJustPress(world);
			jumpingOnOrb = false;
		}
	}

	@Override
	public void update(Time time, World world, Camera camera) {
		super.update(time, world, camera);

		control(time);
		boolean onGround = resolveCollisionsAndGetOnGround(time, world, camera, physicsSpeed >= 0.0 ? Collider.ClipType.DOWN : Collider.ClipType.UP);

		if(onGround) {
			this.onGround = true;
			coyoteTimer = 0.0f;

			transform.rotation = MathUtil.lerp(transform.rotation, Math.round(transform.rotation / 90.0f) * 90.0f, 48.0f * time.getDelta()); // TODO: Replace 90 degrees to (90 degrees + object normal as angle)
		} else {
			coyoteTimer += time.getDelta();
			if(coyoteTimer >= COYOTE_TIME) this.onGround = false;

			transform.rotation += -360.0f * (velocity.x() >= 0.0f ? 1.0f : -1.0f) * (physicsSpeed >= 0.0f ? 1.0f : -1.0f) * time.getDelta();
		}
	}

	private void control(Time time) {
		velocity.x = speed;
		velocity.y -= GRAVITY * physicsSpeed * time.getDelta();

		if(Keyboard.isKeyJustPressed(GLFW.GLFW_KEY_SPACE) || Mouse.isButtonJustPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT))
			jumpingOnOrb = true;
		if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_SPACE) || Mouse.isButtonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
			jumping = true;
			jumpBufferTimer = 0.0f;
		} else {
			jumpBufferTimer += time.getDelta();
			if(jumpBufferTimer >= JUMP_BUFFER_TIME) {
				jumping = false;
				jumpingOnOrb = false;
			}
		}

		if(onGround) {
			jumpingOnOrb = false;

			if(jumping) {
				velocity.y = JUMP_HEIGHT * physicsSpeed;
				jumping = false;
				onGround = false;
			}
		}
	}
}