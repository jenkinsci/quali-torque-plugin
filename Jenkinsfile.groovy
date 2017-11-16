#!/usr/bin/env groovy
import groovy.json.*

def devops /*devops file instance*/
def devopsVersion = 'v1' /*devops file version*/
def changeset /*Keep the current build changeset*/

properties([buildDiscarder(logRotator(numToKeepStr: '100', artifactNumToKeepStr: '30'))])

try {
    node('gp1') {
        timeout(time: 60, unit: 'MINUTES') {
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
                        if( env.BRANCH_NAME.equals('master') ) {
                            echo "in master branch - running maven with clean"
                            devops.runSh('mvn -B clean package')
                        }
                        else{
                            echo "NOT in master branch - running maven without clean"
                            devops.runSh('mvn -B package')
                        }
                        devops.grantFullPermissions("target") //somehow there is a problem with permissions!?
                        dir('target') {
                            echo "branch: '${env.BRANCH_NAME}'"
                            devops.runSh("echo '${env.BRANCH_NAME}' > branch.txt")
                            devops.runSh('ls')
                            echo "${changeset}"
                            devops.uploadArtifact("cs18.hpi")
                            echo "uploadArtifact cs18.hpi"
                            devops.uploadToS3("branch.txt", "ngdevbox/applications/jenkins/${changeset}")
                            echo "uploadToS3 branch.txt"
                            devops.uploadToS3("cs18.hpi", "ngdevbox/applications/jenkins/${changeset}")
                            echo "uploadToS3 cs18.hpi"
                        }
                    }
                }
                stage('Integration test') {
                    def release = [:]
                    release['jenkins'] = changeset
                    release['cs18-api'] = 'forFE'
                    release['cs18-account-ms'] = 'forFE'
                    release['cs18-db'] = 'forFE'
                    cs18.blueprint("n-ca-jenkins-aws", release).startSandbox()
                    /*doInsideSandbox {
                        echo "sanbox env: ${env.SANDBOX}"
                        def sandbox = readJSON text: "${env.SANDBOX}"
                        def url
                        //start job named test1
                        def jobName = "test1"
                        for (application in sandbox.applications) {
                            if (application["name"] == "jenkins") {
                                url = application["shortcuts"][0]
                                break
                            }
                        }
                        echo "url: ${url}"
                        def innerLog = devops.runJenkinsJob(jobName, url, true)
                        writeFile file: 'innerLog.txt', text: innerLog
                        devops.uploadArtifact("innerLog.txt")
                    }*/
                }
            }
        }
    }
}
catch (Exception ex) {
    throw ex
}
