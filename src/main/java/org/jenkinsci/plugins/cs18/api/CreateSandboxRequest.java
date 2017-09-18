package org.jenkinsci.plugins.cs18.api;

import java.util.Map;

public class CreateSandboxRequest
{
    public final String blueprint_name;
    public final String sandbox_name;
    public final Map<String, String> release;
    public final boolean automation;

    public CreateSandboxRequest(String blueprintName,
                                String sandboxName,
                                Map<String, String> release,
                                boolean automation){

        this.blueprint_name = blueprintName;
        this.sandbox_name = sandboxName;
        this.release = release;
        this.automation = automation;
    }
}

