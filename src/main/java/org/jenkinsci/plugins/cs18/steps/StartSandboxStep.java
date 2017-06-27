package org.jenkinsci.plugins.cs18.steps;

import com.google.common.collect.ImmutableSet;
import hudson.AbortException;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.cs18.Messages;
import org.jenkinsci.plugins.cs18.PluginConstants;
import org.jenkinsci.plugins.cs18.SandboxStepExecution;
import org.jenkinsci.plugins.cs18.api.CreateSandboxRequest;
import org.jenkinsci.plugins.cs18.api.CreateSandboxResponse;
import org.jenkinsci.plugins.cs18.api.ResponseData;
import org.jenkinsci.plugins.cs18.api.Sandbox;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * Created by shay-k on 20/06/2017.
 */
public class StartSandboxStep extends AbstractStartSandboxStepImpl {

    @DataBoundConstructor
    public StartSandboxStep(@Nonnull String blueprint)
    {
        super(blueprint);
    }

    @Override
    public StepExecution start(StepContext stepContext) throws Exception {
        return new Execution(stepContext, getBlueprint(), getStage(), getServiceNameForHealthCheck());
    }

    public static class Execution extends SandboxStepExecution<Sandbox> {
        private final String blueprint;
        private final String stage;
        private String serviceNameForHealthCheck;

        protected Execution(@Nonnull StepContext context, String blueprint, String stage, String serviceNameForHealthCheck) throws Exception {
            super(context);
            this.blueprint = blueprint;
            this.stage = stage;
            this.serviceNameForHealthCheck = serviceNameForHealthCheck;
        }


        @Override
        protected Sandbox run() throws Exception {
            TaskListener taskListener = getContext().get(TaskListener.class);
            taskListener.getLogger().println(Messages.StartSandbox_StartingMsg());
            CreateSandboxRequest req = new CreateSandboxRequest(blueprint,stage);
            ResponseData<CreateSandboxResponse> res;
            if(this.serviceNameForHealthCheck != null)
                res = sandboxAPIService.createSandbox(req,this.serviceNameForHealthCheck, 10);
            else
                res = sandboxAPIService.createSandbox(req);
            if(!res.isSuccessful())
                throw new AbortException(res.getMessage());


            String sandboxId = res.getData().id;
            ResponseData<Sandbox[]> sandboxesRes = sandboxAPIService.getSandboxes();
            if(!sandboxesRes.isSuccessful()) {
                throw new AbortException(res.getMessage());
            }
            for(Sandbox sandbox :sandboxesRes.getData()){
                if (sandbox.id.equals(sandboxId)){

                    return sandbox;
                }
            }
            throw new AbortException(String.format(Messages.SandboxNotExistsError(),sandboxId));
        }

        public String getBlueprint() {
            return blueprint;
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
            return  PluginConstants.START_SANDBOX_FUNC_NAME;
        }

        @Override public String getDisplayName() {
            return Messages.StartSandbox_FuncDisplayName();
        }

    }
}