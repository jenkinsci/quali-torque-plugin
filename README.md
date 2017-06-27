# Sandbox-Jenkins-Plugin

## Prerequisite

1) CS18 server must be running.

2) Jenkins server 2.3 and above.

##Architecture

1) open port between Jenkins Slaves and the CS18 API Server


## Installation
1) Download the hpi package from the releases tab

2) Navigate to the advanced section under the plugins tab in jenkins

3) Upload the hpi file into the "upload plugin" section

4) Restart jenkins

## Configuring CS18 in Jenkins
1) Navigate to the main Jenkins settings page

2) Fill all fields under "CS18 connection" section.

![Alt text](Pics/global_settings.png?raw=true)

### Pipeline support (Workflow)
The "startSandbox" and "endandbox" steps provide an easy way to control the lifecycle of CS18
sandboxes. You can use these steps to create a sandbox, execute some test code on it, then delete it.

### Pipeline Scope Example:
The "withSandbox" step provides an alternative syntax which makes it easy to execute some code in the context of a Sandbox.
The code passed in the closure will be guaranteed to run after the sandbox is up and ready and the sandbox end will be taken care
of automatically upon exiting the scope.

Enjoy
