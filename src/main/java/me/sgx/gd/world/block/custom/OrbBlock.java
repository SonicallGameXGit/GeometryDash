package me.sgx.gd.world.block.custom;

import me.sgx.engine.math.MathUtil;
import me.sgx.gd.world.World;
import me.sgx.gd.world.block.Block;
import me.sgx.gd.world.block.Interactable;
import me.sgx.gd.world.block.PlacedBlock;
import me.sgx.gd.world.block.module.MultipleUseModule;
import me.sgx.gd.world.math.Collider;
import org.joml.Vector4f;

public class OrbBlock extends Block implements Interactable {
	private static final int SAMPLES_AHEAD = 100;

	public static float BOUNCE_SHARPNESS = 32.0f,
			BOUNCE_HEIGHT = 1.6f,
			BOUNCE_MAX_HEIGHT = 0.3f,
			BOUNCE_THRESHOLD = 0.5f;

	public static final int MULTIPLE_USE_MODULE_CD_ID = 0;

	private static int currentSampleReadOffset = 0;
	private static float highestSample = 0.0f, lastSample = 0.0f, volume = 0.0f;

	public OrbBlock(Collider triggerCollider, Vector4f uv) {
		super(null, null, triggerCollider, uv);
	}

	@Override
	public void initialize(PlacedBlock placed) {
		super.initialize(placed);
		MultipleUseModule.initialize(placed); // MULTIPLE_USE_MODULE_CD_ID
	}

	public static void update() {
		highestSample = Math.max(highestSample, World.getMusicVolume());

        currentSampleReadOffset++;
        if(currentSampleReadOffset >= SAMPLES_AHEAD) {
            lastSample = highestSample;
            currentSampleReadOffset = 0;

			highestSample = 0.0f;
        }

		float sharpVolume = lastSample;
		sharpVolume = sharpVolume >= BOUNCE_THRESHOLD ? 0.0f : sharpVolume;
		sharpVolume = Math.min(sharpVolume * BOUNCE_HEIGHT, BOUNCE_MAX_HEIGHT);

		volume = MathUtil.lerp(volume,
				sharpVolume,
				BOUNCE_SHARPNESS * World.time.getDelta());
	}
	@Override
	public void update(PlacedBlock placed) {
		placed.sprite.transform.size.set(0.9f + volume);
	}
}