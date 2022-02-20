package org.jenkinsci.plugins.torque.steps;

import com.google.common.collect.ImmutableSet;
import hudson.AbortException;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.torque.Messages;
import org.jenkinsci.plugins.torque.PluginConstants;
import org.jenkinsci.plugins.torque.PluginHelpers;
import org.jenkinsci.plugins.torque.SandboxStepExecution;
import org.jenkinsci.plugins.torque.api.CreateSandboxRequest;
import org.jenkinsci.plugins.torque.api.CreateSandboxResponse;
import org.jenkinsci.plugins.torque.api.ResponseData;
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
    private String duration;
    private Map<String, String> artifacts;
    private Map<String, String> inputs;

    @DataBoundConstructor
    public StartSandboxStep(@Nonnull String spaceName, @Nonnull String blueprint, @Nonnull String sandboxName, @Nonnull String duration, @Nonnull Map<String, String> artifacts, @Nonnull Map<String, String> inputs)
    {
        this.spaceName = spaceName;
        this.blueprint = blueprint;
        this.sandboxName = sandboxName;
        this.duration = duration;
        this.artifacts = artifacts;
        this.inputs = inputs;
    }

    @Override
    public StepExecution start(StepContext stepContext) throws Exception {
        return new Execution(stepContext, this.spaceName, this.blueprint, this.sandboxName, this.duration, this.artifacts, this.inputs);
    }

    public static class Execution extends SandboxStepExecution<String> {
        private final String spaceName;
        private final String sandboxName;
        private final String blueprint;
        private final String duration;
        private final Map<String, String> artifacts;
        private final Map<String, String> inputs;

        protected Execution(@Nonnull StepContext context, @Nonnull String spaceName, @Nonnull String blueprint, @Nonnull String sandboxName, @Nonnull String duration, @Nonnull Map<String, String> artifacts, @Nonnull Map<String, String> inputs) throws Exception {
            super(context);
            this.spaceName = spaceName;
            this.sandboxName = sandboxName;
            this.blueprint = blueprint;
            this.duration = duration;
            this.artifacts = artifacts;
            this.inputs = inputs;
        }

        @Override
        protected String run() throws Exception {
            TaskListener taskListener = getContext().get(TaskListener.class);
            assert taskListener != null;
            taskListener.getLogger().println(Messages.StartSandbox_StartingMsg());
            String sandboxName = this.sandboxName.isEmpty()? PluginHelpers.GenerateSandboxName():this.sandboxName;
            CreateSandboxRequest req = new CreateSandboxRequest(this.blueprint, sandboxName, this.artifacts,true, this.inputs, this.duration);
            ResponseData<CreateSandboxResponse> res = sandboxAPIService.createSandbox(this.spaceName, req);
            if(!res.isSuccessful())
                throw new AbortException(String.format("status_code: %s error: %s", res.getStatusCode(), res.getError()));
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