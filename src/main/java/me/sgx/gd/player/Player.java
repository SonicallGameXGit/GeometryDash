package me.sgx.gd.player;

import me.sgx.engine.io.Keyboard;
import me.sgx.engine.io.Mouse;
import me.sgx.gd.graphics.Sprite;
import me.sgx.gd.world.World;
import me.sgx.gd.world.block.Interactable;
import me.sgx.gd.world.block.PlacedBlock;
import me.sgx.gd.world.math.Collider;
import me.sgx.gd.world.math.Transform;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

import static me.sgx.gd.world.World.time;

public class Player extends Transform {
	private static final boolean DEBUG_INVINCIBLE = false;

	public final ArrayList<Object> cd = new ArrayList<>();

	public static final float SLOW_SPEED = 8.373f, NORMAL_SPEED = 10.386f, DOUBLE_SPEED = 12.914f, TRIPLE_SPEED = 15.6f;
	private ArrayList<PlacedBlock> touchedTriggers = new ArrayList<>();

	private PlayerMode mode;

	public Sprite sprite = new Sprite(this, new Transform());
	public Vector2f velocity = new Vector2f();

	public boolean onGround = false;
	public float speed = SLOW_SPEED;

	public boolean justPressed = false, pressed = false, direction = true;
	public boolean triggerBuffered = false;

	public Player(Transform transform) {
		super(new Transform(transform));
		setMode(PlayerMode.CUBE);
	}

	public void setTransform(Transform transform) {
		position.set(transform.position);
		size.set(transform.size);
		rotation = transform.rotation;
	}

	public void update() {
		mode.animate(this);
		process();
	}
	public void process() {
		handleInput();
		move();

		ArrayList<PlacedBlock> triggeredBlocks = collideAndTriggerBlocks();

		if(justPressed) triggerBuffered = true;
		else if(!pressed || onGround) triggerBuffered = false;

		if(pressed) {
			if(onGround && !triggerBuffered) mode.groundTap(this);
			else mode.fly(this, justPressed);
		} else mode.release(this);

		if(interact(triggeredBlocks)) {
			triggerBuffered = false;
		}

		mode.update(this);
		if(this.position.y >= 100.0f) {
			World.respawn(true);
		}
	}

	public void jump(float height) {
		velocity.y = height * (direction ? 1.0f : -1.0f);
	}
	public void setMode(PlayerMode mode) {
		this.mode = mode;

		cd.clear();
		mode.initialize(this);

		sprite.texture = mode.texture;
		rotation = 0.0f;
	}

	public PlayerMode getMode() {
		return mode;
	}

	private void handleInput() {
		justPressed = Keyboard.isKeyJustPressed(GLFW.GLFW_KEY_SPACE) || Keyboard.isKeyJustPressed(GLFW.GLFW_KEY_UP) || Mouse.isButtonJustPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT);
		pressed = Keyboard.isKeyPressed(GLFW.GLFW_KEY_SPACE) || Keyboard.isKeyPressed(GLFW.GLFW_KEY_UP) || Mouse.isButtonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT);
	}
	private void move() {
		velocity.x = speed;
	}
	private ArrayList<PlacedBlock> collideAndTriggerBlocks() {
		onGround = false;

		Vector2f scaledVelocity = new Vector2f(velocity).mul(time.getDelta());

		float scaledVelocityY = scaledVelocity.y;
		float tempVelocityY = scaledVelocityY;

		ArrayList<PlacedBlock> triggeredBlocks = new ArrayList<>();
		for(PlacedBlock placedBlock : World.blocks) {
			if(placedBlock.block.resolveCollider != null) {
				tempVelocityY = mode.resolveCollider.clipVelocityY(
						this, placedBlock.transform,
						placedBlock.block.resolveCollider,
						tempVelocityY, mode.clipBoth ?
								Collider.ClipType.BOTH :
								(direction ? Collider.ClipType.DOWN : Collider.ClipType.UP)
				);
			}

			if(!DEBUG_INVINCIBLE && placedBlock.block.damageCollider != null) {
				Vector2f damageVelocity = new Vector2f(scaledVelocity.x, tempVelocityY);
				damageVelocity.x = mode.damageCollider.clipVelocityX(this, placedBlock.transform, placedBlock.block.damageCollider, damageVelocity.x, Collider.ClipType.BOTH);

				if(damageVelocity.x != scaledVelocity.x) {
					World.respawn(true);
				}

				damageVelocity.y = mode.damageCollider.clipVelocityY(new Transform(new Vector2f(this.position.x + damageVelocity.x, this.position.y), this.size, this.rotation, this.anchor), placedBlock.transform, placedBlock.block.damageCollider, damageVelocity.y, Collider.ClipType.BOTH);

				if(damageVelocity.y != tempVelocityY) {
					World.respawn(true);
				}
			}

			if(placedBlock.block.triggerCollider != null) {
				if(mode.triggerCollider.intersects(this, placedBlock.transform, placedBlock.block.triggerCollider)) {
					triggeredBlocks.add(placedBlock);
				} else if(touchedTriggers.contains(placedBlock)) {
					((Interactable) placedBlock.block).onRelease(placedBlock, this);
					touchedTriggers.remove(placedBlock);
				}
			}
		}

		position.x += velocity.x() * time.getDelta();
		position.y += tempVelocityY;

		if(position.y() <= 0.0f) {
			tempVelocityY = 0.0f;
			position.y = 0.0f;
		}

		if(tempVelocityY != scaledVelocityY) {
			if(direction ? velocity.y() < 0.0f : velocity.y() > 0.0f) onGround = true;
			velocity.y = 0.0f;
		}

		return triggeredBlocks;
	}
	private boolean interact(ArrayList<PlacedBlock> triggers) {
		touchedTriggers = new ArrayList<>(triggers);

		if(!triggers.isEmpty()) {
			PlacedBlock nearest = triggers.get(0);
			for(PlacedBlock placedBlock : triggers) {
				((Interactable) placedBlock.block).onTouch(placedBlock, this);
				if(placedBlock.transform.position.distance(position) < nearest.transform.position.distance(position)) {
					nearest = placedBlock;
				}
			}

			if(triggerBuffered) {
				mode.onTrigger(this, nearest);
				((Interactable) nearest.block).onClick(nearest, this);
				return true;
			}
		}

		return false;
	}
}