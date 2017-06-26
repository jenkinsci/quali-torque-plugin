package org.jenkinsci.plugins.cs18.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateSandboxResponse
{
    @JsonProperty("id")
    public String id;
    @JsonProperty("name")
    public String name;
}

