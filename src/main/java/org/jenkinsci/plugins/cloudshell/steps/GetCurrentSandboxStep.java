package org.jenkinsci.plugins.cloudshell.steps;

import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import hudson.EnvVars;
import hudson.Extension;
import hudson.model.Run;
import org.jenkinsci.plugins.cloudshell.PluginConstants;
import org.jenkinsci.plugins.cloudshell.api.Sandbox;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.Set;

/**
 * Created by shay-k on 25/06/2017.
 */
public class GetCurrentSandboxStep extends Step {

    @DataBoundConstructor
    public GetCurrentSandboxStep() {
    }

    @Override
    public StepExecution start(StepContext stepContext) throws Exception {
        return new Execution(stepContext);
    }


    public static class Execution extends SynchronousStepExecution<Sandbox> {

        Execution(StepContext context) {
            super(context);
        }

        @Override protected Sandbox run() throws Exception {
            EnvVars envVars = getContext().get(EnvVars.class);
            String sandboxJson = envVars.get(PluginConstants.SANDBOX_ENVVAR);
            if(sandboxJson == null)
                return null;
            return new Gson().fromJson(sandboxJson, Sandbox.class);
        }

    }

    @Extension
    public static class DescriptorImpl extends StepDescriptor {

        @Override
        public String getFunctionName() {
            return "getSandbox";
        }

        @Override
        public String getDisplayName() {
            return "Get The Current Sandbox";
        }

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return ImmutableSet.of(Run.class,EnvVars.class);
        }
    }
}
