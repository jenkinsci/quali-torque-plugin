package org.jenkinsci.plugins.colony.steps

import com.google.gson.Gson
import groovy.json.JsonSlurper
import net.sf.json.JSON
import net.sf.json.JSONObject
import org.jenkinsci.plugins.workflow.cps.CpsScript

class CloudShell implements Serializable {

    private CpsScript script

    public CloudShell(CpsScript script) {
        this.script = script
    }

    public Blueprint blueprint(String blueprint,String sandboxName, Map<String, String> release, Integer timeout){
        return new Blueprint(this, blueprint, sandboxName, release, timeout)
    }

    private <V> V node(Closure<V> body) {
        if (script.env.NODE_NAME != null) {
            // Already inside a node block.
            body()
        } else {
            script.node {
                body()
            }
        }
    }

    public static class Blueprint implements Serializable {
        public final CloudShell cs
        private final String blueprint
        private final Map<String, String> release
        private String sandboxName
        private int timeout

        private Blueprint(CloudShell cs, String blueprint, String sandboxName, Map<String, String> release, Integer timeout) {
            this.timeout = timeout
            this.sandboxName = sandboxName
            this.blueprint = blueprint
            this.cs = cs
            this.release = release
        }

        public Sandbox startSandbox(){
            def sandbox
            cs.node {
                def sandboxId = cs.script.startSandbox(blueprint: blueprint, sandboxName:sandboxName, release: release)
                cs.script.echo("health check - waiting for sandbox ${sandboxId} to become ready for testing...")
                sandbox = cs.script.waitForSandbox(sandboxId: sandboxId, timeout: timeout)
                cs.script.echo("health check done!")
                def sandboxJson = JSONObject.fromObject(sandbox).toString()
                cs.script.echo("sandbox under test details:${sandboxJson}")
            }
            return new Sandbox(this.cs,sandbox)
        }

        public <V> V doInsideSandbox(Closure<V> body) {
            cs.node {
                cs.script.echo(blueprint)
                cs.script.echo(new Gson().toJson(release))
                def sandboxId = cs.script.startSandbox(blueprint: blueprint, sandboxName:sandboxName, release: release)
                try {
                    cs.script.echo("health check - waiting for sandbox ${sandboxId} to become ready for testing...")
                    def sandbox = cs.script.waitForSandbox(sandboxId: sandboxId, timeout: timeout)
                    cs.script.echo("health check done!")
                    def sandboxJson =JSONObject.fromObject(sandbox).toString()
                    def sandboxJSONObject = new JsonSlurper().parseText(sandboxJson)
                    cs.script.echo("sandboxJSONObject:${sandboxJSONObject}")
                    body.call(sandboxJSONObject)
                }
                finally {
                    cs.script.endSandbox sandboxId
                }
            }
        }
    }

    public static class Sandbox implements Serializable {
        public final CloudShell cs
        private final org.jenkinsci.plugins.colony.api.SingleSandbox sandbox;

        private Sandbox(CloudShell cs, org.jenkinsci.plugins.colony.api.SingleSandbox sandbox) {
            this.sandbox = sandbox
            this.cs = cs
        }
        public org.jenkinsci.plugins.colony.api.SingleSandbox getData(){
            def sandboxJson =JSONObject.fromObject(this.sandbox).toString()
            return new JsonSlurper().parseText(sandboxJson)
        }
        public void end(){
            cs.script.endSandbox this.sandbox.id
        }
    }

}