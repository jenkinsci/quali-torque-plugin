package org.jenkinsci.plugins.cloudshell.steps;

import com.google.common.collect.ImmutableSet;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.cloudshell.Config;
import org.jenkinsci.plugins.cloudshell.PluginConstants;
import org.jenkinsci.plugins.cloudshell.SandboxStepExecution;
import org.jenkinsci.plugins.cloudshell.api.CreateSandboxRequest;
import org.jenkinsci.plugins.cloudshell.api.CreateSandboxResponse;
import org.jenkinsci.plugins.cloudshell.api.ResponseData;
import org.jenkinsci.plugins.cloudshell.service.*;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * Created by shay-k on 20/06/2017.
 */
public class CreateSandboxStep extends Step{

    private final String blueprint;
    private String stage;
    private String sandboxName;
    private String serviceNameForHealthCheck;

    @DataBoundConstructor
    public CreateSandboxStep(@Nonnull String blueprint)
    {
        this.blueprint = blueprint;
    }

    @Override
    public StepExecution start(StepContext stepContext) throws Exception {
        return new Execution(stepContext, this.blueprint,this.sandboxName, this.stage, this.serviceNameForHealthCheck);
    }

    public String getBlueprint() {
        return blueprint;
    }

    public String getStage() {
        return stage;
    }

    public String getSandboxName() {
        return sandboxName;
    }

    public String getServiceNameForHealthCheck(){
        return serviceNameForHealthCheck;
    }

    @DataBoundSetter
    public void setSandboxName(String sandboxName) {
        this.sandboxName = sandboxName;
    }

    @DataBoundSetter
    public void setServiceNameForHealthCheck(String serviceNameForHealthCheck) {
        this.serviceNameForHealthCheck = serviceNameForHealthCheck;
    }

    @DataBoundSetter
    public void setStage(String stage) {
        this.stage = stage;
    }


    public static class Execution extends SandboxStepExecution<String> {
        private static final long serialVersionUID = 1L;
        private final String blueprint;
        private final String sandboxName;
        private final String stage;
        private String serviceNameForHealthCheck;

        protected Execution(@Nonnull StepContext context, String blueprint, String sandboxName, String stage, String serviceNameForHealthCheck) throws Exception {
            super(context);
            this.blueprint = blueprint;
            this.sandboxName = sandboxName;
            this.stage = stage;
            this.serviceNameForHealthCheck = serviceNameForHealthCheck;
        }


        @Override
        protected String run() throws Exception {
            TaskListener taskListener = getContext().get(TaskListener.class);
            try
            {
                CreateSandboxRequest req = new CreateSandboxRequest(getBlueprint(),getSandboxName(),getStage());
                ResponseData<CreateSandboxResponse> res = sandboxAPIService.createSandbox(req);
                if(!res.isSuccessful()){
                    throw new Exception(res.getError());
                }
                String sandboxId = res.getData().id;
                if(this.serviceNameForHealthCheck != null) {
                    sandboxAPIService.waitForService(sandboxId, this.serviceNameForHealthCheck, 600);
                }

                return sandboxId;

            } catch (Exception e) {
                taskListener.getLogger().println(e);
                throw e;
            }
        }

        public String getBlueprint() {
            return blueprint;
        }

        public String getSandboxName() {
            return sandboxName;
        }

        public String getStage() {
            return stage;
        }

    }

    @Extension
    public static class Descriptor extends StepDescriptor {

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return ImmutableSet.of(Run.class, TaskListener.class);
        }

        @Override public String getFunctionName() {
            return PluginConstants.CREATE_SANDBOX_FUNC_NAME;
        }

        @Override public String getDisplayName() {
            return PluginConstants.CREATE_SANDBOX_DISPLAY_NAME;
        }

    }
}