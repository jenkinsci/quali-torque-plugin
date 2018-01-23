package org.jenkinsci.plugins.colony.steps;

import com.google.common.collect.ImmutableSet;
import hudson.AbortException;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.colony.Messages;
import org.jenkinsci.plugins.colony.PluginConstants;
import org.jenkinsci.plugins.colony.PluginHelpers;
import org.jenkinsci.plugins.colony.SandboxStepExecution;
import org.jenkinsci.plugins.colony.api.CreateSandboxRequest;
import org.jenkinsci.plugins.colony.api.CreateSandboxResponse;
import org.jenkinsci.plugins.colony.api.ResponseData;
import org.jenkinsci.plugins.workflow.steps.Step;
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
public class StartSandboxStep extends Step {

    private final String spaceName;
    private final String blueprint;
    private String sandboxName;
    private Map<String, String> release;

    @DataBoundConstructor
    public StartSandboxStep(@Nonnull String spaceName, @Nonnull String blueprint, @Nonnull String sandboxName, @Nonnull Map<String, String> release)
    {
        this.spaceName = spaceName;
        this.blueprint = blueprint;
        this.sandboxName = sandboxName;
        this.release = release;
    }

    @Override
    public StepExecution start(StepContext stepContext) throws Exception {
        return new Execution(stepContext, this.spaceName, this.blueprint, this.sandboxName, this.release);
    }

    public static class Execution extends SandboxStepExecution<String> {
        private final String spaceName;
        private final String sandboxName;
        private final String blueprint;
        private final Map<String, String> release;

        protected Execution(@Nonnull StepContext context, @Nonnull String spaceName, @Nonnull String blueprint, @Nonnull String sandboxName, @Nonnull Map<String, String> release) throws Exception {
            super(context);
            this.spaceName = spaceName;
            this.sandboxName = sandboxName;
            this.blueprint = blueprint;
            this.release = release;
        }

        @Override
        protected String run() throws Exception {
            TaskListener taskListener = getContext().get(TaskListener.class);
            assert taskListener != null;
            taskListener.getLogger().println(Messages.StartSandbox_StartingMsg());
            String sandboxName = this.sandboxName.isEmpty()? PluginHelpers.GenerateSandboxName():this.sandboxName;
            CreateSandboxRequest req = new CreateSandboxRequest(this.blueprint, sandboxName, this.release,true);
            ResponseData<CreateSandboxResponse> res = sandboxAPIService.createSandbox(this.spaceName, req);
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