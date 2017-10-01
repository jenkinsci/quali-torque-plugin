package org.jenkinsci.plugins.cs18.service;

import org.jenkinsci.plugins.cs18.api.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface SandboxAPIService
{
    ResponseData<CreateSandboxResponse> createSandbox(final CreateSandboxRequest req) throws IOException;
    ResponseData<Void> deleteSandbox(String sandboxId) throws IOException;
    ResponseData<Sandbox[]> getSandboxes() throws IOException;
    ResponseData<SingleSandbox> getSandboxById(String sandboxId) throws IOException;
}
