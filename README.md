# Sandbox-Jenkins-Plugin

## Prerequisite

1) cs2018 server must be running.

2) Jenkins server 2.3 and above.

##Architecture

1) open port between Jenkins Slaves and the CloudShell Web Server (82 by default but configurable)


## Installation
1) Download the hpi package from the releases tab

2) Navigate to the advanced section under the plugins tab in jenkins

3) Upload the hpi file into the "upload plugin" section

4) Restart jenkins

## Configuring CloudShell in Jenkins
1) Navigate to the main Jenkins settings page

2) Fill all fields under "cloudshell connection" section.

![Alt text](images/global_settings.png?raw=true)

### Pipeline support (Workflow)
The "createSandbox" and "deleteSandbox" steps provide an easy way to control the lifecycle of CloudShell
sandboxes. You can use these steps to create a sandbox, execute some test code on it, then delete it.

### Pipeline Scope Example:
The "withSandbox" step provides an alternative syntax which makes it easy to execute some code in the context of a Sandbox.
The code passed in the closure will be guaranteed to run after the sandbox is up and ready and the sandbox teardown will be taken care
of automatically upon exiting the scope.

Enjoy
