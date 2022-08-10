package org.jenkinsci.plugins.torque;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.Secret;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.torque.service.EnvironmentAPIService;
import org.jenkinsci.plugins.torque.service.EnvironmentAPIServiceImpl;
import org.jenkinsci.plugins.torque.service.EnvironmentServiceConnection;
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

    public static EnvironmentAPIService CreateSandboxAPIService() throws Exception {
        EnvironmentServiceConnection apiConnection = new EnvironmentServiceConnection(
            DESCRIPTOR.getAddress(), DESCRIPTOR.getToken(), 10, 30);
//        return new SandboxAPIServiceMock(); //TODO: change back to real impl
        return new EnvironmentAPIServiceImpl(apiConnection);
    }

    @Symbol("torque")
    public static final class DescriptorImpl extends Descriptor<Config> {
        private String address;
        private Secret token;

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

        public Secret getToken() {
            return token;
        }

        @DataBoundSetter
        public void setAddress(String address) {
            this.address = address;
            save();
        }

        @DataBoundSetter
        public void setToken(Secret token) {
            this.token = token;
            save();
        }
    }
}
