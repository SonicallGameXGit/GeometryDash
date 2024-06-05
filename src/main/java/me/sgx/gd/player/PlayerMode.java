package me.sgx.gd.player;

import me.sgx.engine.math.MathUtil;
import me.sgx.gd.graphics.Textures;
import me.sgx.gd.world.World;
import me.sgx.gd.world.block.PlacedBlock;
import me.sgx.gd.world.math.Collider;
import me.sgx.gd.world.math.Transform;
import org.joml.Vector2f;

public abstract class PlayerMode {
	public static float DEFAULT_JUMP_HEIGHT = 19.91f;

	public static final PlayerMode CUBE = new PlayerMode(
			Textures.PLAYERMODE_CUBE,
			new Collider(), new Collider(new Vector2f(0.9f)), new Collider(),
			false
	) {
		public static final float ROTATE_SPEED = 430.0f, ANGLE_FIX_SPEED = 24.0f, GRAVITY = 95.0f, MIN_VELOCITY = 24.0f;

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
			Textures.PLAYERMODE_SHIP,
			new Collider(new Vector2f(0.6f, 0.5f)),
			new Collider(new Vector2f(0.6f, 0.25f)),
			new Collider(new Vector2f(0.6f, 0.5f)),
			true
	) {
		public static final float ROTATION_SHARPNESS = 12.0f, MAX_ANGLE = 70.0f, ROTATE_SPEED = 6.0f, GRAVITY = 3.0f, MIN_VELOCITY = 12.0f;

		@Override
		public void initialize(Player player) {
			super.initialize(player);

			player.sprite.transform.size.set(1.5f);
			player.sprite.transform.anchor.set(0.25f, 0.0f);
		}

		@Override
		public void groundTap(Player player) {
			fly(player, false);
		}

		@Override
		public void update(Player player) {
			super.update(player);
			player.velocity.y = Math.max(Math.min(player.velocity.y, MIN_VELOCITY), -MIN_VELOCITY);
		}

		@Override
		public void fly(Player player, boolean justStarted) {
			player.velocity.y += GRAVITY * player.speed * (player.direction ? 1.0f : -1.0f) * World.time.getDelta();
			if(!justStarted) {
				player.triggerBuffered = false;
			}
		}
		@Override
		public void release(Player player) {
			player.velocity.y -= GRAVITY * player.speed * (player.direction ? 1.0f : -1.0f) * World.time.getDelta();
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
			Textures.PLAYERMODE_BALL,
			new Collider(), new Collider(new Vector2f(0.9f)), new Collider(),
			true
	) {
		public static final float ROTATE_SPEED = 90.0f, FLY_ROTATE_SPEED = 45.0f, GRAVITY = 7.564f, MIN_VELOCITY = 12.0f;

		@Override
		public void initialize(Player player) {
			super.initialize(player);

			player.cd.add(true); // Rotate Direction
			player.cd.add(false); // Can Flip Direction
			player.cd.add(false); // Can Flip Gravity

			player.sprite.transform.size.set(1.05f);
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
	public static final PlayerMode SWING_COPTER = new PlayerMode(
			Textures.PLAYERMODE_SWINGCOPTER,
			new Collider(),
			new Collider(new Vector2f(0.9f)),
			new Collider(),
			true
	) {
		public static final float ROTATION_SHARPNESS = 12.0f, MAX_ANGLE = 70.0f, ROTATE_SPEED = 6.0f, GRAVITY = 3.0f, MIN_VELOCITY = 12.0f;

		@Override
		public void initialize(Player player) {
			super.initialize(player);
			player.sprite.transform.size.set(1.05f);
		}

		@Override
		public void update(Player player) {
			super.update(player);

			player.velocity.y += GRAVITY * player.speed * (player.direction ? -1.0f : 1.0f) * World.time.getDelta();
			player.velocity.y = player.direction ? Math.max(player.velocity.y, -MIN_VELOCITY) : Math.min(player.velocity.y, MIN_VELOCITY);
		}

		@Override
		public void fly(Player player, boolean justStarted) {
			if (justStarted) {
				player.direction = !player.direction;
			}
		}

		@Override
		public void onTrigger(Player player, PlacedBlock block) {
			super.onTrigger(player, block);
			player.direction = !player.direction;
		}

		@Override
		public void animate(Player player) {
			player.rotation = MathUtil.lerp(
					player.rotation,
					player.onGround ? 0.0f : Math.max(Math.min(player.velocity.y() * ROTATE_SPEED, MAX_ANGLE), -MAX_ANGLE),
					ROTATION_SHARPNESS * World.time.getDelta()
			);
		}
	};
	public static final PlayerMode ROBOT = new PlayerMode(
			Textures.PLAYERMODE_ROBOT,
			new Collider(),
			new Collider(new Vector2f(0.9f)),
			new Collider(),
			true
	) {
		public static final float GRAVITY = 95.0f, MIN_VELOCITY = 24.0f, JUMP_TIME = 0.3f;

		@Override
		public void initialize(Player player) {
			super.initialize(player);

			player.cd.add(0.0f); // Jump timer
			player.cd.add(false); // Jumping
			player.cd.add(false); // Can jump
		}

		@Override
		public void update(Player player) {
			super.update(player);

			float direction = player.direction ? 1.0f : -1.0f;
			player.velocity.y -= GRAVITY * direction * World.time.getDelta();
			player.velocity.y = player.direction ?
					Math.max(player.velocity.y(), -MIN_VELOCITY) :
					Math.min(player.velocity.y(), MIN_VELOCITY);

			if ((boolean)player.cd.get(1)) {
				player.jump(DEFAULT_JUMP_HEIGHT * 0.5f);
			}

			player.sprite.transform.size.y = Math.abs(player.sprite.transform.size.y) * direction;

			player.cd.set(0, (float)player.cd.get(0) + World.time.getDelta());
			if ((float)player.cd.get(0) >= JUMP_TIME) {
				player.cd.set(0, 0.0f);
				player.cd.set(1, false);
			}
		}

		@Override
		public void groundTap(Player player) {
			super.groundTap(player);

			if((boolean)player.cd.get(2)) {
				player.cd.set(0, 0.0f);
				player.cd.set(1, true);
			}

			player.cd.set(2, false);
		}

		@Override
		public void release(Player player) {
			super.release(player);

			player.cd.set(0, 0.0f);
			player.cd.set(1, false);
			player.cd.set(2, true);
		}

		@Override
		public void onTrigger(Player player, PlacedBlock block) {
			super.onTrigger(player, block);
			player.cd.set(2, false);
		}
	};

	public static final PlayerMode SPIDER = new PlayerMode(
			Textures.PLAYERMODE_SPIDER,
			new Collider(new Vector2f(1.0f, 0.7f)),
			new Collider(new Vector2f(0.9f, 0.0f)),
			new Collider(new Vector2f(1.0f, 0.7f)),
			true
	) {
		public static final float GRAVITY = 95.0f, MIN_VELOCITY = 24.0f;

		@Override
		public void initialize(Player player) {
			super.initialize(player);
			player.cd.add(true); // Can flip

			player.sprite.transform.size.set(1.15f);
		}

		@Override
		public void update(Player player) {
			super.update(player);

			boolean canStopVelocity = Math.abs(player.velocity.y) <= MIN_VELOCITY;

			player.velocity.y -= GRAVITY * (player.direction ? 1.0f : -1.0f) * World.time.getDelta();
			if(canStopVelocity) {
				player.velocity.y = player.direction ?
						Math.max(player.velocity.y(), -MIN_VELOCITY) :
						Math.min(player.velocity.y(), MIN_VELOCITY);
			}

			player.sprite.transform.size.y = Math.abs(player.sprite.transform.size.y) * (player.direction ? 1.0f : -1.0f);
		}

		@Override
		public void onTrigger(Player player, PlacedBlock block) {
			super.onTrigger(player, block);
			player.cd.set(0, false);
		}

		@Override
		public void groundTap(Player player) {
			super.groundTap(player);

			if ((boolean)player.cd.get(0)) {
				player.velocity.y = player.direction ? 999999.0f : -999999.0f;
				player.direction = !player.direction;

				player.cd.set(0, false);
			}
		}

		@Override
		public void release(Player player) {
			super.release(player);
			player.cd.set(0, true);
		}
	};

	public final int texture;

	public final Collider resolveCollider, damageCollider, triggerCollider;
	public final boolean clipBoth;

	public PlayerMode(
			int texture,
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
	public void fly(Player player, boolean justStarted) {}
	public void release(Player player) {}
	public void animate(Player player) {}
	public void onTrigger(Player player, PlacedBlock block) {}

	public void initialize(Player player) {
		player.setTransform(new Transform(player.position, new Vector2f(1.0f), 0.0f));
		player.sprite.transform = new Transform();
	}
	public void update(Player player) {}
}