package org.jenkinsci.plugins.cs18.steps;

import com.google.common.collect.ImmutableSet;
import hudson.AbortException;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.cs18.Messages;
import org.jenkinsci.plugins.cs18.PluginConstants;
import org.jenkinsci.plugins.cs18.PluginHelpers;
import org.jenkinsci.plugins.cs18.SandboxStepExecution;
import org.jenkinsci.plugins.cs18.api.CreateSandboxRequest;
import org.jenkinsci.plugins.cs18.api.CreateSandboxResponse;
import org.jenkinsci.plugins.cs18.api.ResponseData;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;

/**
 * Created by shay-k on 20/06/2017.
 */
public class StartSandboxStep extends AbstractStartSandboxStepImpl {

    @DataBoundConstructor
    public StartSandboxStep(@Nonnull String blueprint, @Nonnull Map<String, String> release)
    {
        super(blueprint, release);
    }

    @Override
    public StepExecution start(StepContext stepContext) throws Exception {
        return new Execution(stepContext, getBlueprint(), getRelease());
    }

    public static class Execution extends SandboxStepExecution<String> {
        private final String blueprint;
        private final Map<String, String> release;

        protected Execution(@Nonnull StepContext context, String blueprint, Map<String, String> release) throws Exception {
            super(context);
            this.blueprint = blueprint;
            this.release = release;
        }


        @Override
        protected String run() throws Exception {
            TaskListener taskListener = getContext().get(TaskListener.class);
            assert taskListener != null;
            taskListener.getLogger().println(Messages.StartSandbox_StartingMsg());
            CreateSandboxRequest req = new CreateSandboxRequest(blueprint, PluginHelpers.GenerateSandboxName(), release,true);
            ResponseData<CreateSandboxResponse> res = sandboxAPIService.createSandbox(req);
            if(!res.isSuccessful())
                throw new AbortException(res.getError());

            return res.getData().id;
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