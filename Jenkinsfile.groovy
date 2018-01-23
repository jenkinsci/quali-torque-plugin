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
                    dir('colony') {
                        def scmVars = checkout scm
                        devops.logalore(devopsVersion, scmVars)
                        devops.grantFullPermissions('.')
                        changeset = scmVars.GIT_COMMIT
                    }
                }

                stage('Clean, Package & upload') {
                    dir('colony') {
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
                            devops.uploadArtifact("colony.hpi")
                            echo "uploadArtifact colony.hpi"
                            devops.uploadToS3("branch.txt", "ngdevbox/applications/jenkins/${changeset}")
                            echo "uploadToS3 branch.txt"
                            devops.uploadToS3("colony.hpi", "ngdevbox/applications/jenkins/${changeset}")
                            echo "uploadToS3 colony.hpi"
                        }
                    }
                }
                stage('Integration test') {
                    def release = [:]
                    release['jenkins'] = changeset
                    release['cs18-api'] = 'forDexter'
                    release['cs18-account-ms'] = 'forDexter'
                    release['cs18-db'] = 'forDexter'
                    colony.blueprint("demo trial", "n-ca-jenkins-aws", "jenkinsAndCs18ForPlugin", release, 20).doInsideSandbox { sandbox ->
                        echo "sandbox env: " + sandbox.toString()

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

                        if(innerLog.contains("\"result\":\"FAILURE\"")){
                            throw new Exception("one or more of the innerSandboxes failed. look at the innerLog.txt artifact")
                        }
                    }
                }
            }
        }
    }
}
catch (Exception ex) {
    throw ex
}
