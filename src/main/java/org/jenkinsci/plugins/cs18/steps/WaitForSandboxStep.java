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

    private String serviceNameForHealthCheck;
    private String sandboxId;

    @DataBoundConstructor
    public WaitForSandboxStep(@Nonnull String sandboxId, String serviceNameForHealthCheck)
    {
        this.sandboxId = sandboxId;
        this.serviceNameForHealthCheck = serviceNameForHealthCheck;
    }

    @DataBoundSetter
    public void setSandboxId(String sandboxId) {
        this.sandboxId = sandboxId;
    }

    public String getSandboxId() {
        return this.sandboxId;
    }

    @DataBoundSetter
    public void setServiceNameForHealthCheck(String serviceNameForHealthCheck) {
        this.serviceNameForHealthCheck = serviceNameForHealthCheck;
    }

    public String getServiceNameForHealthCheck() {
        return this.serviceNameForHealthCheck;
    }


    @Override
    public StepExecution start(StepContext stepContext) throws Exception {
        return new Execution(stepContext, getSandboxId(), getServiceNameForHealthCheck());
    }

    public static class Execution extends SandboxStepExecution<Sandbox> {
        private final String sandboxId;
        private final String serviceNameForHealthCheck;

        protected Execution(@Nonnull StepContext context, String sandboxId, String serviceNameForHealthCheck) throws Exception {
            super(context);
            this.sandboxId = sandboxId;
            this.serviceNameForHealthCheck = serviceNameForHealthCheck;
        }


        @Override
        protected Sandbox run() throws Exception {
            return waitForSandbox(sandboxId,this.serviceNameForHealthCheck,8);
        }

        public Sandbox waitForSandbox(String sandboxId, String serviceNameForHealthCheck,double timeoutMinutes) throws IOException, InterruptedException, TimeoutException {

            long startTime = System.currentTimeMillis();
            while ((System.currentTimeMillis()-startTime) < timeoutMinutes*1000*60)
            {
                Sandbox sandbox = getSandbox(sandboxId);
                if(sandbox != null)
                {
                    Service service = getService(sandbox, serviceNameForHealthCheck);
                    if(service != null)
                    {
                        if(waitForSandbox(service))
                            return sandbox;
                    }
                }
                Thread.sleep(2000);
            }
            throw new TimeoutException(String.format(Messages.WaitingForSandboxTimeoutError(),timeoutMinutes));
        }

        private Sandbox getSandbox(String sandboxId) throws IOException {
            ResponseData<Sandbox[]> sandboxesRes = sandboxAPIService.getSandboxes();
            if(!sandboxesRes.isSuccessful()) {
                throw new AbortException(sandboxesRes.getError());
            }
            for(Sandbox sandbox :sandboxesRes.getData()){
                if (sandbox.id.equals(sandboxId)){

                    return sandbox;
                }
            }
            throw new AbortException(String.format(Messages.SandboxNotExistsError(),sandboxId));
        }

        private boolean waitForSandbox(Service service) throws IOException {
            if(service.status == null || service.status.equals("") ||service.status.equals(ServiceStatus.PENDING))
                return false;
            if(service.status.equals(ServiceStatus.COMPLETED))
                return true;
            if(service.status.equals(ServiceStatus.ERROR))
                throw new AbortException(Messages.ServiceStateError(service.name));

            throw new AbortException(Messages.UnknownServiceStatusError(service.name,service.status));
        }
        private Service getService(Sandbox sandbox, String serviceName) {
            for(Service service:sandbox.services)
            {
                if(service.name.equals(serviceName))
                    return service;
            }
            return null;
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