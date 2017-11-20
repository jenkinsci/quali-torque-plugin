package org.jenkinsci.plugins.cs18.steps

import com.google.gson.Gson
import net.sf.json.JSONObject
import org.jenkinsci.plugins.workflow.cps.CpsScript

class CloudShell implements Serializable {

    private CpsScript script

    public CloudShell(CpsScript script) {
        this.script = script
    }

    public Blueprint blueprint(String blueprint, Map<String, String> release){
        return new Blueprint(this, blueprint, release)
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

        private Blueprint(CloudShell cs, String blueprint, Map<String, String> release) {
            this.blueprint = blueprint
            this.cs = cs
            this.release = release
        }

        public Sandbox startSandbox(){
            def sandbox
            cs.node {
                def sandboxId = cs.script.startSandbox(blueprint: blueprint, release: release)
                cs.script.echo("health check - waiting for sandbox ${sandboxId} to become ready for testing...")
                sandbox = cs.script.waitForSandbox(sandboxId: sandboxId)
                cs.script.echo("health check done!")
                def sandboxJson = JSONObject.fromObject(sandbox).toString()
                cs.script.echo("sandbox under test details:${sandboxJson}")
                cs.script.withEnv(["SANDBOX=${sandboxJson}"]) {}
            }
            return new Sandbox(this.cs,sandbox)
        }

        public <V> V doInsideSandbox(Closure<V> body) {
            cs.node {
                cs.script.echo(blueprint)
                cs.script.echo(new Gson().toJson(release))
                def sandboxId = cs.script.startSandbox(blueprint: blueprint, release: release)
                try {
                    cs.script.echo("health check - waiting for sandbox ${sandboxId} to become ready for testing...")
                    def sandbox = cs.script.waitForSandbox(sandboxId: sandboxId)
                    cs.script.echo("health check done!")
                    def sandboxJson =JSONObject.fromObject(sandbox).toString()
                    cs.script.echo("sandbox under test details:${sandboxJson}")
                    cs.script.withEnv(["SANDBOX=${sandboxJson}"]) {
                        body()
                    }
                }
                finally {
                    cs.script.endSandbox sandboxId
                }
            }
        }
    }

    public static class Sandbox implements Serializable {
        public final CloudShell cs
        private final org.jenkinsci.plugins.cs18.api.SingleSandbox sandbox;

        private Sandbox(CloudShell cs, org.jenkinsci.plugins.cs18.api.SingleSandbox sandbox) {
            this.sandbox = sandbox
            this.cs = cs
        }
        public org.jenkinsci.plugins.cs18.api.SingleSandbox getData(){
            return this.sandbox
        }
        public void end(){
            cs.script.endSandbox this.sandbox.id
        }
    }

}