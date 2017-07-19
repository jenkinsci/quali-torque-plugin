package org.jenkinsci.plugins.cs18.steps

import net.sf.json.JSONObject

class CloudShell implements Serializable {

    private org.jenkinsci.plugins.workflow.cps.CpsScript script

    public CloudShell(org.jenkinsci.plugins.workflow.cps.CpsScript script) {
        this.script = script
    }

    public Blueprint blueprint(String blueprint, String stage = null, String serviceNameForHealthCheck = null, String branch = null, String changeset = null){
        return new Blueprint(this,blueprint,stage,serviceNameForHealthCheck, branch, changeset)
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
        public final CloudShell cs;
        private final String blueprint
        private final String stage
        private final String serviceNameForHealthCheck
        private final String branch;
        private final String changeset;

        private Blueprint(CloudShell cs, String blueprint, String stage = null, String serviceNameForHealthCheck = null,String branch = null,String changeset = null) {
            this.serviceNameForHealthCheck = serviceNameForHealthCheck
            this.stage = stage
            this.blueprint = blueprint
            this.cs = cs
            this.branch = branch
            this.changeset = changeset
        }

        public Sandbox startSandbox(){
            def sandbox
            cs.node {
                def sandboxId = cs.script.startSandbox(blueprint: blueprint, stage: stage, branch:branch, changeset:changeset)
                cs.script.echo("health check - waiting for sandbox ${sandboxId} to become ready for testing...")
                sandbox = cs.script.waitForSandbox(sandboxId: sandboxId,serviceNameForHealthCheck:serviceNameForHealthCheck)
                cs.script.echo("health check done!")
                def sandboxJson = JSONObject.fromObject(sandbox).toString()
                cs.script.echo("sandbox under test details:${sandboxJson}")
            }
            return new Sandbox(this.cs,sandbox)
        }

        public <V> V doInsideSandbox(Closure<V> body) {
            cs.node {
                def sandboxId = cs.script.startSandbox(blueprint: blueprint, stage: stage,branch:branch, changeset:changeset)
                try {
                    cs.script.echo("health check - waiting for sandbox ${sandboxId} to become ready for testing...")
                    def sandbox = cs.script.waitForSandbox(sandboxId: sandboxId,serviceNameForHealthCheck:serviceNameForHealthCheck)
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
        public final CloudShell cs;
        private final org.jenkinsci.plugins.cs18.api.Sandbox sandbox;

        private Sandbox(CloudShell cs, org.jenkinsci.plugins.cs18.api.Sandbox sandbox) {
            this.sandbox = sandbox
            this.cs = cs
        }
        public org.jenkinsci.plugins.cs18.api.Sandbox getData(){
            return this.sandbox
        }
        public void end(){
            cs.script.endSandbox this.sandbox.id
        }
    }

}