package org.jenkinsci.plugins.torque.service;

import org.jenkinsci.plugins.torque.api.*;

import java.io.IOException;

public interface SandboxAPIService
{
    ResponseData<CreateSandboxResponse> createSandbox(String spaceName, final CreateSandboxRequest req) throws IOException;
    ResponseData<Void> deleteSandbox(String spaceName, String sandboxId) throws IOException;
    ResponseData<Object> getSandboxById(String spaceName, String sandboxId) throws IOException;
}
