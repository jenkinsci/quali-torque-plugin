package org.jenkinsci.plugins.torque.service;

import org.jenkinsci.plugins.torque.api.*;

import java.io.IOException;

public interface EnvironmentAPIService
{
    ResponseData<CreateEnvResponse> createEnvironment(String spaceName, final CreateSandboxRequest req) throws IOException;
    ResponseData<Void> deleteEnvironment(String spaceName, String environmentId) throws IOException;
    ResponseData<EnvironmentResponse> getEnvironmentById(String spaceName, String environmentId) throws IOException;
}
