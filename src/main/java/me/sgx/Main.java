package me.sgx;

import me.sgx.gd.scene.SceneSystem;
import me.sgx.gd.scene.custom.MainMenuScene;

public class Main {
	public static void main(String[] args) {
		SceneSystem.setScene(new MainMenuScene());
		SceneSystem.run(true);
	}
}