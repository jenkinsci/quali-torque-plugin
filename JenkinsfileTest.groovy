try {
    node('master') {
        timeout(time: 60, unit: 'MINUTES') {
            def access_token
            timestamps {
                stage('Create and authorize account') {
                    sh "curl -X POST --header 'Content-Type: application/json-patch+json' --header 'Accept: application/json' -d '{ \"email\": \"demo@demo.com\", \"password\": \"demo\" }' 'http://cs18-api.sandbox.com:5050/api/accounts/demo/login' > logged_in_account"
                    def loggedInAccount = readJSON file: 'logged_in_account'
                    echo "loggedInAccount: $loggedInAccount"
                    access_token = loggedInAccount.access_token
                    echo "access_token: $access_token"
                }
                stage('Publish Blueprint') {
                    echo "access_token: $access_token"
                    sh "curl -X POST --header 'Content-Type: application/json-patch+json' --header 'Accept: application/json' --header \"Authorization: Bearer $access_token\" -d '{ \"blueprint_name\": \"fasty-k8s\" }' 'http://cs18-api.sandbox.com:5050/api/spaces/demo-trial/catalog'"
                }
                stage('Integration Test') {
                    def artifacts = [:]
                    artifacts["fasty"] = "applications/fasty/"
                    def inputs = [:]
                    parallel(
                            "testing startSandbox": {
                                def sandbox // for the endSandbox in the finally
                                try {
                                    sandbox = colony.blueprint("demo-trial", "fasty-k8s", "testing_startSandbox", artifacts, 5, inputs).startSandbox(true) //true means: endSandboxOnFail
                                    printSandbox(sandbox, "startSandbox")
                                }
                                catch (Exception ex) {
                                    echo "Error: " + ex.toString()
                                    throw ex
                                }
//                                finally {
//                                    echo "colony.endSandbox(sandbox.id)"
//                                    if (sandbox != null && sandbox.id != null)
//                                        colony.endSandbox("demo-trial", sandbox.id)
//                                }
                            },
                            "testing doInsideSandbox": {
//                                colony.blueprint("demo-trial", "fasty-k8s", "testing_doInsideSandbox", artifacts, 5, inputs).doInsideSandbox() { sandbox_details ->
//                                    printSandbox(sandbox_details, "doInsideSandbox")
//                                }
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

