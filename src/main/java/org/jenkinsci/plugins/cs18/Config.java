package org.jenkinsci.plugins.cs18;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.cs18.service.SandboxAPIService;
import org.jenkinsci.plugins.cs18.service.SandboxAPIServiceImpl;
import org.jenkinsci.plugins.cs18.service.SandboxAPIServiceMock;
import org.jenkinsci.plugins.cs18.service.SandboxServiceConnection;
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

    public static SandboxAPIService CreateSandboxAPIService() throws Exception {
        //return new SandboxAPIServiceMock();
        return new SandboxAPIServiceImpl(DESCRIPTOR.getAPIConnection());
    }

    public static final class DescriptorImpl extends Descriptor<Config> {
        private SandboxServiceConnection apiConnection;

        public DescriptorImpl() {
            super(Config.class);
            load();
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException
        {
            apiConnection = new SandboxServiceConnection(json.getString("address"),json.getInt("port"), 10, 30);
            save();
            return super.configure(req,json);
        }

        public SandboxServiceConnection getAPIConnection() throws Exception {
            if(apiConnection == null)
            {
                throw new Exception(Messages.APIConnectionNotConfigured());
            }
            return apiConnection;
        }

        public String getAddress() {
            return apiConnection.address;
        }

        public int getPort() {
            return apiConnection.port;
        }
    }
}
