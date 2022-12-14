#!/usr/bin/env groovy
import groovy.json.*

def devops /*devops file instance*/
def devopsVersion = 'v1' /*devops file version*/
def changeset /*Keep the current build changeset*/

properties([buildDiscarder(logRotator(numToKeepStr: '100', artifactNumToKeepStr: '30'))])
//Schedule to run every day at 2:00AM
if (env.BRANCH_NAME.equals('master')) {
    properties([pipelineTriggers([cron('H 2 * * * ')])])
}

try {
    node('gp2') {
        timeout(time: 60, unit: 'MINUTES') {
            timestamps {
                stage('Init') {
                    node('master') {
                        devops = load('/var/lib/jenkins/devopsLoader.groovy').loadDevops(devopsVersion)
                        devops.slackSend('good', devops.constants.slack().Channel, "Build Started: ${env.JOB_NAME} ${env.BUILD_NUMBER}")
                    }
                }
                stage('Checkout') {
                    devops.resetWorkspace(devopsVersion)
                    dir('torque') {
                        def scmVars = checkout scm
                        devops.logalore(devopsVersion, scmVars)
                        devops.grantFullPermissions('.')
                        changeset = scmVars.GIT_COMMIT
                    }
                }

                stage('Clean, Package & upload') {
                    dir('torque') {
                        if (env.BRANCH_NAME.equals('master')) {
                            echo "in master branch - running maven with clean"
                            devops.runSh('mvn -B clean package')
                        } else {
                            echo "NOT in master branch - running maven without clean"
                            devops.runSh('mvn -B package')
                        }
                        devops.grantFullPermissions("target") //somehow there is a problem with permissions!?
                        dir('target') {
                            echo "branch: '${env.BRANCH_NAME}'"
                            devops.runSh("echo '${env.BRANCH_NAME}' > branch.txt")
                            devops.runSh('ls')
                            echo "${changeset}"
                            devops.uploadArtifact("torque.hpi")
                            echo "uploadArtifact torque.hpi"
                            devops.uploadToS3("branch.txt", "${devops.constants.misc().BucketName}/applications/jenkins/${changeset}")
                            echo "uploadToS3 branch.txt"
                            devops.uploadToS3("torque.hpi", "${devops.constants.misc().BucketName}/applications/jenkins/${changeset}")
                            echo "uploadToS3 torque.hpi"
                        }
                    }
                }
                stage('Integration test') {
                    def artifacts = [:]
                    def lastDexterArtifacts = devops.getTheLastUpdatedArtifactsPath("${devops.constants.misc().BucketName}/applications/cs18-api/dexter/")
                    echo "Full path for artifact: " + "dexter/$lastDexterArtifacts"

                    artifacts['jenkins'] = "applications/jenkins/$changeset"
                    artifacts['cs18-api'] = "applications/cs18-api/dexter/${lastDexterArtifacts}"
                    artifacts['cs18-account-ms'] = "applications/cs18-account-ms/dexter/${lastDexterArtifacts}"
                    artifacts['cs18-notifications-ms'] = "applications/cs18-notifications-ms/dexter/${lastDexterArtifacts}"
                    artifacts['cs18-blueprint-ms'] = "applications/cs18-blueprint-ms/dexter/${lastDexterArtifacts}"
                    // both must be here although there is not artifacts in s3
                    artifacts['cs18-rabbitmq'] = "applications/cs18-rabbitmq/"
                    artifacts['cs18-postgres'] = "applications/cs18-postgres/"


                    torque.blueprint("demo-trial", "n-ca-jenkins-aws", "jenkinsAndCs18ForPlugin", artifacts, 20).doInsideSandbox(false)
                        { sandbox ->
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

                            if (innerLog.contains("\"result\":\"FAILURE\"")) {
                                throw new Exception("one or more of the innerSandboxes failed. look at the innerLog.txt artifact")
                            }
                        }
                }
            }
        }
    }
    devops.slackSend('good', devops.constants.slack().Channel, "Build Finished: ${env.JOB_NAME} ${env.BUILD_NUMBER}")
}
catch (Exception ex) {

    devops.slackSend('danger', devops.constants.slack().Channel, "Build Failed: ${env.JOB_NAME} ${env.BUILD_NUMBER}\n$ex.message")
    if (env.BRANCH_NAME == 'master') {
        currentBuild.result = "FAILURE"
        devops.sendEmailEx('${SCRIPT, template="build_status.template"}', "[Jenkins] Failure: ${env.JOB_NAME} (Build #${env.BUILD_NUMBER})", devops.constants.mail().ALL)
    }

    throw ex
}
