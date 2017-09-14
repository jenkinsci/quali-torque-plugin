package org.jenkinsci.plugins.cs18.api;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Sandbox implements Serializable
{
    @SerializedName("id")
    public String id;
    @SerializedName("name")
    public String name;
    @SerializedName("blueprint")
    public String blueprint;
    @SerializedName("cloud_provider")
    public String cloudProvider;
    @SerializedName("services")
    public List<Service> services;
    @SerializedName("deployment_status")
    public String deploymentStatus;
}
