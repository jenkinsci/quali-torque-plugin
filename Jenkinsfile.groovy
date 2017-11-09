#!/usr/bin/env groovy
import groovy.json.*

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
                    devops.runSh('mvn -B -offline -Dmaven.test.skip=true package') // TODO: add clean
                    dir('target'){
                        echo "branch: ${env.BRANCH_NAME}"
                        devops.runSh("echo ${env.BRANCH_NAME} > branch.txt")
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
            stage('Integration test'){
                def release = [:]
                release['jenkins'] = changeset
                cs18.blueprint("n-ca-jenkins-aws", release).doInsideSandbox() {
                    echo "sanbox env: ${env.SANDBOX}"
                    def sandbox = readJSON text: "${env.SANDBOX}"
                    def url = sandbox.applications[0].shortcuts[0]
                    echo "url: ${url}"
                    //start job named test1
                    def jobName="test1"
                    echo devops.runJenkinsJob(url, jobName, true)
                }
            }
        }
    }
}
catch (Exception ex) {
    throw ex
}
