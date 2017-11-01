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
            stage('Clean, Build & Package') {
                steps {
                    dir('cs18') {
                        devops.runSh('mvn -B -DskipTests clean package')
                    }
                }
            }
        }
    }
}
catch (Exception ex) {
    throw ex
}