package org.jenkinsci.plugins.cs18.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.List;

public class Sandbox implements Serializable
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
