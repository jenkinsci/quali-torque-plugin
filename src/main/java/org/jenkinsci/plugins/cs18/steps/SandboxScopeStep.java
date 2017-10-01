package org.jenkinsci.plugins.cs18.steps;

import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import hudson.AbortException;
import hudson.EnvVars;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.cs18.Config;
import org.jenkinsci.plugins.cs18.Messages;
import org.jenkinsci.plugins.cs18.PluginConstants;
import org.jenkinsci.plugins.cs18.PluginHelpers;
import org.jenkinsci.plugins.cs18.api.*;
import org.jenkinsci.plugins.cs18.service.SandboxAPIService;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class SandboxScopeStep extends AbstractStartSandboxStepImpl {

    @DataBoundConstructor
    public SandboxScopeStep(@Nonnull String blueprint, @Nonnull Map<String, String> release)
    {
        super(blueprint, release);
    }

    @Override
    public StepExecution start(StepContext stepContext) throws Exception {
        return new SandboxScopeStep.Execution(stepContext, getBlueprint(), getRelease());
    }

    public static class Execution extends AbstractStepExecutionImpl {

        private transient SandboxAPIService sandboxAPIService;
        private static final long serialVersionUID = 1;
        private final String blueprint;
        private final Map<String, String> release;
        private String sandboxId;

        public Execution(@Nonnull StepContext context, String blueprint, Map<String, String> release) throws Exception {
            super(context);
            this.blueprint = blueprint;
            this.release = release;
            sandboxAPIService = Config.CreateSandboxAPIService();
        }

        private transient SingleSandbox sandbox;

        @Override
        public boolean start() throws Exception {
            getContext().newBodyInvoker().
                    withContext(createSandbox()).
                    withContext(EnvironmentExpander.merge(getContext().get(EnvironmentExpander.class), createEnvironmentExpander())).
                    withCallback(new Callback(sandboxId, sandboxAPIService)).
                    start();
            return false;
        }

        @Override
        public void stop(@Nonnull Throwable throwable) throws Exception {
            if (sandboxId != null) {
                endSandbox(sandboxId, sandboxAPIService,getContext());
            }
        }

        private EnvironmentExpander createEnvironmentExpander()
        {
            final EnvVars env = new EnvVars();

            String sandboxJson = new Gson().toJson(sandbox);
            env.override(PluginConstants.SANDBOX_ENVVAR, sandboxJson);
            EnvironmentExpander expander = new EnvironmentExpander() {
                @Override
                public void expand(@Nonnull EnvVars envVars) throws IOException, InterruptedException {
                    envVars.overrideAll(env);
                }
            };
            return expander;
        }

        private boolean createSandbox() throws Exception {
            CreateSandboxRequest req = new CreateSandboxRequest(blueprint, PluginHelpers.GenerateSandboxName(), release,true);
            ResponseData<CreateSandboxResponse> res = sandboxAPIService.createSandbox(req);
            if(!res.isSuccessful()){
                throw new AbortException(res.getError());
            }

            sandboxId = res.getData().id;

            ResponseData<SingleSandbox> sandboxByIdRes=sandboxAPIService.getSandboxById(sandboxId);
            if (!sandboxByIdRes.isSuccessful()){
                throw new AbortException(res.getError());
            }
            sandbox = sandboxByIdRes.getData();
            return true;

//            endSandbox(sandboxId,sandboxAPIService,getContext());
//            throw new AbortException(String.format(Messages.SandboxNotExistsError(),sandboxId));
        }

        private static void endSandbox(String sandboxId, SandboxAPIService sandboxAPIService, StepContext stepContext) throws Exception {
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
                endSandbox(sandboxId, sandboxAPIService, stepContext);
            }

        }

    }
    //@Extension
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
