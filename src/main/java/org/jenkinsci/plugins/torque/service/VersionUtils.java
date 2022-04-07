package org.jenkinsci.plugins.torque.service;

import java.io.IOException;
import java.util.Properties;

public class VersionUtils {
    public static final String PackageVersion;
    static {
        try {
            PackageVersion = getPackageVersion();
        } catch (IOException e) {
            throw new RuntimeException("Error while initializing VersionUtils.", e);
        }
    }

    private static String getPackageVersion() throws IOException {
        Properties properties = new Properties();
        properties.load(VersionUtils.class.getClassLoader().getResourceAsStream("jenkins-torque-plugin.properties"));
        return properties.getProperty("version");
    }
}
