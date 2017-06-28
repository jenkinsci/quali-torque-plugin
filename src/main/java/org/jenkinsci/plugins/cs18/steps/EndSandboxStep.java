package org.jenkinsci.plugins.cs18.steps;

import com.google.common.collect.ImmutableSet;
import hudson.AbortException;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.cs18.Messages;
import org.jenkinsci.plugins.cs18.PluginConstants;
import org.jenkinsci.plugins.cs18.SandboxStepExecution;
import org.jenkinsci.plugins.cs18.api.ResponseData;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Set;

/**
 * Created by shay-k on 20/06/2017.
 */
public class EndSandboxStep extends Step{
    private final String sandboxId;

    public String getSandboxId() {
        return sandboxId;
    }

    @DataBoundConstructor
    public EndSandboxStep(@Nonnull String sandboxId)
    {
        this.sandboxId= sandboxId;
    }

    @Override
    public StepExecution start(StepContext stepContext) throws Exception {
        return new Execution(stepContext, this.sandboxId);
    }

    public static class Execution extends SandboxStepExecution<Void> {
        private final String sandboxId;
        protected Execution(@Nonnull StepContext context, String sandboxId) throws Exception {
            super(context);
            this.sandboxId = sandboxId;
        }

        @Override
        protected Void run() throws Exception {
            TaskListener taskListener = getContext().get(TaskListener.class);
            taskListener.getLogger().println(String.format(Messages.EndSandbox_EndingMsg(sandboxId)));
            ResponseData<Void> res = sandboxAPIService.deleteSandbox(sandboxId);
            if(!res.isSuccessful())
                throw new AbortException(res.getError());
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