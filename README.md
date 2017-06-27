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

![Alt text](images/global_settings.png?raw=true)

### Pipeline support (Workflow)

How to use 'cs18' DSL:

1. Use **cs18.blueprint** to define a Blueprint object that can then be used to perform operations on a
Blueprint:
```
blueprint = cs18.blueprint('blueprint_name','stage','servicehealthcheck')
```

  * use 'Blueprint.startSandbox' to starts a sandbox and returns a sandbox object which you could stop later.
  * use 'Blueprint.doInsideSandbox' to starts a sandbox for the duration of the body
Sandbox:
  * use 'Sandbox.end()' to stop the sandbox

Enjoy
