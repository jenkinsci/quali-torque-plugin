package org.jenkinsci.plugins.cloudshell.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Service
{
    @JsonProperty("name")
    public String name;
    @JsonProperty("addresses")
    public List<String> addresses;
}
