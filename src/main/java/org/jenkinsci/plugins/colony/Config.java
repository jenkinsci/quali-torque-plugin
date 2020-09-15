package org.jenkinsci.plugins.colony;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.colony.service.SandboxAPIService;
import org.jenkinsci.plugins.colony.service.SandboxAPIServiceImpl;
import org.jenkinsci.plugins.colony.service.SandboxServiceConnection;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.DataBoundSetter;

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
        SandboxServiceConnection apiConnection = new SandboxServiceConnection(
            DESCRIPTOR.getAddress(), DESCRIPTOR.getToken(), 10, 30);
//        return new SandboxAPIServiceMock(); //TODO: change back to real impl
        return new SandboxAPIServiceImpl(apiConnection);
    }

    @Symbol("colony")
    public static final class DescriptorImpl extends Descriptor<Config> {
        private String address;
        private String token;

        public DescriptorImpl() {
            super(Config.class);
            load();
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException
        {
            req.bindJSON(this, json);
            save();
            return true;
        }

        public String getAddress() {
            return address;
        }

        public String getToken() {
            return token;
        }

        @DataBoundSetter
        public void setAddress(String address) {
            this.address = address;
            save();
        }

        @DataBoundSetter
        public void setToken(String token) {
            this.token = token;
            save();
        }
    }
}
