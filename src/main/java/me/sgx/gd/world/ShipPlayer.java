package me.sgx.gd.world;

import me.sgx.engine.io.Keyboard;
import me.sgx.engine.io.Mouse;
import me.sgx.engine.math.MathUtil;
import me.sgx.engine.math.Time;
import me.sgx.gd.graphics.Camera;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

public class ShipPlayer extends Player {
	private static final float GRAVITY = 3.0f, GRAVITY_CLAMP = 5.0f, ANGLE_CLAMP = 40.0f, ROTATION_SHARPNESS = 14.0f;

	public ShipPlayer() {
		super(Player.SHIP_TEXTURE, new Collider(new Vector2f(0.25f), new Vector2f(0.5f)), new Collider(new Vector2f(0.35f), new Vector2f(0.3f)));
	}
	public ShipPlayer(Player origin) {
		super(Player.SHIP_TEXTURE, new Collider(new Vector2f(0.25f), new Vector2f(0.5f)), new Collider(new Vector2f(0.35f), new Vector2f(0.3f)));

		copyFrom(origin);
		transform.scale.set(1.3f);
	}

	@Override
	protected Vector2f getAnchor() {
		return new Vector2f(-0.25f, 0.0f);
	}

	@Override
	public void update(Time time, World world, Camera camera) {
		super.update(time, world, camera);
		control(time);

		transform.rotation = MathUtil.lerp(transform.rotation, resolveCollisionsAndGetOnGround(time, world, camera) ? 0.0f : velocity.y() / GRAVITY_CLAMP * ANGLE_CLAMP, ROTATION_SHARPNESS * time.getDelta());
	}

	private void control(Time time) {
		velocity.x = speed;

		if(Keyboard.isKeyPressed(GLFW.GLFW_KEY_SPACE) || Mouse.isButtonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT))
			velocity.y += GRAVITY * speed * physicsSpeed * time.getDelta();
		else velocity.y -= GRAVITY * speed * physicsSpeed * time.getDelta();

		velocity.y = Math.max(Math.min(velocity.y(), GRAVITY_CLAMP * 2.0f), -GRAVITY_CLAMP * 2.0f);
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
						blockResolveCollider, tempVelocityY, Collider.ClipType.BOTH
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
