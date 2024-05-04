package me.sgx.gd.graphics;

import java.io.File;

public class Textures {
	private static final String DIRECTORY = "res/textures";

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

		Graphics.loadTextureRaw((folder + '/' + path.getName().split("\\.")[0]).substring(1), path.toString());
	}

	public static void initialize() {
		File directory = new File(DIRECTORY);
		if(!directory.exists() || !directory.isDirectory()) {
			System.err.println("Could not find textures directory: \"" + DIRECTORY + "\".");
			System.exit(1);
		}

		File[] files = directory.listFiles();
		if(files == null) return;

		for(File file : files) loadTexture(file, "");
	}
}