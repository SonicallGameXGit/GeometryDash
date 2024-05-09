package me.sgx.gd.audio;

import lombok.extern.log4j.Log4j2;
import me.sgx.engine.audio.Audio;
import me.sgx.engine.audio.AudioSystem;

import java.io.File;
import java.util.HashMap;

@Log4j2
public class Sounds {
	private static final HashMap<String, Audio> audios = new HashMap<>();
	private static final String DIRECTORY = "res/sounds";

	private static void loadTexture(File path, String folder) {
        log.info("Loading sound: {}", path.getName());
		if(path.isDirectory()) {
			File[] files = path.listFiles();

			if(files != null) {
				for(File file : files) {
					loadTexture(file, folder + '/' + path.getName());
				}
			}

			return;
		}

		log.info("Loaded sound: {}", path.getName().split("\\.")[0]);
		audios.put(
				(folder + '/' + path.getName().split("\\.")[0]).substring(1),
				AudioSystem.loadAudio(path.toString())
		);
	}

	public static void initialize() {
		log.info("Initializing sounds");
		File directory = new File(DIRECTORY);
		if(!directory.exists() || !directory.isDirectory()) {
			log.fatal("Could not find sounds directory: \"{}\".", DIRECTORY);
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