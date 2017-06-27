package org.jenkinsci.plugins.cs18.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class Service implements Serializable
{
    @JsonProperty("name")
    public String name;
    @JsonProperty("addresses")
    public List<String> addresses;
}
