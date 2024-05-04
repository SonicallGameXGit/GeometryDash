package me.sgx.gd.graphics.animation.interpolation;

import me.sgx.gd.graphics.animation.InterpolationMode;

public class BounceInterpolation extends Interpolation {
	public final int bounces;
	public final float damping;

	public BounceInterpolation(InterpolationMode mode, int bounces, float damping) {
		super(mode);

		this.bounces = bounces;
		this.damping = damping;
	}
}
