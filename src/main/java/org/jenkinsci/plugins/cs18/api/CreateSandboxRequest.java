package org.jenkinsci.plugins.cs18.api;

public class CreateSandboxRequest
{
    public final String blueprint_name;
    public final String stage;
    public final String name;
    public final String branch;
    public final String changeset;
    public final boolean automation;

    public CreateSandboxRequest(String blueprintName,
                                String stage,
                                String name,
                                String branch,
                                String changeset,
                                boolean automation){

        this.blueprint_name = blueprintName;
        this.stage = stage;
        this.name = name;
        this.branch = branch;
        this.changeset = changeset;
        this.automation = automation;
    }
}

