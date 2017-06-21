package org.jenkinsci.plugins.cloudshell.api;

public class CreateSandboxRequest
{
    public final String blueprint_profile;
    public final String sandbox_name;
    public final String stage;

    public CreateSandboxRequest(String blueprintName, String sandboxName, String stage){

        this.blueprint_profile = blueprintName;
        this.sandbox_name = sandboxName;
        this.stage = stage;
    }
}

