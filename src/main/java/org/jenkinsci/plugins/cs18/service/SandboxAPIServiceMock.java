package org.jenkinsci.plugins.cs18.service;

import org.jenkinsci.plugins.cs18.api.CreateSandboxRequest;
import org.jenkinsci.plugins.cs18.api.CreateSandboxResponse;
import org.jenkinsci.plugins.cs18.api.ResponseData;
import org.jenkinsci.plugins.cs18.api.Sandbox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class SandboxAPIServiceMock implements SandboxAPIService{
    private List<Sandbox> sandboxes = new ArrayList<Sandbox>();
    @Override
    public ResponseData<CreateSandboxResponse> createSandbox(CreateSandboxRequest req) throws IOException {
        CreateSandboxResponse res = new CreateSandboxResponse();
        res.id= UUID.randomUUID().toString();
        Sandbox sandbox = new Sandbox();
        sandbox.id = res.id;
        sandbox.name = "sandbox-"+res.id;
        sandboxes.add(sandbox);
        return ResponseData.ok(res,200);
    }

    @Override
    public ResponseData<CreateSandboxResponse> createSandbox(CreateSandboxRequest req, String healthCheckService, double timeoutMinutes) throws IOException, TimeoutException, InterruptedException {
        return createSandbox(req);
    }

    @Override
    public ResponseData<Void> deleteSandbox(String sandboxId) throws IOException {
        Sandbox mach = null;
        for(Sandbox sandbox: sandboxes){
            if(sandbox.id == sandboxId){
                mach = sandbox;
            }
        }
        sandboxes.remove(mach);
        return ResponseData.ok(null,200);
    }

    @Override
    public ResponseData<Sandbox[]> getSandboxes() throws IOException {
        Sandbox [] sandboxesArr = new Sandbox[sandboxes.size()];
        sandboxesArr = sandboxes.toArray(sandboxesArr);
        return ResponseData.ok(sandboxesArr,200);
    }

    @Override
    public void waitForService(String sandboxId, String serviceName, double timeoutMinutes) throws TimeoutException, IOException, InterruptedException {

    }
}
