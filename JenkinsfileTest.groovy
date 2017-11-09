try {
    node('master') {
        timestamps {
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