package org.jenkinsci.plugins.cloudshell.builders;

import com.google.gson.Gson;
import hudson.AbortException;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import org.jenkinsci.plugins.cloudshell.Config;
import org.jenkinsci.plugins.cloudshell.Messages;
import org.jenkinsci.plugins.cloudshell.PluginConstants;
import org.jenkinsci.plugins.cloudshell.api.CreateSandboxRequest;
import org.jenkinsci.plugins.cloudshell.api.CreateSandboxResponse;
import org.jenkinsci.plugins.cloudshell.api.ResponseData;
import org.jenkinsci.plugins.cloudshell.api.Sandbox;
import org.jenkinsci.plugins.cloudshell.service.SandboxAPIService;
import org.jenkinsci.plugins.cloudshell.service.SandboxAPIServiceImpl;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class CreateSandboxBuilder extends Builder {

    private final String blueprintName;
    private final String stage;
    private final String serviceNameForHealthCheck;
    private transient SandboxAPIService sandboxAPIService= null;

    @DataBoundConstructor
    public CreateSandboxBuilder(String blueprintName, String stage, String serviceNameForHealthCheck)
    {
        this.blueprintName = blueprintName;
        this.stage = stage;
        this.serviceNameForHealthCheck = serviceNameForHealthCheck;
    }

    public String getBlueprintName() {
        return blueprintName;
    }
    public String getStage() {
        return stage;
    }
    public String getServiceNameForHealthCheck() {
        return serviceNameForHealthCheck;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {

        try {
            Sandbox sandbox = createSandbox();
            CreateSandboxAction createSandboxAction = build.getAction(CreateSandboxAction.class);
            if(createSandboxAction == null) {
                createSandboxAction = new CreateSandboxAction();
                build.addAction(createSandboxAction);
            }
            createSandboxAction.addSandboxId(sandbox.id);

            String sandboxJson = new Gson().toJson(sandbox).toString();
            build.addAction(new VariableInjectionAction(PluginConstants.SANDBOX_ENVVAR,sandboxJson));
            return true;
        } catch (Exception e) {
            build.setResult(Result.FAILURE);
            e.printStackTrace(listener.getLogger());
            return false;
        }
    }
    private Sandbox createSandbox() throws IOException, TimeoutException, InterruptedException {
        CreateSandboxRequest req = new CreateSandboxRequest(getBlueprintName(),getStage());
        ResponseData<CreateSandboxResponse> res = sandboxAPIService.createSandbox(req);
        if(!res.isSuccessful()){
            throw new AbortException(res.getMessage());
        }

        String sandboxId = res.getData().id;
        if(getServiceNameForHealthCheck() != null)
            sandboxAPIService.waitForService(sandboxId, getServiceNameForHealthCheck(),10);

        ResponseData<Sandbox[]> sandboxesRes = sandboxAPIService.getSandboxes();
        if(!sandboxesRes.isSuccessful()) {
            throw new AbortException(res.getMessage());
        }
        for(Sandbox sandbox :sandboxesRes.getData()){
            if (sandbox.id == sandboxId){
                return sandbox;
            }
        }
        throw new AbortException(String.format(Messages.SandboxNotExistsError(),sandboxId));
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

   // @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        public DescriptorImpl(){
            load();
        }

        @Override
        public String getDisplayName() {
            return Messages.StartSandbox_FuncDisplayName();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

    }
}
