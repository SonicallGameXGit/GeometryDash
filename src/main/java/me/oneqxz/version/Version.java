package me.oneqxz.version;

import lombok.Getter;

public class Version {

    private final String stringed;
    @Getter private final int version;

    public Version(String version)
    {
        this.stringed = version;

        String cleanedVersion = version.replaceAll("[v.]", "");
        this.version = Integer.parseInt(cleanedVersion);
    }

    public String getAsString() {
        return this.stringed;
    }

    public VersionStatus compareVersions(Version currentVersion, Version newVersion)
    {
        int current = this.version;
        int newVer = newVersion.getVersion();

        if(current < newVer)
            return VersionStatus.OUTDATED;
        else if(current == newVer)
            return VersionStatus.CURRENT;
        else
            return VersionStatus.FUTURE;
    }
}
