package org.jenkinsci.plugins.cloudshell.service;

import org.jenkinsci.plugins.cloudshell.api.CreateSandboxRequest;
import org.jenkinsci.plugins.cloudshell.api.CreateSandboxResponse;
import org.jenkinsci.plugins.cloudshell.api.ResponseData;
import org.jenkinsci.plugins.cloudshell.api.Sandbox;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface SandboxAPIService
{
    ResponseData<CreateSandboxResponse> createSandbox(final CreateSandboxRequest req) throws IOException;
    ResponseData<CreateSandboxResponse> createSandbox(final CreateSandboxRequest req, String healthCheckService,double timeoutMinutes) throws IOException, TimeoutException, InterruptedException;
    public ResponseData<Void> deleteSandbox(String sandboxId) throws IOException;
    ResponseData<Sandbox[]> getSandboxes() throws IOException;
    void waitForService(String sandboxId, String serviceName, double timeoutMinutes) throws TimeoutException, IOException, InterruptedException;
}
