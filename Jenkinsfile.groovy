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

            stage('Build') {
                dir('cs18-app/server') {
                    devops.runSh('dotnet restore')
                    devops.runSh('dotnet build')
                }
            }
            stage('Build') {
                steps {
                    sh 'mvn -B -DskipTests clean package'
                }
            }
        }
    }
}
catch (Exception ex) {
    // abugov: send email is only temporary
    //if (devops != null)
    //devops.sendMail("devops@quali.com", "build failed", "sorry for this email will be removed soon (sent from Jenkinsfile.groovy of tryops/master)")

    throw ex
}