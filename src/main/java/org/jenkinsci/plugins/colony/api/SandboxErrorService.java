package org.jenkinsci.plugins.colony.api;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

//Error from the sandbox get by id response
public class SandboxErrorService implements Serializable {
    @SerializedName("time")
    public String time;
    @SerializedName("code")
    public String code;
    @SerializedName("message")
    public String message;
}