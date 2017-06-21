package org.jenkinsci.plugins.cloudshell;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.cloudshell.service.SandboxServiceConnection;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Created by shay-k on 20/06/2017.
 */
public class Config extends AbstractDescribableImpl<Config> {

    @Override
    public Config.DescriptorImpl getDescriptor() {
        return DESCRIPTOR;
    }

    @Extension
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();


    public static final class DescriptorImpl extends Descriptor<Config> {
        private SandboxServiceConnection cloudshellConnection;

        public DescriptorImpl() {
            super(Config.class);
            load();
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException
        {
            cloudshellConnection = new SandboxServiceConnection(json.getString("address"),json.getInt("port"), 10);
            save();
            return super.configure(req,json);
        }

        public SandboxServiceConnection getCloudShellConnection() throws Exception {
            if(cloudshellConnection == null)
            {
                throw new Exception("CloudShell Connection was not set");
            }
            return cloudshellConnection;
        }

        public String getAddress() {
            return cloudshellConnection.address;
        }

        public int getPort() {
            return cloudshellConnection.port;
        }
    }
}
