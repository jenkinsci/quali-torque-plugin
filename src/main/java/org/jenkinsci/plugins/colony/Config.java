package org.jenkinsci.plugins.colony;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.colony.service.SandboxAPIService;
import org.jenkinsci.plugins.colony.service.SandboxAPIServiceImpl;
import org.jenkinsci.plugins.colony.service.SandboxAPIServiceMock;
import org.jenkinsci.plugins.colony.service.SandboxServiceConnection;
import org.jenkinsci.plugins.scriptsecurity.sandbox.whitelists.ProxyWhitelist;
import org.jenkinsci.plugins.scriptsecurity.sandbox.whitelists.StaticWhitelist;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;

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
        return new SandboxAPIServiceMock(); //TODO: change back to real impl
//        return new SandboxAPIServiceImpl(DESCRIPTOR.getAPIConnection());
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