package me.sgx.gd.graphics.animation.interpolation;

import me.sgx.gd.graphics.animation.InterpolationMode;

public class EaseInterpolation extends Interpolation {
	public final float power;
	public EaseInterpolation(InterpolationMode mode, float power) {
		super(mode);
		this.power = power;
	}
}