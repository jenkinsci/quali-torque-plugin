package org.jenkinsci.plugins.torque;

import org.jenkinsci.plugins.torque.service.EnvironmentAPIService;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;

import javax.annotation.Nonnull;

/**
 * Created by shay-k on 21/06/2017.
 */
public abstract class SandboxStepExecution<T> extends SynchronousNonBlockingStepExecution<T> {

    protected transient EnvironmentAPIService environmentAPIService = null;

    protected SandboxStepExecution(@Nonnull StepContext context) throws Exception {
        super(context);
        environmentAPIService = Config.CreateSandboxAPIService();
    }

    @Override
    protected abstract  T run() throws Exception;
}
