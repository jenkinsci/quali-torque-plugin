package org.jenkinsci.plugins.cs18.api;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Service implements Serializable
{
    @SerializedName("name")
    public String name;
    @SerializedName("addresses")
    public List<String> addresses;
    @SerializedName("deployment_status")
    public String deploymentStatus;
}
