package org.jenkinsci.plugins.torque;

import java.util.UUID;

public class PluginHelpers{
    public static String GenerateSandboxName()
    {
        return String.format("sandbox-for-testing-%s", UUID.randomUUID().toString());
    }
}
