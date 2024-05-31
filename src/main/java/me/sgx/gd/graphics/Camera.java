package me.sgx.gd.graphics;

import me.sgx.engine.math.MathUtil;
import me.sgx.gd.player.Player;
import org.joml.Vector2f;

import static me.sgx.gd.world.World.time;

public class Camera {
	private static final float FOLLOW_BORDER_HEIGHT = 0.8f, FOLLOW_SPEED_Y = 14.0f;
	private static final Vector2f FOLLOW_OFFSET = new Vector2f(2.0f, 0.0f);

	public static Camera main = new Camera();

	public Vector2f position = new Vector2f(), zoom = new Vector2f(1.0f);
	public float rotation = 0.0f;

	public float rawY = position.y();

	public void follow(Player player) {
		position.x = player.position.x() + FOLLOW_OFFSET.x();

		float top = rawY - ((FOLLOW_BORDER_HEIGHT - 1.0f) / zoom.y() * 2.0f);
		float bottom = rawY + ((FOLLOW_BORDER_HEIGHT - 1.0f) / zoom.y() * 2.0f);

		if(player.position.y() >= top) rawY += player.position.y() - top;
		if(player.position.y() <= bottom) rawY -= bottom - player.position.y();

		position.y = MathUtil.lerp(
				position.y(),
				rawY + FOLLOW_OFFSET.y(),
				FOLLOW_SPEED_Y * time.getDelta());
	}

	public float getTopBound() {
		return position.y + 1.0f / zoom.y;
	}
	public float getBottomBound() {
        return position.y - 1.0f / zoom.y;
    }
	public float getRightBound(float aspect) {
		return position.x + aspect / zoom.x;
	}
	public float getLeftBound(float aspect) {
        return position.x - aspect / zoom.x;
    }
}