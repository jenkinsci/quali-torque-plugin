package org.jenkinsci.plugins.colony.service;

import org.jenkinsci.plugins.colony.api.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SandboxAPIServiceMock implements SandboxAPIService{
    private static List<SingleSandbox> sandboxes = new ArrayList<SingleSandbox>();
    @Override
    public ResponseData<CreateSandboxResponse> createSandbox(CreateSandboxRequest req) throws IOException {
        CreateSandboxResponse res = new CreateSandboxResponse();
        res.id= UUID.randomUUID().toString();
        SingleSandbox sandbox = new SingleSandbox();
        sandbox.id = res.id;
        sandbox.name = "sandbox-"+res.id;
        sandbox.deploymentStatus = SandboxDeploymentStatus.DONE;
        sandboxes.add(sandbox);
        return ResponseData.ok(res,200,null);
    }

    @Override
    public ResponseData<Void> deleteSandbox(String sandboxId) throws IOException {
        SingleSandbox mach = null;
        for(SingleSandbox sandbox: sandboxes){
            if(sandbox.id == sandboxId){
                mach = sandbox;
            }
        }
        sandboxes.remove(mach);
        return ResponseData.ok(null,200, null);
    }

    @Override
    public ResponseData<Sandbox[]> getSandboxes() throws IOException {
        Sandbox [] sandboxesArr = new Sandbox[sandboxes.size()];
        sandboxesArr = sandboxes.toArray(sandboxesArr);
        return ResponseData.ok(sandboxesArr,200, null);
    }

    @Override
    public ResponseData<SingleSandbox> getSandboxById(String sandboxId) throws IOException {
        SingleSandbox mach = null;
        for(SingleSandbox sandbox: sandboxes){
            if(sandbox.id == sandboxId){
                mach = sandbox;
            }
        }
        return ResponseData.ok(mach, 200, null);
    }
}
