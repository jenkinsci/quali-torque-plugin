package org.jenkinsci.plugins.cloudshell.builders;


import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import org.jenkinsci.plugins.cloudshell.Config;
import org.jenkinsci.plugins.cloudshell.service.SandboxAPIService;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

/**
 * Created by shay-k on 22/06/2017.
 */
public class DeleteSandboxRecorder extends Recorder {

    @DataBoundConstructor
    public DeleteSandboxRecorder(){}

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public BuildStepDescriptor getDescriptor() {
        return (Descriptor) super.getDescriptor();
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        try {
            SandboxAPIService sandboxAPIService = Config.CreateSandboxAPIService();
            CreateSandboxAction createSandboxAction = build.getAction(CreateSandboxAction.class);

            for (String sandboxId:createSandboxAction.getSandboxIds()){
                sandboxAPIService.deleteSandbox(sandboxId);
                createSandboxAction.removeSandboxId(sandboxId);
            }
            return true;
        } catch (Exception e) {
            build.setResult(Result.FAILURE);
            e.printStackTrace(listener.getLogger());
            return false;
        }
    }

    //@Extension
    public static class Descriptor extends BuildStepDescriptor<Publisher> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Delete Sandboxes";
        }
    }

}
