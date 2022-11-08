package org.jenkinsci.plugins.torque.steps;

import com.google.common.collect.ImmutableSet;
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

            long startTime = System.currentTimeMillis();
            while ((System.currentTimeMillis()-startTime) < timeoutMinutes*1000*60)
            {
                ResponseData<EnvironmentResponse> responseData = getEnvironment(spaceName, sandboxId);
                if(responseData.isSuccessful())
                {
                    EnvironmentResponse environmentData = responseData.getData();

                    if (!environmentData.details.state.currentState.equals(this.prevStatus)) {
                        prevStatus = environmentData.details.state.currentState;
                    }

                    if(waitForSandbox(environmentData.details))
                        return responseData.getRawBodyJson();
                }
                Thread.sleep(2000);
            }
            throw new TimeoutException(String.format(Messages.WaitingForSandboxTimeoutError(),timeoutMinutes));
        }

        private ResponseData<EnvironmentResponse> getEnvironment(String spaceName, String sandboxId) throws IOException {
            ResponseData<EnvironmentResponse> sandboxByIdRes= environmentAPIService.getEnvironmentById(spaceName, sandboxId);
            if (!sandboxByIdRes.isSuccessful()){
                for(int i=0; i<5; i++){
                    sandboxByIdRes= environmentAPIService.getEnvironmentById(spaceName, sandboxId);
                    if (sandboxByIdRes.isSuccessful()){
                        return sandboxByIdRes;
                    }
                }
                throw new AbortException(String.format("failed after 5 retries. status_code: %s error: %s", sandboxByIdRes.getStatusCode(), sandboxByIdRes.getError()));
            }
            return sandboxByIdRes;

        }

        private boolean waitForSandbox(EnvironmentDetailsResponse details) throws IOException {
            if(details.computedStatus.equals(SandboxStatus.LAUNCHING))
                return false;
            if(details.computedStatus.equals(SandboxStatus.ACTIVE))
                return true;
            if(details.computedStatus.equals(SandboxStatus.ACTIVE_WITH_ERROR)) {
                String grain_statuses_str = formatGrainsDeploymentStatuses(details);
                throw new AbortException(Messages.SandboxDeploymentFailedError(details.computedStatus, grain_statuses_str));
            }

            throw new AbortException(Messages.UnknownSandboxStatusError(details.id, details.computedStatus));
        }

        private String formatGrainsDeploymentStatuses(EnvironmentDetailsResponse environment)throws IOException{
            StringBuilder builder = new StringBuilder();
            boolean isFirst = true;
            for (EnvironmentGrainResponse grain:environment.state.getGrains()) {
                if (isFirst)
                    isFirst = false;
                else
                    builder.append(", ");

                builder.append(String.format("%s: %s", grain.getName(), grain.getState()));
            }

            if (!environment.state.getErrors().isEmpty()) {
                builder.append(System.getProperty("line.separator"));
                builder.append("Environment Errors: ");
                for (EnvironmentErrorResponse error : environment.state.getErrors()) {
                    if (isFirst)
                        isFirst = false;
                    else
                        builder.append(", ");

                    builder.append(String.format("%s", error.getMessage()));
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