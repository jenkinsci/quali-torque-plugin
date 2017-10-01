package org.jenkinsci.plugins.cs18.builders;

import com.google.gson.Gson;
import hudson.AbortException;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import org.jenkinsci.plugins.cs18.Messages;
import org.jenkinsci.plugins.cs18.PluginConstants;
import org.jenkinsci.plugins.cs18.PluginHelpers;
import org.jenkinsci.plugins.cs18.api.CreateSandboxRequest;
import org.jenkinsci.plugins.cs18.api.CreateSandboxResponse;
import org.jenkinsci.plugins.cs18.api.ResponseData;
import org.jenkinsci.plugins.cs18.api.Sandbox;
import org.jenkinsci.plugins.cs18.service.SandboxAPIService;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class CreateSandboxBuilder extends Builder {

    private final String blueprint;
    private final Map<String, String> release;
    private transient SandboxAPIService sandboxAPIService= null;

    @DataBoundConstructor
    public CreateSandboxBuilder(String blueprint, Map<String, String> release)
    {
        this.blueprint = blueprint;
        this.release = release;
    }


    public String getBlueprint() {
        return this.blueprint;
    }

    public Map<String, String> getRelease() {
        return this.release;
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

            String sandboxJson = new Gson().toJson(sandbox);
            build.addAction(new VariableInjectionAction(PluginConstants.SANDBOX_ENVVAR,sandboxJson));
            return true;
        } catch (Exception e) {
            build.setResult(Result.FAILURE);
            e.printStackTrace(listener.getLogger());
            return false;
        }
    }
    private Sandbox createSandbox() throws IOException, TimeoutException, InterruptedException {
        CreateSandboxRequest req = new CreateSandboxRequest(getBlueprint(),
                PluginHelpers.GenerateSandboxName(),
                getRelease(),
                true);

        ResponseData<CreateSandboxResponse> res = sandboxAPIService.createSandbox(req);
        if(!res.isSuccessful()){
            throw new AbortException(res.getError());
        }

        String sandboxId = res.getData().id;
        ResponseData<Sandbox[]> sandboxesRes = sandboxAPIService.getSandboxes();
        if(!sandboxesRes.isSuccessful()) {
            throw new AbortException(res.getError());
        }
        for(Sandbox sandbox :sandboxesRes.getData()){
            if (sandbox.id.equals(sandboxId)){
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
