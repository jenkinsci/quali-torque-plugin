package org.jenkinsci.plugins.torque.api;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EnvironmentResponse  implements Serializable {
    @SerializedName("owner")
    UserResponse owner;

    @SerializedName("details")
    public
    EnvironmentDetailsResponse details;
}
