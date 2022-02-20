package org.jenkinsci.plugins.torque.steps;

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
        return "torque";
    }

    @Override public Object getValue(CpsScript script) throws Exception {
        Binding binding = script.getBinding();
        Object torque;
        if (binding.hasVariable(getName())) {
            torque = binding.getVariable(getName());
        } else {
            // Note that if this were a method rather than a constructor, we would need to mark it @NonCPS lest it throw CpsCallableInvocation.
            torque = script.getClass().getClassLoader().loadClass("org.jenkinsci.plugins.torque.steps.CloudShell").getConstructor(CpsScript.class).newInstance(script);
            binding.setVariable(getName(), torque);
        }
        return torque;
    }

}