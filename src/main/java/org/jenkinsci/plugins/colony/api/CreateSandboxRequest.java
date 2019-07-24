package org.jenkinsci.plugins.colony.api;

import java.util.Map;

public class CreateSandboxRequest
{
    public final String blueprint_name;
    public final String sandbox_name;
    public final Map<String, String> artifacts;
    public final boolean automation;
    public final Map<String, String> inputs;
    public final String duration;
    public final String description;

    public CreateSandboxRequest(String blueprintName,
                                String sandboxName,
                                Map<String, String> artifacts,
                                boolean automation,
                                Map<String, String> inputs,
                                String duration){

        this.blueprint_name = blueprintName;
        this.sandbox_name = sandboxName;
        this.artifacts = artifacts;
        this.automation = automation;
        this.inputs = inputs;
        this.duration = duration;
        this.description="JenkinsOriginSandbox";
    }
}

