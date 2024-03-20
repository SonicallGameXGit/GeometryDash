package me.sgx.gd.world;

import me.sgx.engine.io.Keyboard;
import me.sgx.engine.io.Mouse;
import me.sgx.engine.math.Time;
import me.sgx.gd.graphics.Camera;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

public class BallPlayer extends Player {
	private static final float GRAVITY = 50.0f, ROTATE_SPEED = 60.0f, COYOTE_TIME = 0.15f, JUMP_BUFFER_TIME = 0.075f;

	private float coyoteTimer = 0.0f, jumpBufferTimer = 0.0f;
	private boolean onGround = false, jumping = false, jumpingOnOrb = false, direction = false, rotateDirection = false;

	public BallPlayer() {
		super(Player.BALL_TEXTURE, new Collider(), new Collider(new Vector2f(0.25f), new Vector2f(0.5f)));
	}
	public BallPlayer(Player origin) {
		super(Player.BALL_TEXTURE, new Collider(), new Collider(new Vector2f(0.25f), new Vector2f(0.5f)));
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
		boolean onGround = resolveCollisionsAndGetOnGround(time, world, camera, Collider.ClipType.BOTH);

		if(onGround) {
			System.out.println("true");
			this.onGround = true;
			coyoteTimer = 0.0f;
		} else {
			coyoteTimer += time.getDelta();
			if(coyoteTimer >= COYOTE_TIME) this.onGround = false;
		}

		transform.rotation += ROTATE_SPEED * speed * (rotateDirection ? -1.0f : 1.0f) * (onGround ? 1.0f : 0.75f) * time.getDelta();
	}

	private void control(Time time) {
		velocity.x = speed;
		velocity.y -= GRAVITY * physicsSpeed * time.getDelta();

		if(Keyboard.isKeyJustPressed(GLFW.GLFW_KEY_SPACE) || Mouse.isButtonJustPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT))
			jumpingOnOrb = true;
		if(Keyboard.isKeyJustPressed(GLFW.GLFW_KEY_SPACE) || Mouse.isButtonJustPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
			jumping = true;
			jumpBufferTimer = 0.0f;
		} else {
			jumpBufferTimer += time.getDelta();
			if(jumpBufferTimer >= JUMP_BUFFER_TIME) {
				jumping = false;
				jumpingOnOrb = false;
			}
		}

		physicsSpeed = Math.abs(physicsSpeed) * (direction ? -1.0f : 1.0f);

		if(onGround) {
			jumpingOnOrb = false;

			if(jumping) {
				direction = !direction;
				jumping = false;
				onGround = false;

				velocity.y = 0.0f;
			} else rotateDirection = physicsSpeed >= 0.0f;
		}
	}
}
