package org.jenkinsci.plugins.cs18.steps;

import org.jenkinsci.plugins.workflow.steps.Step;
import org.kohsuke.stapler.DataBoundSetter;

public abstract class AbstractStartSandboxStepImpl extends Step
{
    private final String blueprint;
    private String stage;
    private String serviceNameForHealthCheck;

    public AbstractStartSandboxStepImpl(String blueprint)
    {
        this.blueprint = blueprint;
    }

    public String getBlueprint() {
        return blueprint;
    }

    public String getStage() {
        return stage;
    }

    public String getServiceNameForHealthCheck(){
        return serviceNameForHealthCheck;
    }

    @DataBoundSetter
    public void setServiceNameForHealthCheck(String serviceNameForHealthCheck) {
        this.serviceNameForHealthCheck = serviceNameForHealthCheck;
    }

    @DataBoundSetter
    public void setStage(String stage) {
        this.stage = stage;
    }

}
