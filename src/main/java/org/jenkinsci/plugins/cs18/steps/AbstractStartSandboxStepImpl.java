package org.jenkinsci.plugins.cs18.steps;

import org.jenkinsci.plugins.workflow.steps.Step;
import org.kohsuke.stapler.DataBoundSetter;

public abstract class AbstractStartSandboxStepImpl extends Step
{
    private final String blueprint;
    private String stage;
    private String serviceNameForHealthCheck;
    private String branch;
    private String changeset;

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

    public String getBranch() {
        return branch;
    }

    @DataBoundSetter
    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getChangeset() {
        return changeset;
    }

    @DataBoundSetter
    public void setChangeset(String changeset) {
        this.changeset = changeset;
    }
}
