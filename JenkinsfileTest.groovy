try {
    node('master') {
        timestamps {
            stage('Publish Blueprint'){
                sh "curl -X POST --header 'Content-Type: application/json-patch+json' --header 'Accept: application/json' -d '{ \"blueprint_name\": \"fasty-k8s\" }' 'http://cs18-api.sandbox.com:5050/api/catalog'"
            }
            stage('Integration Test') {
                def release = [:]
                release["fasty"] = ""
                cs18.blueprint("fasty-k8s", release).startSandbox()
                writeFile file: 'sandbox_data.json', text: "${env.SANDBOX}"
            }
        }
    }
}
catch(Exception ex){
    throw ex
}
