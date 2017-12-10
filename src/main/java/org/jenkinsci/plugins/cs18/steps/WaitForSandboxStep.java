package org.jenkinsci.plugins.cs18.steps;

import com.google.common.collect.ImmutableSet;
import hudson.AbortException;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.cs18.Messages;
import org.jenkinsci.plugins.cs18.PluginConstants;
import org.jenkinsci.plugins.cs18.SandboxStepExecution;
import org.jenkinsci.plugins.cs18.api.*;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeoutException;

/**
 * Created by shay-k on 20/06/2017.
 */
public class WaitForSandboxStep extends Step {

    private String sandboxId;
    private Integer timeout;

    @DataBoundConstructor
    public WaitForSandboxStep(@Nonnull String sandboxId, @Nonnull Integer timeout)
    {
        this.sandboxId = sandboxId;
        this.timeout = timeout;
    }

    @DataBoundSetter
    public void setSandboxId(String sandboxId) {
        this.sandboxId = sandboxId;
    }

    public String getSandboxId() {
        return this.sandboxId;
    }


    @Override
    public StepExecution start(StepContext stepContext) throws Exception {
        return new Execution(stepContext, getSandboxId(), timeout);
    }

    public static class Execution extends SandboxStepExecution<SingleSandbox> {
        private final String sandboxId;
        private Integer timeout;

        protected Execution(@Nonnull StepContext context, String sandboxId, Integer timeout) throws Exception {
            super(context);
            this.sandboxId = sandboxId;
            this.timeout = timeout;
        }

        @Override
        protected SingleSandbox run() throws Exception {
            return waitForSandbox(sandboxId, timeout);
        }

        public SingleSandbox waitForSandbox(String sandboxId, double timeoutMinutes) throws IOException, InterruptedException, TimeoutException {

            long startTime = System.currentTimeMillis();
            while ((System.currentTimeMillis()-startTime) < timeoutMinutes*1000*60)
            {
                SingleSandbox sandbox = getSandbox(sandboxId);
                if(sandbox != null)
                {
                    if(waitForSandbox(sandbox))
                        return sandbox;
                }
                Thread.sleep(2000);
            }
            throw new TimeoutException(String.format(Messages.WaitingForSandboxTimeoutError(),timeoutMinutes));
        }

        private SingleSandbox getSandbox(String sandboxId) throws IOException {

            ResponseData<SingleSandbox> sandboxByIdRes=sandboxAPIService.getSandboxById(sandboxId);
            if (!sandboxByIdRes.isSuccessful()){
                throw new AbortException(sandboxByIdRes.getError());
            }
            return sandboxByIdRes.getData();

        }

        private boolean waitForSandbox(SingleSandbox sandbox) throws IOException {
            if(sandbox.deploymentStatus.equals(SandboxDeploymentStatus.PREPARING) ||
                    sandbox.deploymentStatus.equals(SandboxDeploymentStatus.DEPLOYING))
                return false;
            if(sandbox.deploymentStatus.equals(SandboxDeploymentStatus.DONE))
                return true;
            if(sandbox.deploymentStatus.equals(SandboxDeploymentStatus.ERROR) ||
                    sandbox.deploymentStatus.equals(SandboxDeploymentStatus.ABORTED)) {
                String app_statuses_str = formatAppsDeploymentStatuses(sandbox);
                throw new AbortException(Messages.SandboxDeploymentFailedError(sandbox.deploymentStatus, app_statuses_str));
            }

            throw new AbortException(Messages.UnknownSandboxDeploymentStatusError(sandbox.id, sandbox.deploymentStatus));
        }

        private String formatAppsDeploymentStatuses(SingleSandbox sandbox)throws IOException{
            StringBuilder builder = new StringBuilder();
            boolean isFirst = true;
            for (Service service:sandbox.applications){
                if (isFirst)
                    isFirst= false;
                else
                    builder.append(", ");
                builder.append(String.format("%s: %s", service.name, service.deploymentStatus));
            }
            return builder.toString();
        }
    }

    @Extension
    public static class Descriptor extends StepDescriptor {

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return ImmutableSet.of(Run.class, TaskListener.class);
        }

        @Override public String getFunctionName() {
            return  PluginConstants.WAIT_FOR_SANDBOX_FUNC_NAME;
        }

        @Override public String getDisplayName() {
            return Messages.WaitForSandbox_FuncDisplayName();
        }

    }
}