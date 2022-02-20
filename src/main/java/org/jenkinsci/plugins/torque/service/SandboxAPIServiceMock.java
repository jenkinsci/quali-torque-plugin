package org.jenkinsci.plugins.torque.service;

import com.google.gson.GsonBuilder;
import org.jenkinsci.plugins.torque.api.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SandboxAPIServiceMock implements SandboxAPIService{
    private static List<SingleSandbox> sandboxes = new ArrayList<SingleSandbox>();
    @Override
    public ResponseData<CreateSandboxResponse> createSandbox(String spaceName, CreateSandboxRequest req) throws IOException {
        CreateSandboxResponse res = new CreateSandboxResponse();
        res.id= UUID.randomUUID().toString();
        SingleSandbox sandbox = new SingleSandbox();
        sandbox.id = res.id;
        sandbox.name = "sandbox-"+res.id;
        sandbox.sandboxStatus = SandboxStatus.ACTIVE;
        sandboxes.add(sandbox);
        String rawBodyJson  = new GsonBuilder().setPrettyPrinting().create().toJson(res);
        return ResponseData.ok(res,200, rawBodyJson);
    }

    @Override
    public ResponseData<Void> deleteSandbox(String spaceName, String sandboxId) throws IOException {
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
    public ResponseData<Object> getSandboxById(String spaceName, String sandboxId) throws IOException {
        Object match = null;
        for(SingleSandbox sandbox: sandboxes){
            if(sandbox.id == sandboxId){
                match = sandbox;
            }
        }
        String rawBodyJson  = new GsonBuilder().setPrettyPrinting().create().toJson(match);
        return ResponseData.ok(match, 200, rawBodyJson);
    }
}
