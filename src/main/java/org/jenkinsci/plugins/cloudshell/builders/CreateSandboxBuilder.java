package org.jenkinsci.plugins.cloudshell.builders;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import org.jenkinsci.plugins.cloudshell.Config;
import org.jenkinsci.plugins.cloudshell.PluginConstants;
import org.jenkinsci.plugins.cloudshell.api.CreateSandboxRequest;
import org.jenkinsci.plugins.cloudshell.api.CreateSandboxResponse;
import org.jenkinsci.plugins.cloudshell.api.ResponseData;
import org.jenkinsci.plugins.cloudshell.service.SandboxAPIService;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

public class CreateSandboxBuilder extends Builder {

    private final String blueprintName;
    private final String sandboxName;
    private final String stage;
    private transient SandboxAPIService sandboxAPIService = null;

    @DataBoundConstructor
    public CreateSandboxBuilder(String blueprintName, String sandboxName, String stage)
    {
        this.blueprintName = blueprintName;
        this.sandboxName = sandboxName;
        this.stage = stage;
    }

    public String getBlueprintName() {
        return blueprintName;
    }
    public String getSandboxName() {
        return sandboxName;
    }
    public String getStage() {
        return stage;
    }

    public SandboxAPIService getSandboxAPIService() throws Exception {
        if(sandboxAPIService == null)
            sandboxAPIService = new SandboxAPIService(Config.DESCRIPTOR.getCloudShellConnection());
        return sandboxAPIService;
    }
    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        try
        {
            CreateSandboxRequest req = new CreateSandboxRequest(getBlueprintName(),getSandboxName(),getStage());
            ResponseData<CreateSandboxResponse> res = getSandboxAPIService().createSandbox(req);
            if(!res.isSuccessful()){
                throw new Exception(res.getError());
            }
            return true;
        } catch (Exception e) {
            listener.getLogger().println(e);
            build.setResult(Result.FAILURE);
            return false;
        }
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        public DescriptorImpl(){
            load();
        }

        @Override
        public String getDisplayName() {
            return PluginConstants.CREATE_SANDBOX_DISPLAY_NAME;
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

    }
}
