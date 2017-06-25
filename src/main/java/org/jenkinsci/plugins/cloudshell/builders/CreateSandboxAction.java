package org.jenkinsci.plugins.cloudshell.builders;

import hudson.model.Action;
import org.jenkinsci.plugins.cloudshell.api.Sandbox;

import java.util.ArrayList;

/**
 * Created by shay-k on 22/06/2017.
 */
public class CreateSandboxAction implements Action {

    private ArrayList<String> sandboxesIds;

    public CreateSandboxAction() {
        sandboxesIds = new ArrayList<>();
    }

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public String getUrlName() {
        return null;
    }
    public void addSandboxId(String sandboxId){
        sandboxesIds.add(sandboxId);
    }

    public String [] getSandboxIds(){
        String [] sandboxeIdArr = new String[sandboxesIds.size()];
        return sandboxesIds.toArray(sandboxeIdArr);
    }

    public void removeSandboxId(String sandboxId){
        sandboxesIds.remove(sandboxId);
    }
}
