package org.jenkinsci.plugins.colony.steps;

import com.google.common.collect.ImmutableSet;
import hudson.AbortException;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.colony.Messages;
import org.jenkinsci.plugins.colony.PluginConstants;
import org.jenkinsci.plugins.colony.SandboxStepExecution;
import org.jenkinsci.plugins.colony.api.*;
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

    public static class Execution extends SandboxStepExecution<String> {
        private final String sandboxId;
        private Integer timeout;

        protected Execution(@Nonnull StepContext context, String sandboxId, Integer timeout) throws Exception {
            super(context);
            this.sandboxId = sandboxId;
            this.timeout = timeout;
        }

        @Override
        protected String run() throws Exception {
            return waitForSandbox(sandboxId, timeout);
        }

        public String waitForSandbox(String sandboxId, double timeoutMinutes) throws IOException, InterruptedException, TimeoutException {

            long startTime = System.currentTimeMillis();
            while ((System.currentTimeMillis()-startTime) < timeoutMinutes*1000*60)
            {
                ResponseData<SingleSandbox> sandbox = getSandbox(sandboxId);
                if(sandbox != null)
                {
                    if(waitForSandbox(sandbox.getData()))
                        return sandbox.getRawBodyJson();
                }
                Thread.sleep(2000);
            }
            throw new TimeoutException(String.format(Messages.WaitingForSandboxTimeoutError(),timeoutMinutes));
        }

        private ResponseData<SingleSandbox> getSandbox(String sandboxId) throws IOException {

            ResponseData<SingleSandbox> sandboxByIdRes=sandboxAPIService.getSandboxById(sandboxId);
            if (!sandboxByIdRes.isSuccessful()){
                throw new AbortException(sandboxByIdRes.getError());
            }
            return sandboxByIdRes;

        }

        private boolean waitForSandbox(SingleSandbox sandbox) throws IOException {
            if(sandbox.sandboxStatus.equals(SandboxDeploymentStatus.PREPARING) ||
                    sandbox.sandboxStatus.equals(SandboxDeploymentStatus.DEPLOYING))
                return false;
            if(sandbox.sandboxStatus.equals(SandboxDeploymentStatus.DONE))
                return true;
            if(sandbox.sandboxStatus.equals(SandboxDeploymentStatus.ERROR) ||
                    sandbox.sandboxStatus.equals(SandboxDeploymentStatus.ABORTED)) {
                String app_statuses_str = formatAppsDeploymentStatuses(sandbox);
                throw new AbortException(Messages.SandboxDeploymentFailedError(sandbox.sandboxStatus, app_statuses_str));
            }

            throw new AbortException(Messages.UnknownSandboxDeploymentStatusError(sandbox.id, sandbox.sandboxStatus));
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