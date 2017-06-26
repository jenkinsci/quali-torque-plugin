package org.jenkinsci.plugins.cs18.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Sandbox
{
    @JsonProperty("id")
    public String id;
    @JsonProperty("name")
    public String name;
    @JsonProperty("blueprint")
    public String blueprint;
    @JsonProperty("cloud_provider")
    public String cloudProvider;
    @JsonProperty("services")
    public List<Service> services;

}
