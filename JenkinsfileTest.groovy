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

                    echo "testing doInsideSandbox"
                    colony.blueprint("fasty-k8s", "testing_doInsideSandbox", release, 5).doInsideSandbox() { sandbox_details->
                        echo "sandbox_details - from delegate param: $sandbox_details"
                    }

                    echo "testing startSandbox"
                    def sandbox
                    try {
                        sandbox = colony.blueprint("fasty-k8s", "testing_startSandbox", release, 5).startSandbox()
                        echo "startSandbox - from the enviroment param - sandbox: ${sandbox}"
                        echo "sandbox.getData():"
                        echo sandbox.getData()
                    }
                    finally {
                        echo "before sandbox.end()"
                        sandbox.end()
                        echo "after sandbox.end()"
                    }
                }
            }
        }
    }
}
catch (Exception ex) {
    throw ex
}
