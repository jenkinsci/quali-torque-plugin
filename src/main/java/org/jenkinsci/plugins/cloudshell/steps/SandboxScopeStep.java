package org.jenkinsci.plugins.cloudshell.steps;

import com.google.common.collect.ImmutableSet;
import hudson.EnvVars;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.cloudshell.Config;
import org.jenkinsci.plugins.cloudshell.PluginConstants;
import org.jenkinsci.plugins.cloudshell.api.CreateSandboxRequest;
import org.jenkinsci.plugins.cloudshell.api.CreateSandboxResponse;
import org.jenkinsci.plugins.cloudshell.api.ResponseData;
import org.jenkinsci.plugins.cloudshell.service.SandboxAPIService;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SandboxScopeStep extends Step {

    //private static final Logger LOGGER = Logger.getLogger(SandboxScopeStep.class.getName());
    private final String blueprint;
    private String stage;
    private String sandboxName;
    private String serviceNameForHealthCheck;

    @DataBoundConstructor
    public SandboxScopeStep(@Nonnull String blueprint)
    {
        this.blueprint = blueprint;
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

    @Override
    public StepExecution start(StepContext stepContext) throws Exception {
        return new SandboxScopeStep.Execution(stepContext, this.blueprint,this.sandboxName, this.stage, this.serviceNameForHealthCheck);
    }

    public static class Execution extends AbstractStepExecutionImpl {

        private transient SandboxAPIService sandboxAPIService;
        private static final long serialVersionUID = 1;
        private final String blueprint;
        private final String sandboxName;
        private final String stage;
        private String serviceNameForHealthCheck;
        private String sandboxId;

        public Execution(@Nonnull StepContext context, String blueprint, String sandboxName, String stage, String serviceNameForHealthCheck) throws Exception {
            super(context);
            this.blueprint = blueprint;
            this.sandboxName = sandboxName;
            this.stage = stage;
            this.serviceNameForHealthCheck = serviceNameForHealthCheck;
            sandboxAPIService = new SandboxAPIService(Config.DESCRIPTOR.getCloudShellConnection());
        }


        @Override
        public boolean start() throws Exception {
            createSandbox();
            getContext().newBodyInvoker().
                    withContext(EnvironmentExpander.merge(getContext().get(EnvironmentExpander.class), new EnvExpander(sandboxId))).
                    withCallback(new Callback(sandboxId,sandboxAPIService)).
                    start();
            return false;
        }

        @Override
        public void stop(@Nonnull Throwable throwable) throws Exception {
            if (sandboxId != null) {
                deleteSandbox(sandboxId,sandboxAPIService,getContext());
            }
        }


        private boolean createSandbox() throws Exception {
            TaskListener taskListener = getContext().get(TaskListener.class);
            try
            {
                CreateSandboxRequest req = new CreateSandboxRequest(blueprint,sandboxName,stage);
                ResponseData<CreateSandboxResponse> res = sandboxAPIService.createSandbox(req);
                if(!res.isSuccessful()){
                    throw new Exception(res.getError());
                }

                sandboxId = res.getData().id;
                if(this.serviceNameForHealthCheck != null) {
                    sandboxAPIService.waitForService(sandboxId, this.serviceNameForHealthCheck, 600);
                }

            } catch (Exception e) {
                taskListener.getLogger().println(e);
                throw e;
            }
            return false;
        }

        private static void deleteSandbox(String sandboxId, SandboxAPIService sandboxAPIService,StepContext stepContext) throws Exception {
            TaskListener taskListener = stepContext.get(TaskListener.class);
            try
            {
                ResponseData<Void> res = sandboxAPIService.deleteSandbox(sandboxId);
                if(!res.isSuccessful()){
                    throw new Exception(res.getError());
                }
            } catch (Exception e) {
                taskListener.getLogger().println(e);
                throw e;
            }
        }

        private static class Callback extends BodyExecutionCallback.TailCall {
            private static final long serialVersionUID = 1;
            private transient SandboxAPIService sandboxAPIService;
            private final String sandboxId;

            Callback(String sandboxId,SandboxAPIService sandboxAPIService) {
                this.sandboxId = sandboxId;
                this.sandboxAPIService = sandboxAPIService;
            }
            @Override
            protected void finished(StepContext stepContext) throws Exception {
                deleteSandbox(sandboxId, sandboxAPIService, stepContext);
            }

        }

    }

    private static final class EnvExpander extends EnvironmentExpander {
        private static final long serialVersionUID = 1;
        private final Map<String,String> overrides;
        private EnvExpander(String sandboxId) {
            this.overrides = new HashMap<>();
            this.overrides.put("SANDBOX_ID", sandboxId);
        }

        @Override
        public void expand(@Nonnull EnvVars envVars) throws IOException, InterruptedException {
            envVars.overrideAll(overrides);
        }
    }

    @Extension
    public static class Descriptor extends StepDescriptor {

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return ImmutableSet.of(Run.class, TaskListener.class);
        }

        @Override
        public String getFunctionName() {
            return PluginConstants.WITH_SANDBOX_FUNC_NAME;
        }

        @Override
        public String getDisplayName() {
            return PluginConstants.WITH_SANDBOX_DISPLAY_NAME;
        }

        @Override
        public boolean takesImplicitBlockArgument() {
            return true;
        }

    }
}
