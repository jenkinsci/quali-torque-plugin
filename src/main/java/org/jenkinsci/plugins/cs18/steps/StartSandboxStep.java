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
import org.jenkinsci.plugins.cs18.api.*;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeoutException;

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
        return new Execution(stepContext, getBlueprint(), getStage(), getBranch(), getChangeset());
    }

    public static class Execution extends SandboxStepExecution<String> {
        private final String blueprint;
        private final String stage;
        private final String branch;
        private final String changeset;

        protected Execution(@Nonnull StepContext context, String blueprint, String stage, String branch, String changeset) throws Exception {
            super(context);
            this.blueprint = blueprint;
            this.stage = stage;
            this.branch = branch;
            this.changeset = changeset;
        }


        @Override
        protected String run() throws Exception {
            TaskListener taskListener = getContext().get(TaskListener.class);
            taskListener.getLogger().println(Messages.StartSandbox_StartingMsg());
            CreateSandboxRequest req = new CreateSandboxRequest(blueprint,stage, PluginHelpers.GenerateSandboxName(),branch,changeset,true);
            ResponseData<CreateSandboxResponse> res = sandboxAPIService.createSandbox(req);
            if(!res.isSuccessful())
                throw new AbortException(res.getError());

            String sandboxId = res.getData().id;
            return sandboxId;
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