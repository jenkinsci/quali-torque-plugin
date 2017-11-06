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
            stage('Install Requirements') {
                devops.runSh('apt-get update')
                devops.runSh('apt-get install -y maven')
                devops.runSh('apt-get install -y openjdk-8-jdk')
            }
            stage('Clean, Package & upload') {
                dir('cs18') {
                    devops.runSh('ls')
                    devops.runSh('mvn -B clean package')
                    dir('target'){
                        devops.uploadArtifact("cs18.hpi")
                        writeFile file: 'branch.txt', text: "${env.BRANCH_NAME}"
                        devops.uploadToS3("branch.txt", "ngdevbox/applications/jenkins/${changeset}")
                        devops.uploadToS3("cs18.hpi", "ngdevbox/applications/jenkins/${changeset}")
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