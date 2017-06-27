package org.jenkinsci.plugins.cs18.steps

import net.sf.json.JSONObject

class CloudShell implements Serializable {

    private org.jenkinsci.plugins.workflow.cps.CpsScript script

    public CloudShell(org.jenkinsci.plugins.workflow.cps.CpsScript script) {
        this.script = script
    }

    public Sandbox sandbox() {
        new Sandbox(this)
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

    public static class Sandbox implements Serializable {
        public final CloudShell cs;
        private Sandbox(CloudShell cs) {
            this.cs = cs
        }
        public org.jenkinsci.plugins.cs18.api.Sandbox start(String blueprint, String stage = null, String serviceNameForHealthCheck = null) {
            def sandbox
            cs.node {
                sandbox = cs.script.startSandbox(blueprint: blueprint, serviceNameForHealthCheck: serviceNameForHealthCheck, stage: stage)
                def sandboxJson = JSONObject.fromObject(sandbox).toString()
                cs.script.echo("Sandbox:${sandboxJson}")
            }
            return sandbox
        }

        public void end(String sandboxId){
            cs.script.endSandbox sandboxId
        }

        public <V> V inside(String blueprint, String stage = null, String serviceNameForHealthCheck = null, Closure<V> body) {
            cs.node {
                def sandbox = cs.script.startSandbox(blueprint: blueprint, serviceNameForHealthCheck: serviceNameForHealthCheck, stage: stage)
                def sandboxJson =JSONObject.fromObject(sandbox).toString()
                cs.script.echo("Sandbox:${sandboxJson}")
                try {
                    cs.script.withEnv(["SANDBOX=${sandboxJson}"]) {
                        body()
                    }
                }
                finally {
                    cs.script.endSandbox sandbox['id']
                }
            }
        }
    }

}