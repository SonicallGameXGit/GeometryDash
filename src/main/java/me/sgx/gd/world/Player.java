package me.sgx.gd.world;

import me.sgx.engine.graphics.texture.Texture;
import me.sgx.engine.math.Time;
import me.sgx.gd.graphics.Camera;
import me.sgx.gd.graphics.Drawable;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;

public abstract class Player extends Drawable {
	protected static final int CUBE_TEXTURE = Texture.create("res/textures/cube.png", GL11.GL_NEAREST);
	protected static final int SHIP_TEXTURE = Texture.create("res/textures/ship.png", GL11.GL_NEAREST);
	protected static final int BALL_TEXTURE = Texture.create("res/textures/ball.png", GL11.GL_NEAREST);

	public float speed = 1.0f, physicsSpeed = 1.0f;

	public Transform transform = new Transform();
	protected Vector2f velocity = new Vector2f();

	protected final Collider resolveCollider, killCollider;

	private final int texture;
	protected Player(int texture, Collider resolveCollider, Collider killCollider) {
		this.texture = texture;
		this.resolveCollider = resolveCollider;
		this.killCollider = killCollider;
	}

	protected void copyFrom(Player origin) {
		transform = origin.transform;
		velocity = origin.velocity;

		speed = origin.speed;
		physicsSpeed = origin.physicsSpeed;
	}

	@Override
	public void render() {
		loadTexture(texture);
		render(transform, getAnchor());
	}

	protected Vector2f getAnchor() {
		return new Vector2f();
	}

	public void update(Time time, World world, Camera camera) {
		if(transform.position.y() <= -20.0f) world.restart(this, camera); // TODO: Remove, it's only for debugging
	}

	public void resetVelocity() {
		velocity.set(0.0f);
	}
}
