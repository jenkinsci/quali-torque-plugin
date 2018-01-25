package org.jenkinsci.plugins.colony.service;

/**
 * Created by shay-k on 18/06/2017.
 */
public class SandboxServiceConnection {
    public final String address;
    public final int port;
    public final String token;
    public final int connectionTimeoutSec;
    public final int readTimeoutSec;
    public SandboxServiceConnection(String serverAddress, int port, String token, int connectionTimeoutSec, int readTimeoutSec){
        this.port = port;
        this.address= serverAddress;
        this.token = String.format("Bearer %s",token);
        this.connectionTimeoutSec = connectionTimeoutSec;
        this.readTimeoutSec = readTimeoutSec;
    }
}