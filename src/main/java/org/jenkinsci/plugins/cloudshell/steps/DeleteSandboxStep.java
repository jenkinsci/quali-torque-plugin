package org.jenkinsci.plugins.cloudshell.steps;

import com.google.common.collect.ImmutableSet;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.cloudshell.PluginConstants;
import org.jenkinsci.plugins.cloudshell.SandboxStepExecution;
import org.jenkinsci.plugins.cloudshell.api.ResponseData;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * Created by shay-k on 20/06/2017.
 */
public class DeleteSandboxStep extends Step{
    private final String sandboxId;

    public String getSandboxId() {
        return sandboxId;
    }

    @DataBoundConstructor
    public DeleteSandboxStep(@Nonnull String sandboxId)
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
            return PluginConstants.DELETE_SANDBOX_FUNC_NAME;
        }

        @Override public String getDisplayName() {
            return PluginConstants.DELETE_SANDBOX_DISPLAY_NAME;
        }

    }
}