package org.jenkinsci.plugins.torque.steps

import com.google.gson.Gson
import net.sf.json.JSONObject
import org.jenkinsci.plugins.workflow.cps.CpsScript

class Torque implements Serializable {

    private CpsScript script

    Torque(CpsScript script) {
        this.script = script
    }

    Blueprint blueprint(String spaceName, String blueprint, String sandboxName, String duration, Map<String, String> artifacts, Map<String, String> inputs, Integer timeout) {
        return new Blueprint(this, spaceName, blueprint, sandboxName, duration, artifacts, inputs, timeout)
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
        public final Torque cs
        private final String blueprint
        private final Map<String, String> artifacts
        private final Map<String, String> inputs
        private String sandboxName
        private String duration
        private int timeout
        private String spaceName

        private Blueprint(Torque cs, String spaceName, String blueprint, String sandboxName, String duration, Map<String, String> artifacts, Map<String, String> inputs, Integer timeout) {
            this.spaceName = spaceName
            this.timeout = timeout
            this.sandboxName = sandboxName
            this.blueprint = blueprint
            this.duration = duration
            this.cs = cs
            this.artifacts = artifacts
            this.inputs = inputs
        }

        Object startSandbox(boolean endSandboxOnFail = true) {
            def sandboxJSONObject = null
            def sandboxId
            cs.node {
                try {
                    sandboxId = cs.script.startSandbox(spaceName: spaceName, blueprint: blueprint, sandboxName: sandboxName, duration: duration, artifacts: artifacts, inputs: inputs)
                    cs.script.echo("health check - waiting for environment ${sandboxId} to become ready for testing...")
                    String sandboxString = cs.script.waitForSandbox(spaceName: spaceName, sandboxId: sandboxId, timeout: timeout)
                    cs.script.echo("health check done! returned:${sandboxString}")
                    sandboxJSONObject = JSONObject.fromObject(sandboxString)//JSONSerializer.toJSON(sandboxString)
                }
                catch (Exception ex) {
                    cs.script.echo("** Exception thrown during environment creation **")
                    if (sandboxId != null) {
                        cs.script.echo("Environment id:${sandboxId}")
                        if (endSandboxOnFail) {
                            cs.script.echo("End environment:${sandboxId}")
                            cs.script.endSandbox(spaceName: spaceName, sandboxId: sandboxId)
                        } else {
                            cs.script.echo("Keeping environment:${sandboxId} up.")
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
                cs.script.echo(new Gson().toJson(artifacts))
                cs.script.echo(spaceName)
                def sandboxId = cs.script.startSandbox(spaceName: spaceName, blueprint: blueprint, sandboxName: sandboxName, artifacts: artifacts, inputs: inputs)
                def sandbox_status = ""
                try {
                    cs.script.echo("health check - waiting for environment ${sandboxId} to become ready for testing...")
                    String sandboxString = cs.script.waitForSandbox(spaceName: spaceName, sandboxId: sandboxId, timeout: timeout)
                    cs.script.echo("health check done! returned:${sandboxString}")
                    def sandboxJSONObject = JSONObject.fromObject(sandboxString)//JSONSerializer.toJSON(sandboxString)
                    sandbox_status = sandboxJSONObject.details.state.currentState
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