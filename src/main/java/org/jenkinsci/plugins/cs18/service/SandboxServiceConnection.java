package org.jenkinsci.plugins.cs18.service;

/**
 * Created by shay-k on 18/06/2017.
 */
public class SandboxServiceConnection {
    public final String address;
    public final int port;
    public final int connectionTimeoutSec;
    public final int readTimeoutSec;
    public SandboxServiceConnection(String serverAddress, int port, int connectionTimeoutSec, int readTimeoutSec){
        this.port = port;
        this.address= serverAddress;
        this.connectionTimeoutSec = connectionTimeoutSec;
        this.readTimeoutSec = readTimeoutSec;
    }
}