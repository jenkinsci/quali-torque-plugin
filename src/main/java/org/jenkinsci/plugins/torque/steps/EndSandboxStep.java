package org.jenkinsci.plugins.torque.steps;

import com.google.common.collect.ImmutableSet;
import hudson.AbortException;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.torque.PluginConstants;
import org.jenkinsci.plugins.torque.SandboxStepExecution;
import org.jenkinsci.plugins.torque.api.ResponseData;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.jenkinsci.plugins.torque.Messages;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * Created by shay-k on 20/06/2017.
 */
public class EndSandboxStep extends Step{
    private final String sandboxId;
    private final String spaceName;

    public String getSandboxId() {
        return sandboxId;
    }

    public String getSpaceName() {
        return spaceName;
    }


    @DataBoundConstructor
    public EndSandboxStep(@Nonnull String spaceName, @Nonnull String sandboxId)
    {
        this.sandboxId= sandboxId;
        this.spaceName= spaceName;
    }

    @Override
    public StepExecution start(StepContext stepContext) throws Exception {
        return new Execution(stepContext, this.spaceName, this.sandboxId);
    }

    public static class Execution extends SandboxStepExecution<Void> {
        private final String spaceName;
        private final String sandboxId;
        protected Execution(@Nonnull StepContext context, String spaceName, String sandboxId) throws Exception {
            super(context);
            this.spaceName = spaceName;
            this.sandboxId = sandboxId;
        }

        @Override
        protected Void run() throws Exception {
            TaskListener taskListener = getContext().get(TaskListener.class);
            taskListener.getLogger().println(String.format(Messages.EndSandbox_EndingMsg(sandboxId)));
            ResponseData<Void> res = sandboxAPIService.deleteSandbox(this.spaceName, this.sandboxId);
            if(!res.isSuccessful())
                throw new AbortException(String.format("status_code: %s error: %s", res.getStatusCode(), res.getError()));
            return null;
        }
    }

    @Extension
    public static class DescriptorImpl extends StepDescriptor {

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return ImmutableSet.of(Run.class, TaskListener.class);
        }

        @Override public String getFunctionName() {
            return PluginConstants.END_SANDBOX_FUNC_NAME;
        }

        @Override public String getDisplayName() {
            return Messages.EndSandbox_FuncDisplayName();
        }

    }
}