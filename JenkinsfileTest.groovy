try {
    node('master') {
        timeout(time: 60, unit: 'MINUTES') {
            def access_token
            timestamps {
                stage('Integration Test') {
                    def artifacts = [:]
                    def inputs = [:]
                    parallel(
                            "testing startSandbox": {
                                def sandbox // for the endSandbox in the finally
                                try {
                                    sandbox = torque.blueprint("Trial", "fasty-k8s", "testing_startSandbox", "PT30M", artifacts, inputs, 5).startSandbox(true) //true means: endSandboxOnFail
                                    printSandbox(sandbox, "startSandbox")
                                }
                                catch (Exception ex) {
                                    echo "Error: " + ex.toString()
                                    throw ex
                                }
                                finally {
                                    echo "torque.endSandbox(sandbox.id)"
                                    if (sandbox != null && sandbox.id != null)
                                        torque.endSandbox("Trial", sandbox.id)
                                }
                            },
                            "testing doInsideSandbox": {
                                torque.blueprint("Trial", "fasty-k8s", "testing_doInsideSandbox", "PT30M", artifacts, inputs, 5).doInsideSandbox() { sandbox_details ->
                                    printSandbox(sandbox_details, "doInsideSandbox")
                                }
                            })
                }
            }
        }
    }
}
catch (Exception ex) {
    throw ex
}

def printSandbox(sandbox, name) {
    echo "start:" + name
    echo "sandbox: " + sandbox
    echo "sandbox.toString(): " + sandbox.toString()
    echo "sandbox.toString(1): " + sandbox.toString(1)
    echo "sandbox.toString(6): " + sandbox.toString(6)
    echo "sandbox.toString(2,4): " + sandbox.toString(2, 4)
    echo "sandbox.id: " + sandbox.id
    echo "sandbox.name: " + sandbox.name
    echo "sandbox.blueprint_name: " + sandbox.blueprint_name
    echo "sandbox.sandbox_status: " + sandbox.sandbox_status
    for (app in sandbox.applications) {
        echo "app.name: " + app.name
        echo "app.deployment_status: " + app.deployment_status
    }

    echo "sandbox[\"applications\"] :" + sandbox["applications"]
    echo "sandbox[\"applications\"][0].name :" + sandbox["applications"][0].name
    echo "sandbox[\"applications\"][0].name :" + sandbox["applications"][0].deployment_status
    echo "sandbox[\"applications\"][0][\"name\"] :" + sandbox["applications"][0]["name"]
    echo "sandbox[\"applications\"][0][\"deployment_status\"] :" + sandbox["applications"][0]["deployment_status"]
    echo "end:" + name
}

