package org.jenkinsci.plugins.cloudshell.api;

public class CreateSandboxRequest
{
    public final String blueprint_profile;
    public final String stage;

    public CreateSandboxRequest(String blueprintName, String stage){

        this.blueprint_profile = blueprintName;
        this.stage = stage;
    }
}

