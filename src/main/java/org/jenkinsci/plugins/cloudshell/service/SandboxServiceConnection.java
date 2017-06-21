package org.jenkinsci.plugins.cloudshell.service;

/**
 * Created by shay-k on 18/06/2017.
 */
public class SandboxServiceConnection {
    public final String address;
    public final int port;
    public final int connectionTimeoutSec;
    public SandboxServiceConnection(String serverAddress, int port, int connectionTimeoutSec){
        this.port = port;
        this.address= serverAddress;
        this.connectionTimeoutSec = connectionTimeoutSec;
    }
}