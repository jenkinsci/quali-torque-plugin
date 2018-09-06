package org.jenkinsci.plugins.colony.service;

import org.jenkinsci.plugins.colony.api.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface SandboxAPIService
{
    ResponseData<CreateSandboxResponse> createSandbox(String spaceName, final CreateSandboxRequest req) throws IOException;
    ResponseData<Void> deleteSandbox(String spaceName, String sandboxId) throws IOException;
    ResponseData<Object> getSandboxById(String spaceName, String sandboxId) throws IOException;
}
