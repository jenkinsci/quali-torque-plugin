package org.jenkinsci.plugins.torque.steps;

import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import hudson.AbortException;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.torque.Messages;
import org.jenkinsci.plugins.torque.PluginConstants;
import org.jenkinsci.plugins.torque.SandboxStepExecution;
import org.jenkinsci.plugins.torque.api.*;
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

    private String spaceName;
    private String sandboxId;
    private Integer timeout;

    @DataBoundConstructor
    public WaitForSandboxStep(@Nonnull String spaceName, @Nonnull String sandboxId, @Nonnull Integer timeout)
    {
        this.spaceName = spaceName;
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


    @DataBoundSetter
    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }

    public String getSpaceName() {
        return this.spaceName;
    }


    @Override
    public StepExecution start(StepContext stepContext) throws Exception {
        return new Execution(stepContext, getSpaceName(), getSandboxId(), timeout);
    }

    public static class Execution extends SandboxStepExecution<String> {
        private final String spaceName;
        private final String sandboxId;
        private Integer timeout;
        private String prevStatus = "";
        private String currStatus = "";

        protected Execution(@Nonnull StepContext context, String spaceName, String sandboxId, Integer timeout) throws Exception {
            super(context);
            this.spaceName = spaceName;
            this.sandboxId = sandboxId;
            this.timeout = timeout;
        }

        @Override
        protected String run() throws Exception {
            return waitForSandbox(this.spaceName, this.sandboxId, timeout);
        }

        public String waitForSandbox(String spaceName, String sandboxId, double timeoutMinutes) throws IOException, InterruptedException, TimeoutException {

            SingleSandbox sandboxData = null;
            long startTime = System.currentTimeMillis();
            while ((System.currentTimeMillis()-startTime) < timeoutMinutes*1000*60)
            {
                ResponseData<Object> sandbox = getSandbox(spaceName, sandboxId);
                if(sandbox != null)
                {
                    sandboxData = new Gson().fromJson(sandbox.getRawBodyJson(), SingleSandbox.class);

                    if (!sandboxData.sandboxStatus.equals(this.prevStatus)) {
                        prevStatus = sandboxData.sandboxStatus;
                    }

                    if(waitForSandbox(sandboxData))
                        return sandbox.getRawBodyJson();
                }
                Thread.sleep(2000);
            }
            throw new TimeoutException(String.format(Messages.WaitingForSandboxTimeoutError(),timeoutMinutes));
        }

        private ResponseData<Object> getSandbox(String spaceName, String sandboxId) throws IOException {
            ResponseData<Object> sandboxByIdRes=sandboxAPIService.getSandboxById(spaceName, sandboxId);
            if (!sandboxByIdRes.isSuccessful()){
                for(int i=0; i<5; i++){
                    sandboxByIdRes=sandboxAPIService.getSandboxById(spaceName, sandboxId);
                    if (sandboxByIdRes.isSuccessful()){
                        return sandboxByIdRes;
                    }
                }
                throw new AbortException(String.format("failed after 5 retries. status_code: %s error: %s", sandboxByIdRes.getStatusCode(), sandboxByIdRes.getError()));
            }
            return sandboxByIdRes;

        }

        private boolean waitForSandbox(SingleSandbox sandbox) throws IOException {
            if(sandbox.sandboxStatus.equals(SandboxStatus.LAUNCHING))
                return false;
            if(sandbox.sandboxStatus.equals(SandboxStatus.ACTIVE))
                return true;
            if(sandbox.sandboxStatus.equals(SandboxStatus.ACTIVE_WITH_ERROR)) {
                String app_statuses_str = formatAppsDeploymentStatuses(sandbox);
                throw new AbortException(Messages.SandboxDeploymentFailedError(sandbox.sandboxStatus, app_statuses_str));
            }

            throw new AbortException(Messages.UnknownSandboxStatusError(sandbox.id, sandbox.sandboxStatus));
        }

        private String formatAppsDeploymentStatuses(SingleSandbox sandbox)throws IOException{
            StringBuilder builder = new StringBuilder();
            boolean isFirst = true;
            for (Service service:sandbox.applications){
                if (isFirst)
                    isFirst= false;
                else
                    builder.append(", ");

                builder.append(String.format("%s: %s", service.name, service.status));
            }

            if (!sandbox.sandboxErrors.isEmpty()) {
                builder.append(System.getProperty("line.separator"));
                builder.append("Sandbox Errors: ");
                for (SandboxErrorService service : sandbox.sandboxErrors) {
                    if (isFirst)
                        isFirst = false;
                    else
                        builder.append(", ");

                    builder.append(String.format("%s: %s", service.time, service.code, service.message));
                }
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