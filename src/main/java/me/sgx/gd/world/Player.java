package me.sgx.gd.world;

import me.sgx.engine.io.Keyboard;
import me.sgx.engine.io.Mouse;
import me.sgx.engine.math.MathUtil;
import me.sgx.engine.math.Time;
import me.sgx.gd.graphics.Drawable;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

public class Player extends Drawable {
	public final int texture;
	public PlayerInfo info = new PlayerInfo();

	public Transform transform = new Transform();
	private final Vector2f velocity = new Vector2f();

	public Collider resolveCollider = new Collider(), killCollider = new Collider();

	private float coyoteTimer = 0.0f, jumpBufferTimer = 0.0f;
	private boolean onGround = false, jumping = false;

	public Player(int texture, Transform transform) {
		this.texture = texture;
		this.transform = transform;
	}
	public Player(int texture) {
		this.texture = texture;
	}

	@Override
	public void render() {
		loadTexture(texture);
		render(transform);
	}

	public void update(Time time, World world) {
		control(time);
		boolean onGround = resolveCollisionsAndGetOnGround(time, world);

		if(onGround) {
			this.onGround = true;
			coyoteTimer = 0.0f;

			transform.rotation = MathUtil.lerp(transform.rotation, Math.round(transform.rotation / 90.0f) * 90.0f, 48.0f * time.getDelta()); // TODO: Replace 90 degrees to (1 / 90 degrees + object normal as angle)
		} else {
			coyoteTimer += time.getDelta();
			if(coyoteTimer >= info.coyoteTime) this.onGround = false;

			transform.rotation += -360.0f * (velocity.x() >= 0.0f ? 1.0f : -1.0f) * time.getDelta();
		}

		if(transform.position.y() <= -20.0f) respawn(world); // TODO: Remove, it's only for debugging
	}

	private void control(Time time) {
		velocity.x = 0.0f;
		//if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_D)) velocity.x += info.speed;
		//if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_A)) velocity.x -= info.speed;

		velocity.x = info.speed;
		velocity.y -= info.gravity * time.getDelta();

		if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_SPACE) || Mouse.isButtonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
			jumping = true;
			jumpBufferTimer = 0.0f;
		} else {
			jumpBufferTimer += time.getDelta();
			if(jumpBufferTimer >= info.jumpBufferTime) jumping = false;
		}

		if(jumping && onGround) {
			velocity.y = info.jumpHeight;
			onGround = false;
		}
	}

	private boolean resolveCollisionsAndGetOnGround(Time time, World world) {
		float tempVelocityY = velocity.y() * time.getDelta();
		float originTempVelocityY = tempVelocityY;

		for(Block block : world.blocks) {
			Collider blockResolveCollider = BlockInfo.getById(block.id).resolveCollider();
			Collider blockKillCollider = BlockInfo.getById(block.id).killCollider();

			if(blockResolveCollider != null) {
				tempVelocityY = resolveCollider.clipVelocityBottom(
						transform, block.transform,
						blockResolveCollider, tempVelocityY
				);
			}

			if(blockKillCollider != null && killCollider.intersects(transform, block.transform, blockKillCollider)) respawn(world);
		}

		transform.position.x += velocity.x() * time.getDelta();
		transform.position.y += tempVelocityY;

		if(originTempVelocityY != tempVelocityY && originTempVelocityY < 0.0f) {
			velocity.y = 0.0f;
			return true;
		}

		return false;
	}

	public void respawn(World world) {
		transform.position.set(world.spawnPoint);
		velocity.set(0.0f);
	}

	public boolean isOnGround() {
		return onGround;
	}
}
