package org.jenkinsci.plugins.cs18.steps;

import org.jenkinsci.plugins.workflow.steps.Step;
import org.kohsuke.stapler.DataBoundSetter;

import java.util.Map;

public abstract class AbstractStartSandboxStepImpl extends Step
{
    private final String blueprint;
    private Map<String, String> release;

    public AbstractStartSandboxStepImpl(String blueprint, Map<String, String> release)
    {
        this.blueprint = blueprint;
        this.release = release;
    }

    public String getBlueprint() {
        return this.blueprint;
    }

    public Map<String, String> getRelease() {
        return this.release;
    }

    @DataBoundSetter
    public void setRelease(Map<String, String> release) {
        this.release = release;
    }
}
