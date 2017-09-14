package org.jenkinsci.plugins.cs18.api;

import com.google.gson.annotations.SerializedName;

public class CreateSandboxResponse
{
    @SerializedName("id")
    public String id;
    @SerializedName("name")
    public String name;
}

