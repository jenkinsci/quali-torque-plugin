#!groovy​

def devops /*devops file instance*/
def devopsVersion = 'v1' /*devops file version*/
def changeset /*Keep the current build changeset*/

properties([buildDiscarder(logRotator(numToKeepStr: '100', artifactNumToKeepStr: '30'))])

try {
    node('gp1') {
        timestamps {
            stage('Init') {
                node('master') {
                    devops = load('/var/lib/jenkins/devopsLoader.groovy').loadDevops(devopsVersion)
                }
            }
            stage('Checkout') {
                devops.resetWorkspace(devopsVersion)
                dir('cs18') {
                    def scmVars = checkout scm
                    devops.logalore(devopsVersion, scmVars)
                    devops.grantFullPermissions('.')
                    changeset = scmVars.GIT_COMMIT
                }
            }
            
            stage('Clean, Package & upload') {
                dir('cs18') {
                    devops.runSh('ls')
                    devops.runSh('mvn -B package') // TODO: add clean
                    dir('target'){
                        writeFile file: 'branch.txt', text: "${env.BRANCH_NAME}"
                        devops.runSh('ls')
                        echo ${changeset}
                        devops.uploadArtifact("cs18.hpi")
                        echo "uploadArtifact cs18.hpi"
                        devops.uploadToS3("branch.txt", "ngdevbox/applications/jenkins/${changeset}")
                        echo "uploadToS3 branch.txt"
                        devops.uploadToS3("cs18.hpi", "ngdevbox/applications/jenkins/${changeset}")
                        echo "uploadToS3 cs18.hpi"
                    }
                }
            }
            stage('Integration test'){
                def release = [:]
                release['jenkins'] = changeset
                cs18.blueprint("n-ca-jenkins-aws", release).doInsideSandbox {
                    echo "branch: "${env.BRANCH_NAME}
                    echo "inside the sandbox! "${env.SANDBOX}
                }
            }
        }
    }
}
catch (Exception ex) {
    throw ex
}
