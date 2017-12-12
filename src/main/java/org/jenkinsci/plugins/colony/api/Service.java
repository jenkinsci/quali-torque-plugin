package org.jenkinsci.plugins.colony.api;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Service implements Serializable
{
    @SerializedName("name")
    public String name;
    @SerializedName("shortcuts")
    public List<String> shortcuts;
    @SerializedName("deployment_status")
    public String deploymentStatus;
}
