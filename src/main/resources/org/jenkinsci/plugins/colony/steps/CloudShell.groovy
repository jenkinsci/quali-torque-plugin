package org.jenkinsci.plugins.colony.steps

import com.google.gson.Gson
import net.sf.json.JSONObject
import net.sf.json.JSONSerializer
import org.jenkinsci.plugins.workflow.cps.CpsScript

class CloudShell implements Serializable {

    private CpsScript script

    CloudShell(CpsScript script) {
        this.script = script
    }

    Blueprint blueprint(String blueprint,String sandboxName, Map<String, String> release, Integer timeout){
        return new Blueprint(this, blueprint, sandboxName, release, timeout)
    }

    def endSandbox(String sandboxId){
        script.endSandbox sandboxId
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

    def static class Blueprint implements Serializable {
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

        Object startSandbox(){
            def sandboxJSONObject
            cs.node {
                def sandboxId = cs.script.startSandbox(blueprint: blueprint, sandboxName:sandboxName, release: release)
                cs.script.echo("health check - waiting for sandbox ${sandboxId} to become ready for testing...")
                String sandboxString = cs.script.waitForSandbox(sandboxId: sandboxId, timeout: timeout)
                cs.script.echo("health check done! returned:${sandboxString}")
                sandboxJSONObject = new JSONObject(sandboxString)//JSONSerializer.toJSON(sandboxString)
            }
            return sandboxJSONObject
        }

        def <V> V doInsideSandbox(Closure<V> body) {
            cs.node {
                cs.script.echo(blueprint)
                cs.script.echo(new Gson().toJson(release))
                def sandboxId = cs.script.startSandbox(blueprint: blueprint, sandboxName:sandboxName, release: release)
                try {
                    cs.script.echo("health check - waiting for sandbox ${sandboxId} to become ready for testing...")
                    String sandboxString = cs.script.waitForSandbox(sandboxId: sandboxId, timeout: timeout)
                    cs.script.echo("health check done! returned:${sandboxString}")
                    def sandboxJSONObject = new JSONObject(sandboxString)//JSONSerializer.toJSON(sandboxString)
                    body.call(sandboxJSONObject)
                }
                finally {
                    cs.script.endSandbox sandboxId
                }
            }
        }
    }
}