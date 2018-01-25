package org.jenkinsci.plugins.colony.steps

import com.google.gson.Gson
import net.sf.json.JSONObject
import org.jenkinsci.plugins.workflow.cps.CpsScript

class CloudShell implements Serializable {

    private CpsScript script

    CloudShell(CpsScript script) {
        this.script = script
    }

    Blueprint blueprint(String spaceName, String blueprint,String sandboxName, Map<String, String> release, Integer timeout, Map<String, String> inputs = [:]){
        return new Blueprint(this, spaceName, blueprint, sandboxName, release, timeout,inputs)
    }

    def endSandbox(String spaceName, String sandboxId){
        script.endSandbox(spaceName:spaceName, sandboxId:sandboxId)
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
        private final Map<String, String> inputs
        private String sandboxName
        private int timeout
        private String spaceName

        private Blueprint(CloudShell cs, String spaceName, String blueprint, String sandboxName, Map<String, String> release, Integer timeout, Map<String, String> inputs = [:]) {
            this.spaceName = spaceName
            this.timeout = timeout
            this.sandboxName = sandboxName
            this.blueprint = blueprint
            this.cs = cs
            this.release = release
            this.inputs = inputs
        }

        Object startSandbox(){
            def sandboxJSONObject
            cs.node {
                def sandboxId = cs.script.startSandbox(spaceName: spaceName, blueprint: blueprint, sandboxName:sandboxName, release: release, inputs: inputs)
                cs.script.echo("health check - waiting for sandbox ${sandboxId} to become ready for testing...")
                String sandboxString = cs.script.waitForSandbox(spaceName:spaceName, sandboxId: sandboxId, timeout: timeout)
                cs.script.echo("health check done! returned:${sandboxString}")
                sandboxJSONObject = JSONObject.fromObject(sandboxString)//JSONSerializer.toJSON(sandboxString)
            }
            return sandboxJSONObject
        }

        def <V> V doInsideSandbox(Closure<V> body) {
            cs.node {
                cs.script.echo(blueprint)
                cs.script.echo(new Gson().toJson(release))
                cs.script.echo(spaceName)
                def sandboxId = cs.script.startSandbox(spaceName: spaceName, blueprint: blueprint, sandboxName:sandboxName, release: release, inputs: inputs)
                try {
                    cs.script.echo("health check - waiting for sandbox ${sandboxId} to become ready for testing...")
                    String sandboxString = cs.script.waitForSandbox(spaceName:spaceName, sandboxId: sandboxId, timeout: timeout)
                    cs.script.echo("health check done! returned:${sandboxString}")
                    def sandboxJSONObject = JSONObject.fromObject(sandboxString)//JSONSerializer.toJSON(sandboxString)
                    body.call(sandboxJSONObject)
                }
                finally {
                    cs.script.endSandbox(spaceName:spaceName, sandboxId:sandboxId)
                }
            }
        }
    }
}