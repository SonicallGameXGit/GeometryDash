package me.sgx.gd.audio;

import me.sgx.engine.audio.Audio;
import me.sgx.engine.audio.AudioSystem;

import java.io.File;
import java.util.HashMap;

public class Sounds {
	private static final HashMap<String, Audio> audios = new HashMap<>();
	private static final String DIRECTORY = "res/sounds";

	private static void loadTexture(File path, String folder) {
		if(path.isDirectory()) {
			File[] files = path.listFiles();

			if(files != null) {
				for(File file : files) {
					loadTexture(file, folder + '/' + path.getName());
				}
			}

			return;
		}

		audios.put(
				(folder + '/' + path.getName().split("\\.")[0]).substring(1),
				AudioSystem.loadAudio(path.toString())
		);
	}

	public static void initialize() {
		File directory = new File(DIRECTORY);
		if(!directory.exists() || !directory.isDirectory()) {
			System.err.println("Could not find sounds directory: \"" + DIRECTORY + "\".");
			System.exit(1);
		}

		File[] files = directory.listFiles();
		if(files == null) return;

		for(File file : files) loadTexture(file, "");
	}

	public static Audio getAudio(String name) {
		return audios.get(name);
	}
}