try {
    node('master') {
        timeout(time: 60, unit: 'MINUTES') {
            timestamps {
                stage('Publish Blueprint') {
                    sh "curl -X POST --header 'Content-Type: application/json-patch+json' --header 'Accept: application/json' -d '{ \"blueprint_name\": \"fasty-k8s\" }' 'http://cs18-api.sandbox.com:5050/api/catalog'"
                }
                stage('Integration Test') {
                    def release = [:]
                    release["fasty"] = ""

                    echo "testing startSandbox"
                    def sandbox // for the endSandbox in the finally
                    try {
                        sandbox = colony.blueprint("fasty-k8s", "testing_startSandbox", release, 5).startSandbox()
                        echo "startSandbox returned: ${sandbox}"
                        echo "sandbox.id: " + sandbox.id
                        echo "sandbox.name: " + sandbox.name
                        echo "sandbox.blueprint_name: " + sandbox.blueprint_name
                        echo "sandbox.sandbox_status: " + sandbox.sandbox_status
                        for (app in sandbox.applications) {
                            echo "app.name: " + app.name
                            echo "app.sandbox_status: " + app.sandbox_status
                        }
                    }
                    catch(Exception ex){
                        echo ex.toString()
                        throw ex
                    }
                    finally {
                        echo "colony.endSandbox(sandbox.id)"
                        if (sandbox != null && sandbox.id != null)
                            colony.endSandbox(sandbox.id)
                    }

                    echo "testing doInsideSandbox"
                    colony.blueprint("fasty-k8s", "testing_doInsideSandbox", release, 5).doInsideSandbox() { sandbox_details ->
                        echo "doInsideSandbox delegate: $sandbox_details"
                    }
                }
            }
        }
    }
}
catch (Exception ex) {
    throw ex
}
