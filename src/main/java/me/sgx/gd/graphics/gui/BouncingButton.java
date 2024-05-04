package me.sgx.gd.graphics.gui;

import me.sgx.gd.graphics.animation.Animation;
import me.sgx.gd.graphics.animation.InterpolationMode;
import me.sgx.gd.graphics.animation.Keyframe;
import me.sgx.gd.graphics.animation.interpolation.BounceInterpolation;
import me.sgx.gd.graphics.animation.interpolation.Interpolation;
import me.sgx.gd.world.math.Transform;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class BouncingButton extends Button {
	private final Animation animation = new Animation(
			2.5f,
			new Keyframe<>(1.0f, new BounceInterpolation(InterpolationMode.OUT, 3, 4.5f)),
			new Keyframe<>(1.2f, new Interpolation(InterpolationMode.OUT))
	);

	private final Vector2f originalSize;

	public BouncingButton(String texture, Transform transform) {
		super(texture, transform);
		originalSize = new Vector2f(transform.size);
	}

	public BouncingButton(String texture, Transform transform, Vector4f uv) {
		super(texture, transform, uv);
		originalSize = new Vector2f(transform.size);
	}

	public BouncingButton(String texture, Transform holder, Transform transform) {
		super(texture, holder, transform);
		originalSize = new Vector2f(transform.size);
	}

	public BouncingButton(String texture, Transform holder, Transform transform, Vector4f uv) {
		super(texture, holder, transform, uv);
		originalSize = new Vector2f(transform.size);
	}

	@Override
	public void update() {
		super.update();

		if(isJustPressed()) {
			animation.keyframes[0].value = 1.0f;
			animation.keyframes[1].value = 1.2f;

			animation.play();
		}
		if(isReleased()) {
			animation.keyframes[0].value = 1.2f;
			animation.keyframes[1].value = 1.0f;

			if(animation.time < 0.5f) animation._continue();
			else animation.play();
		}

		transform.size.set(new Vector2f(originalSize).mul(animation.getValue()));
	}
}
