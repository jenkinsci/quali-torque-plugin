try {
    node('master') {
        timestamps {
            stage('Integration Test') {
                def release = [:]
                cs18.blueprint("fasty-k8s", release).doInsideSandbox {
                    writeFile file: 'sandbox_data.json', text: "${env.SANDBOX}"
                }
            }
        }
    }
}
catch(Exception ex){
    throw ex
}