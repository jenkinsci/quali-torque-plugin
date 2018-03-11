package org.jenkinsci.plugins.colony.steps

import com.google.gson.Gson
import net.sf.json.JSONObject
import org.jenkinsci.plugins.workflow.cps.CpsScript

class CloudShell implements Serializable {

    private CpsScript script

    CloudShell(CpsScript script) {
        this.script = script
    }

    Blueprint blueprint(String spaceName, String blueprint, String sandboxName, Map<String, String> release, Integer timeout, Map<String, String> inputs = [:]) {
        return new Blueprint(this, spaceName, blueprint, sandboxName, release, timeout, inputs)
    }

    def endSandbox(String spaceName, String sandboxId) {
        script.endSandbox(spaceName: spaceName, sandboxId: sandboxId)
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

        Object startSandbox(boolean endSandboxOnFail = true) {
            def sandboxJSONObject
            def sandboxId
            cs.node {
                try {
                    sandboxId = cs.script.startSandbox(spaceName: spaceName, blueprint: blueprint, sandboxName: sandboxName, release: release, inputs: inputs)
                    cs.script.echo("health check - waiting for sandbox ${sandboxId} to become ready for testing...")
                    String sandboxString = cs.script.waitForSandbox(spaceName: spaceName, sandboxId: sandboxId, timeout: timeout)
                    cs.script.echo("health check done! returned:${sandboxString}")
                    sandboxJSONObject = JSONObject.fromObject(sandboxString)//JSONSerializer.toJSON(sandboxString)
                }
                catch (Exception ex) {
                    cs.script.echo("** Exception thrown during sandbox creation **")
                    if (sandboxId != null) {
                        cs.script.echo("Sandbox id:${sandboxId}")
                        if (endSandboxOnFail) {
                            cs.script.echo("End sandbox:${sandboxId}")
                            cs.script.endSandbox(spaceName: spaceName, sandboxId: sandboxId)
                        } else {
                            cs.script.echo("Keeping sandbox:${sandboxId} up.")
                        }
                    }
                    throw ex
                }
            }
            return sandboxJSONObject
        }

        def <V> V doInsideSandbox(boolean endSandboxOnFail = true, Closure<V> body) {
            cs.node {
                cs.script.echo(blueprint)
                cs.script.echo(new Gson().toJson(release))
                cs.script.echo(spaceName)
                def sandboxId = cs.script.startSandbox(spaceName: spaceName, blueprint: blueprint, sandboxName: sandboxName, release: release, inputs: inputs)
                def sandbox_status = ""
                try {
                    cs.script.echo("health check - waiting for sandbox ${sandboxId} to become ready for testing...")
                    String sandboxString = cs.script.waitForSandbox(spaceName: spaceName, sandboxId: sandboxId, timeout: timeout)
                    cs.script.echo("health check done! returned:${sandboxString}")
                    def sandboxJSONObject = JSONObject.fromObject(sandboxString)//JSONSerializer.toJSON(sandboxString)
                    sandbox_status = sandboxJSONObject.sandbox_status
                    body.call(sandboxJSONObject)
                }
                finally {
                    if (sandbox_status == "Active" || endSandboxOnFail)
                        cs.script.endSandbox(spaceName: spaceName, sandboxId: sandboxId)
                }
            }
        }
    }
}