package org.jenkinsci.plugins.colony.steps

import com.google.gson.Gson
import net.sf.json.JSONObject
import org.jenkinsci.plugins.workflow.cps.CpsScript

class CloudShell implements Serializable {

    private CpsScript script

    CloudShell(CpsScript script) {
        this.script = script
    }

    Blueprint blueprint(String spaceName, String blueprint,String sandboxName, Map<String, String> artifacts, Integer timeout, Map<String, String> inputs = [:]){
        return new Blueprint(this, spaceName, blueprint, sandboxName, artifacts, timeout,inputs)
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
        private final Map<String, String> artifacts
        private final Map<String, String> inputs
        private String sandboxName
        private int timeout
        private String spaceName

        private Blueprint(CloudShell cs, String spaceName, String blueprint, String sandboxName, Map<String, String> artifacts, Integer timeout, Map<String, String> inputs = [:]) {
            this.spaceName = spaceName
            this.timeout = timeout
            this.sandboxName = sandboxName
            this.blueprint = blueprint
            this.cs = cs
            this.artifacts = artifacts
            this.inputs = inputs
        }

        Object startSandbox(){
            def sandboxJSONObject
            cs.node {
                def sandboxId = cs.script.startSandbox(spaceName: spaceName, blueprint: blueprint, sandboxName:sandboxName, artifacts: artifacts, inputs: inputs)
                cs.script.echo("health check - waiting for sandbox ${sandboxId} to become ready for testing...")
                String sandboxString = cs.script.waitForSandbox(spaceName:spaceName, sandboxId: sandboxId, timeout: timeout)
                cs.script.echo("health check done! returned:${sandboxString}")
                sandboxJSONObject = JSONObject.fromObject(sandboxString)//JSONSerializer.toJSON(sandboxString)
            }
            return sandboxJSONObject
        }

        def <V> V doInsideSandbox(boolean endSandboxOnFail=true, Closure<V> body) {
            cs.node {
                cs.script.echo(blueprint)
                cs.script.echo(new Gson().toJson(artifacts))
                cs.script.echo(spaceName)
                def sandboxId = cs.script.startSandbox(spaceName: spaceName, blueprint: blueprint, sandboxName:sandboxName, artifacts: artifacts, inputs: inputs)
                def sandbox_status=""
                try {
                    cs.script.echo("health check - waiting for sandbox ${sandboxId} to become ready for testing...")
                    String sandboxString = cs.script.waitForSandbox(spaceName:spaceName, sandboxId: sandboxId, timeout: timeout)
                    cs.script.echo("health check done! returned:${sandboxString}")
                    def sandboxJSONObject = JSONObject.fromObject(sandboxString)//JSONSerializer.toJSON(sandboxString)
                    sandbox_status = sandboxJSONObject.sandbox_status
                    body.call(sandboxJSONObject)
                }
                finally {
                    if(sandbox_status == "Active" || endSandboxOnFail)
                        cs.script.endSandbox(spaceName:spaceName, sandboxId:sandboxId)
                }
            }
        }
    }
}