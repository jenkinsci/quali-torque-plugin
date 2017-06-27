package org.jenkinsci.plugins.cs18.steps;

import groovy.lang.Binding;
import hudson.Extension;
import org.jenkinsci.plugins.workflow.cps.CpsScript;
import org.jenkinsci.plugins.workflow.cps.GlobalVariable;
/**
 * Created by shay-k on 27/06/2017.
 */
@Extension
public class CloudShellDSL extends GlobalVariable {

    @Override public String getName() {
        return "cs18";
    }

    @Override public Object getValue(CpsScript script) throws Exception {
        Binding binding = script.getBinding();
        Object cs18;
        if (binding.hasVariable(getName())) {
            cs18 = binding.getVariable(getName());
        } else {
            // Note that if this were a method rather than a constructor, we would need to mark it @NonCPS lest it throw CpsCallableInvocation.
            cs18 = script.getClass().getClassLoader().loadClass("org.jenkinsci.plugins.cs18.steps.CloudShell").getConstructor(CpsScript.class).newInstance(script);
            binding.setVariable(getName(), cs18);
        }
        return cs18;
    }

}