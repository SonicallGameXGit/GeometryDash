package me.sgx.gd.player;

import me.sgx.engine.math.MathUtil;
import me.sgx.gd.world.World;
import me.sgx.gd.world.math.Collider;
import me.sgx.gd.world.math.Transform;
import org.joml.Vector2f;

public abstract class PlayerMode {
	public static float DEFAULT_JUMP_HEIGHT = 19.91f;

	public static final PlayerMode CUBE = new PlayerMode(
			"cube",
			new Collider(), new Collider(), new Collider(),
			false
	) {
		public static final float ROTATE_SPEED = 430.0f, ANGLE_FIX_SPEED = 24.0f, GRAVITY = 95.0f, MIN_VELOCITY = 26.0f;

		@Override
		public void groundTap(Player player) {
			player.jump(DEFAULT_JUMP_HEIGHT);
		}

		@Override
		public void update(Player player) {
			super.update(player);

			player.velocity.y -= GRAVITY * (player.direction ? 1.0f : -1.0f) * World.time.getDelta();
			player.velocity.y = player.direction ?
					Math.max(player.velocity.y(), -MIN_VELOCITY) :
					Math.min(player.velocity.y(), MIN_VELOCITY);
		}

		@Override
		public void animate(Player player) {
			if(!player.onGround) {
				player.rotation -= ROTATE_SPEED * (player.direction ? 1.0f : -1.0f) * World.time.getDelta();
			} else {
				player.rotation = MathUtil.lerp(
						player.rotation,
						(float) Math.round(player.rotation / 90.0f) * 90.0f, // TODO: add surface normal offset to angle
						ANGLE_FIX_SPEED * World.time.getDelta()
				);
			}
		}
	};
	public static final PlayerMode SHIP = new PlayerMode(
			"ship",
			new Collider(new Vector2f(0.6f, 1.0f)),
			new Collider(new Vector2f(0.6f, 1.0f)),
			new Collider(new Vector2f(0.6f, 1.0f)),
			true
	) {
		public static final float ROTATION_SHARPNESS = 12.0f, MAX_ANGLE = 70.0f, ROTATE_SPEED = 6.0f, GRAVITY = 3.0f, MIN_VELOCITY = 14.0f;

		@Override
		public void initialize(Player player) {
			super.initialize(player);

			player.sprite.transform.size.set(1.5f);
			player.sprite.transform.anchor.set(0.25f, 0.0f);
		}

		@Override
		public void groundTap(Player player) {
			fly(player);
		}

		@Override
		public void fly(Player player) {
			player.velocity.y += GRAVITY * player.speed * (player.direction ? 1.0f : -1.0f) * World.time.getDelta();
			player.velocity.y = Math.min(player.velocity.y, MIN_VELOCITY);
		}
		@Override
		public void release(Player player) {
			player.velocity.y -= GRAVITY * player.speed * (player.direction ? 1.0f : -1.0f) * World.time.getDelta();
			player.velocity.y = Math.max(player.velocity.y, -MIN_VELOCITY);
		}

		@Override
		public void animate(Player player) {
			player.rotation = MathUtil.lerp(
					player.rotation,
					player.onGround ? 0.0f : Math.max(Math.min(player.velocity.y() * ROTATE_SPEED, MAX_ANGLE), -MAX_ANGLE),
					ROTATION_SHARPNESS * World.time.getDelta()
			);
			player.sprite.transform.size.y = Math.abs(player.sprite.transform.size.y) * (player.direction ? 1.0f : -1.0f);
		}
	};
	public static final PlayerMode BALL = new PlayerMode(
			"ball",
			new Collider(), new Collider(), new Collider(),
			true
	) {
		public static final float ROTATE_SPEED = 90.0f, FLY_ROTATE_SPEED = 45.0f, GRAVITY = 7.564f, MIN_VELOCITY = 26.0f;

		@Override
		public void initialize(Player player) {
			super.initialize(player);

			player.cd.add(true); // Rotate Direction
			player.cd.add(false); // Can Flip Direction
			player.cd.add(false); // Can Flip Gravity
		}

		@Override
		public void groundTap(Player player) {
			if((boolean) player.cd.get(2)) {
				player.direction = !player.direction;
				player.cd.set(2, false);
			}
		}
		@Override
		public void animate(Player player) {
			player.rotation -= (player.onGround ? ROTATE_SPEED : FLY_ROTATE_SPEED) * ((boolean) player.cd.get(0) ? 1.0f : -1.0f) * player.speed * player.size.y() * World.time.getDelta();
		}

		@Override
		public void update(Player player) {
			super.update(player);

			player.velocity.y -= GRAVITY * (player.direction ? 1.0f : -1.0f) * player.speed * World.time.getDelta();
			player.velocity.y = player.direction ?
					Math.max(player.velocity.y(), -MIN_VELOCITY) :
					Math.min(player.velocity.y(), MIN_VELOCITY);

			if(!player.onGround) {
				player.cd.set(1, true);
			} else {
				if((boolean) player.cd.get(1)) player.cd.set(0, player.direction);
				player.cd.set(1, false);
			}

			if(!player.pressed) {
				player.cd.set(2, true);
			}
		}
	};

	public final String texture;

	public final Collider resolveCollider, damageCollider, triggerCollider;
	public final boolean clipBoth;

	public PlayerMode(
			String texture,
			Collider resolveCollider,
			Collider damageCollider,
			Collider triggerCollider,
			boolean clipBoth
	) {
		this.texture = texture;
		this.resolveCollider = new Collider(resolveCollider);
		this.damageCollider = new Collider(damageCollider);
		this.triggerCollider = new Collider(triggerCollider);
		this.clipBoth = clipBoth;
	}

	public void groundTap(Player player) {}
	public void fly(Player player) {}
	public void release(Player player) {}
	public void animate(Player player) {}

	public void initialize(Player player) {
		player.setTransform(new Transform(player.position, new Vector2f(1.0f), 0.0f));
		player.sprite.transform = new Transform();
	}
	public void update(Player player) {}
}