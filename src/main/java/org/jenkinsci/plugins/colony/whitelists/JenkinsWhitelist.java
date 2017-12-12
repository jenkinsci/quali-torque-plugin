package org.jenkinsci.plugins.colony.whitelists;

import hudson.Extension;
import java.io.IOException;

import org.jenkinsci.plugins.scriptsecurity.sandbox.whitelists.ProxyWhitelist;
import org.jenkinsci.plugins.scriptsecurity.sandbox.whitelists.StaticWhitelist;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/**
 * Includes entries useful for scripts accessing the Jenkins API, such as model objects.
 */
@Restricted(NoExternalUse.class)
@Extension
public final class JenkinsWhitelist extends ProxyWhitelist {

    public JenkinsWhitelist() throws IOException {
        super(StaticWhitelist.from(JenkinsWhitelist.class.getResource("jenkins-whitelist")));
    }

}