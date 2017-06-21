package org.jenkinsci.plugins.cloudshell.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateSandboxResponse
{
    @JsonProperty("id")
    public String id;
    @JsonProperty("name")
    public String name;
}

