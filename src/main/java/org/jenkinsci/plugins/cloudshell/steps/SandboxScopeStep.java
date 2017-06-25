package org.jenkinsci.plugins.cloudshell.steps;

import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import hudson.AbortException;
import hudson.EnvVars;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.cloudshell.Config;
import org.jenkinsci.plugins.cloudshell.Messages;
import org.jenkinsci.plugins.cloudshell.PluginConstants;
import org.jenkinsci.plugins.cloudshell.api.CreateSandboxRequest;
import org.jenkinsci.plugins.cloudshell.api.CreateSandboxResponse;
import org.jenkinsci.plugins.cloudshell.api.ResponseData;
import org.jenkinsci.plugins.cloudshell.api.Sandbox;
import org.jenkinsci.plugins.cloudshell.service.SandboxAPIService;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeoutException;

public class SandboxScopeStep extends AbstractCreateSandboxStepImpl {

    @DataBoundConstructor
    public SandboxScopeStep(@Nonnull String blueprint)
    {
        super(blueprint);
    }

    @Override
    public StepExecution start(StepContext stepContext) throws Exception {
        return new SandboxScopeStep.Execution(stepContext, getBlueprint(), getSandboxName(), getStage(), getServiceNameForHealthCheck());
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
            sandboxAPIService = Config.CreateSandboxAPIService();
        }


        @Override
        public boolean start() throws Exception {
            Sandbox sandbox = createSandbox();
            final EnvVars env = new EnvVars();
            String sandboxJson = new Gson().toJson(sandbox).toString();
            env.override(PluginConstants.SANDBOX_ENVVAR, sandboxJson);


            EnvironmentExpander expander = new EnvironmentExpander() {
                @Override
                public void expand(@Nonnull EnvVars envVars) throws IOException, InterruptedException {
                    envVars.overrideAll(env);
                }
            };
            getContext().newBodyInvoker().
                    withContext(EnvironmentExpander.merge(getContext().get(EnvironmentExpander.class), expander)).
                    withCallback(new Callback(sandboxId, sandboxAPIService)).
                    start();
            return false;
        }

        @Override
        public void stop(@Nonnull Throwable throwable) throws Exception {
            if (sandboxId != null) {
                deleteSandbox(sandboxId, sandboxAPIService,getContext());
            }
        }


        private Sandbox createSandbox() throws IOException, TimeoutException, InterruptedException {
            CreateSandboxRequest req = new CreateSandboxRequest(blueprint,sandboxName,stage);
            ResponseData<CreateSandboxResponse> res = sandboxAPIService.createSandbox(req);
            if(!res.isSuccessful()){
                throw new AbortException(res.getMessage());
            }

            sandboxId = res.getData().id;
            if(this.serviceNameForHealthCheck != null)
                sandboxAPIService.waitForService(sandboxId, this.serviceNameForHealthCheck,10);

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

        private static void deleteSandbox(String sandboxId, SandboxAPIService sandboxAPIService, StepContext stepContext) throws Exception {
            ResponseData<Void> res = sandboxAPIService.deleteSandbox(sandboxId);
            if(!res.isSuccessful())
                throw new AbortException(res.getError());
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
            return Messages.WithSandbox_FuncDisplayName();
        }

        @Override
        public boolean takesImplicitBlockArgument() {
            return true;
        }

    }
}
