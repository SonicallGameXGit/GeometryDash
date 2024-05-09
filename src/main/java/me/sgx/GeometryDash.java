package me.sgx;

import lombok.extern.log4j.Log4j2;
import me.oneqxz.version.Version;
import me.sgx.gd.scene.SceneSystem;
import me.sgx.gd.scene.custom.MainMenuScene;

@Log4j2
public class GeometryDash {

	public static final String NAME = "Geometry Dash";
	public static final Version VERSION = new Version("1.0.0");

	public static void main(String[] args) {
		log.info("Loading...");

		SceneSystem.setScene(new MainMenuScene());
		SceneSystem.run(true);
	}
}