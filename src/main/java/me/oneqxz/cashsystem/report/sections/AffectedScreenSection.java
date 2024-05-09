package me.oneqxz.cashsystem.report.sections;

import me.sgx.gd.scene.SceneSystem;

public class AffectedScreenSection implements ICrashSection {

    @Override
    public String formatSection(Throwable crush) {
        StringBuilder sb = new StringBuilder();
        sb.append("-- Affected scene --\n");
        sb.append("Details:\n");
        sb.append("\tScene name: ").append(SceneSystem.getScene().toString());
        return sb.toString();
    }

}
