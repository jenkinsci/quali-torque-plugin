package org.jenkinsci.plugins.cs18;

import hudson.Extension;
import hudson.model.Job;
import hudson.model.RootAction;
import hudson.model.Run;
import hudson.model.View;
import hudson.util.RunList;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.cs18.api.Sandbox;
import org.kohsuke.stapler.Stapler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shay-k on 28/06/2017.
 */
//@Extension
public class CS18Report implements RootAction {
    @Override
    public String getIconFileName() {
        return "/plugin/cs18/icons/cs18-24x24.png";
    }

    @Override
    public String getDisplayName() {
        return Messages.CS18Report_DisplayName();
    }

    @Override
    public String getUrlName() {
        return "/cs18";
    }
    public View getOwner() {
        View view = Stapler.getCurrentRequest().findAncestorObject(View.class);
        if (view != null) {
            return view;
        } else {
            return Jenkins.getInstance().getStaplerFallback();
        }
    }
    public List<Sandbox> getSandboxes() {
//        List<Run> lastBuilds = new ArrayList<Run>();
//        for (Job item : Jenkins.getInstance().getAllItems(Job.class)) {
//            Job job = (Job) item;
//            Run lb = job.getLastBuild();
//            while (lb != null && (lb.hasntStartedYet() || lb.isBuilding()))
//                lb = lb.getPreviousBuild();
//
////            if (lb != null && lb.getAction(ClaimBuildAction.class) != null) {
////                lastBuilds.add(lb);
////            }
//        }

        return new ArrayList<Sandbox>();
    }

}
