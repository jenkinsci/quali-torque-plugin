package org.jenkinsci.plugins.torque.service;

import com.google.gson.GsonBuilder;
import org.jenkinsci.plugins.torque.api.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EnvironmentAPIServiceMock implements EnvironmentAPIService {
    private static List<EnvironmentResponse> environments = new ArrayList<EnvironmentResponse>();
    @Override
    public ResponseData<CreateEnvResponse> createEnvironment(String spaceName, CreateSandboxRequest req) throws IOException {
        CreateEnvResponse res = new CreateEnvResponse();
        res.id= UUID.randomUUID().toString();
        EnvironmentResponse environment = new EnvironmentResponse();
        environment.details = new EnvironmentDetailsResponse();
        environment.details.id = res.id;
        //environment.details.name = "sandbox-"+res.id;
        //environment.details.sandboxStatus = SandboxStatus.ACTIVE;
        environments.add(environment);
        String rawBodyJson  = new GsonBuilder().setPrettyPrinting().create().toJson(res);
        return ResponseData.ok(res,200, rawBodyJson);
    }

    @Override
    public ResponseData<Void> deleteEnvironment(String spaceName, String environmentId) throws IOException {
        EnvironmentResponse mach = null;
        for(EnvironmentResponse environment: environments){
            if(environment.details.id == environmentId){
                mach = environment;
            }
        }
        environments.remove(mach);
        return ResponseData.ok(null,200, null);
    }

    @Override
    public ResponseData<EnvironmentResponse> getEnvironmentById(String spaceName, String environmentId) throws IOException {
        EnvironmentResponse match = null;
        for(EnvironmentResponse sandbox: environments){
            if(sandbox.details.id == environmentId){
                match = sandbox;
            }
        }
        String rawBodyJson  = new GsonBuilder().setPrettyPrinting().create().toJson(match);
        return ResponseData.ok(match, 200, rawBodyJson);
    }
}
