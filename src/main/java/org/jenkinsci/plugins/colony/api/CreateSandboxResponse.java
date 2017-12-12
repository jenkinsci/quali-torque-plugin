package org.jenkinsci.plugins.colony.api;

import com.google.gson.annotations.SerializedName;

public class CreateSandboxResponse
{
    @SerializedName("id")
    public String id;
    @SerializedName("name")
    public String name;
}

