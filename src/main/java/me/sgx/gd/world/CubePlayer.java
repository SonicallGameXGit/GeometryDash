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
	private boolean onGround = false, jumping = false;

	public CubePlayer() {
		super(Player.CUBE_TEXTURE, new Collider(), new Collider(new Vector2f(0.25f), new Vector2f(0.5f)));
	}
	public CubePlayer(Player origin) {
		super(Player.CUBE_TEXTURE, new Collider(), new Collider(new Vector2f(0.25f), new Vector2f(0.5f)));

		copyFrom(origin);
		transform.scale.set(1.0f);
	}

	@Override
	public void update(Time time, World world, Camera camera) {
		super.update(time, world, camera);

		control(time);
		boolean onGround = resolveCollisionsAndGetOnGround(time, world, camera);

		if(onGround) {
			this.onGround = true;
			coyoteTimer = 0.0f;

			transform.rotation = MathUtil.lerp(transform.rotation, Math.round(transform.rotation / 90.0f) * 90.0f, 48.0f * time.getDelta()); // TODO: Replace 90 degrees to (1 / 90 degrees + object normal as angle)
		} else {
			coyoteTimer += time.getDelta();
			if(coyoteTimer >= COYOTE_TIME) this.onGround = false;

			transform.rotation += -360.0f * (velocity.x() >= 0.0f ? 1.0f : -1.0f) * time.getDelta();
		}
	}

	private void control(Time time) {
		velocity.x = speed;
		velocity.y -= GRAVITY * physicsSpeed * time.getDelta();

		if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_SPACE) || Mouse.isButtonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
			jumping = true;
			jumpBufferTimer = 0.0f;
		} else {
			jumpBufferTimer += time.getDelta();
			if(jumpBufferTimer >= JUMP_BUFFER_TIME) jumping = false;
		}

		if(jumping && onGround) {
			velocity.y = JUMP_HEIGHT * physicsSpeed;
			onGround = false;
		}
	}

	private boolean resolveCollisionsAndGetOnGround(Time time, World world, Camera camera) {
		float tempVelocityY = velocity.y() * time.getDelta();
		float originTempVelocityY = tempVelocityY;

		for(Block block : world.blocks) {
			Collider blockResolveCollider = BlockInfo.getById(block.id).resolveCollider();
			Collider blockKillCollider = BlockInfo.getById(block.id).killCollider();

			if (blockResolveCollider != null) {
				tempVelocityY = resolveCollider.clipVelocityY(
						transform, block.transform,
						blockResolveCollider, tempVelocityY, Collider.ClipType.DOWN
				);
			}

			if (blockKillCollider != null && killCollider.intersects(transform, block.transform, blockKillCollider))
				world.restart(this, camera);
		}

		transform.position.x += velocity.x() * time.getDelta();
		transform.position.y += tempVelocityY;

		if(originTempVelocityY != tempVelocityY) {
			velocity.y = 0.0f;
			return true;
		}

		return false;
	}
}