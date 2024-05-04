package me.sgx.gd.graphics.animation;

import me.sgx.engine.math.MathUtil;
import me.sgx.gd.graphics.animation.interpolation.BounceInterpolation;
import me.sgx.gd.graphics.animation.interpolation.EaseInterpolation;
import me.sgx.gd.graphics.animation.interpolation.Interpolation;

public class Keyframe<I extends Interpolation> {
	public float value;
	public I interpolation;

	public Keyframe(float value, I interpolation) {
        this.value = value;
        this.interpolation = interpolation;
    }

	public float interpolate(Keyframe<?> next, float delta) {
		if (interpolation instanceof EaseInterpolation ease) {
			switch (interpolation.mode) {
				case IN -> {
					return MathUtil.lerp(value, next.value, (float) Math.pow(delta, ease.power));
				}
				case OUT -> {
					return MathUtil.lerp(value, next.value, (float) Math.pow(delta, 1.0f / ease.power));
				}
				case IN_OUT -> {
					return MathUtil.lerp(value, next.value, (float) (
									delta < 0.5f ?
											Math.pow(delta * 2.0, ease.power) / 2.0 :
											1.0 - Math.pow(-2.0 * delta + 2.0, ease.power) / 2.0
							)
					);
				}
			}

			return 0.0f;
		} else if (interpolation instanceof BounceInterpolation bounce) {
			switch (interpolation.mode) {
				case IN -> {
					return MathUtil.lerp(value, next.value, bounceIn(delta, 1.0f, bounce));
				}
				case OUT -> {
					return MathUtil.lerp(value, next.value, bounceOut(delta, 1.0f, bounce));
				}
				case IN_OUT -> {
					float bounceIn = bounceIn(delta, 2.0f, bounce);
					float bounceOut = bounceOut(delta, 2.0f, bounce);

					return MathUtil.lerp(value, next.value, (bounceIn + bounceOut) / 2.0f);
				}
			}

			return 0.0f;
		}

		return MathUtil.lerp(value, next.value, delta);
	}

	private float bounceIn(float delta, float scale, BounceInterpolation interpolation) {
		float x = -delta * scale + scale;
		float squareX = x * x;

		return (float) (
				Math.abs(
						Math.cos(squareX * Math.PI * (interpolation.bounces + 0.5))
				) / (squareX * Math.exp(x * interpolation.damping) + 1.0)
		);
	}

	private float bounceOut(float delta, float scale, BounceInterpolation interpolation) {
		float x = delta * scale;
		float squareX = x * x;

		return 1.0f - (float) (
				Math.abs(
						Math.cos(squareX * Math.PI * (interpolation.bounces + 0.5))
				) / (squareX * Math.exp(x * interpolation.damping) + 1.0)
		);
	}
}