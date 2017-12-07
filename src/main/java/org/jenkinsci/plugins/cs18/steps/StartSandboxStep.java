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
import org.kohsuke.stapler.DataBoundSetter;
import org.jenkinsci.plugins.workflow.steps.Step;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;

/**
 * Created by shay-k on 20/06/2017.
 */
public class StartSandboxStep extends Step {

    private final String blueprint;
    private String blueprintName;
    private Map<String, String> release;

    @DataBoundConstructor
    public StartSandboxStep(@Nonnull String blueprint,@Nonnull String blueprintName, @Nonnull Map<String, String> release)
    {
        this.blueprint = blueprint;
        this.blueprintName = blueprintName;
        this.release = release;
    }

    public String getBlueprint() {
        return this.blueprint;
    }

    public Map<String, String> getRelease() {
        return this.release;
    }

    public String getBlueprintName() {
        return this.blueprintName;
    }

    @DataBoundSetter
    public void setRelease(Map<String, String> release) {
        this.release = release;
    }

    @Override
    public StepExecution start(StepContext stepContext) throws Exception {
        return new Execution(stepContext, getBlueprint(),getBlueprintName(), getRelease());
    }

    public static class Execution extends SandboxStepExecution<String> {
        private String sandboxName;
        private final String blueprint;
        private final Map<String, String> release;

        protected Execution(@Nonnull StepContext context, String sandboxName, String blueprint, Map<String, String> release) throws Exception {
            super(context);
            this.sandboxName = sandboxName;
            this.blueprint = blueprint;
            this.release = release;
        }


        @Override
        protected String run() throws Exception {
            TaskListener taskListener = getContext().get(TaskListener.class);
            assert taskListener != null;
            taskListener.getLogger().println(Messages.StartSandbox_StartingMsg());
            String sandboxName = this.sandboxName.isEmpty() ? PluginHelpers.GenerateSandboxName() : this.sandboxName;
            CreateSandboxRequest req = new CreateSandboxRequest(blueprint, sandboxName, release,true);
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