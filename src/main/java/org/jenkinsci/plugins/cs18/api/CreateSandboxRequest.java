package org.jenkinsci.plugins.cs18.api;

public class CreateSandboxRequest
{
    public final String blueprint_profile;
    public final String stage;
    public final String sandbox_name;

    public CreateSandboxRequest(String blueprintName, String stage, String sandbox_name){

        this.blueprint_profile = blueprintName;
        this.stage = stage;
        this.sandbox_name = sandbox_name;
    }
}

