package org.jenkinsci.plugins.torque.service;

import hudson.util.Secret;

/**
 * Created by shay-k on 18/06/2017.
 */
public class EnvironmentServiceConnection {
    public final String address;
    public final Secret token;
    public final int connectionTimeoutSec;
    public final int readTimeoutSec;
    public EnvironmentServiceConnection(String serverAddress, Secret token, int connectionTimeoutSec, int readTimeoutSec) {
        this.address = serverAddress;
        this.token = token;
        this.connectionTimeoutSec = connectionTimeoutSec;
        this.readTimeoutSec = readTimeoutSec;
    }

    public String getAuthorizationHeader(){
        return String.format("Bearer %s", this.token.getPlainText());
    }
}