package org.jenkinsci.plugins.cs18.api;

public class CreateSandboxRequest
{
    public final String blueprint_profile;
    public final String stage;
    public final String sandbox_name;
    public final String branch;
    public final String changeset;
    public final boolean automation;

    public CreateSandboxRequest(String blueprintName,
                                String stage,
                                String sandbox_name,
                                String branch,
                                String changeset,
                                boolean automation){

        this.blueprint_profile = blueprintName;
        this.stage = stage;
        this.sandbox_name = sandbox_name;
        this.branch = branch;
        this.changeset = changeset;
        this.automation = automation;
    }
}

