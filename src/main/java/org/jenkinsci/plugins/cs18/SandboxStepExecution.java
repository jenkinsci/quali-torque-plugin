package org.jenkinsci.plugins.cs18;

import org.jenkinsci.plugins.cs18.service.SandboxAPIService;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousStepExecution;

import javax.annotation.Nonnull;

/**
 * Created by shay-k on 21/06/2017.
 */
public abstract class SandboxStepExecution<T> extends SynchronousNonBlockingStepExecution<T> {

    protected transient SandboxAPIService sandboxAPIService = null;

    protected SandboxStepExecution(@Nonnull StepContext context) throws Exception {
        super(context);
        sandboxAPIService = Config.CreateSandboxAPIService();
    }

    @Override
    protected abstract  T run() throws Exception;
}
