package me.sgx.gd.graphics.animation;

import me.sgx.engine.math.Time;
import java.util.ArrayList;

public class Animation {
	private static final ArrayList<Animation> animations = new ArrayList<>();

	public final Keyframe<?>[] keyframes;

	private float value;
	public float time = 0.0f, speed = 1.0f;

	private boolean playing = false;
	public boolean looping = false;

	public Animation(Keyframe<?>... keyframes) {
		this.keyframes = keyframes;
		this.value = keyframes[0].value;

		animations.add(this);
	}
	public Animation(float speed, Keyframe<?>... keyframes) {
		this.keyframes = keyframes;

		this.value = keyframes[0].value;
		this.speed = speed;

		animations.add(this);
	}

	public void play() {
		stop();
		playing = true;
	}
	public void _continue() {
		playing = true;
	}
	public void update(Time time) {
		if(!playing) return;

		this.time += speed * time.getDelta();
		if(this.time > 1.0f) {
			if(!looping) {
				this.time = 1.0f;
				pause();
			} else this.time = 0.0f;
		}
		if(this.time < 0.0f) {
			if(!looping) {
				this.time = 0.0f;
				pause();
			} else this.time = 1.0f;
		}

		float biasedTime = Math.min(this.time, 0.99f);
		float scaledTime = biasedTime * (keyframes.length - 1.0f);

		int keyframeIndex = (int) Math.floor(scaledTime);
		float keyframeFrac = scaledTime - keyframeIndex;

		value = keyframes[keyframeIndex].interpolate(keyframes[keyframeIndex + 1], keyframeFrac);
	}
	public void stop() {
		pause();

		value = keyframes[0].value;
		time = 0.0f;
	}
	public void pause() {
        playing = false;
    }

	public boolean isPlaying() {
        return playing;
    }
	public float getValue() {
        return value;
    }

	public static void updateAll(Time time) {
		for(Animation animation : animations) {
            animation.update(time);
        }
	}
}