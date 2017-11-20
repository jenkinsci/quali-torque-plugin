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
                    cs18.blueprint("fasty-k8s", release).doInsideSandbox() {
                        echo "doInsideSandbox - from the enviroment param - env.SANDBOX: ${env.SANDBOX}"
                    }

                    def sandbox
                    try {
                        sandbox = cs18.blueprint("fasty-k8s", release).startSandbox()
                        echo "startSandbox - from the enviroment param - sandboxJson: ${sandboxJson}"
                    }
                    finally {
                        sandbox.end()
                    }
                }
            }
        }
    }
}
catch (Exception ex) {
    throw ex
}
