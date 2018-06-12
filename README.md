# Sandbox-Jenkins-Plugin 

## Prerequisite

1) colony server must be running.

2) Jenkins server 2.3 and above.

## Architecture

1) open port between Jenkins Slaves and the colony API Server


## Installation
1) Download the hpi package from the releases tab

2) Navigate to the advanced section under the plugins tab in jenkins

3) Upload the hpi file into the "upload plugin" section

4) Restart jenkins

## Configuring colony in Jenkins
1) Navigate to the main Jenkins settings page

2) Fill all fields under "colony connection" section.

![Alt text](images/global_settings.png?raw=true)

### Pipeline support (Workflow)

How to use 'colony' DSL:

Blueprint:
  * Use **colony.blueprint** to define a Blueprint object that can then be used to perform operations on a blueprint.
```
blueprint = colony.blueprint('blueprint_name', ['appname1': 'version', 'appname2': 'version'])
```
  * Use **blueprint.startSandbox** to starts a sandbox and returns a sandbox object which you could stop later.
```
sandbox = blueprint.startSandbox()
```
  * Use **blueprint.doInsideSandbox** to starts a sandbox for the duration of the body
```
blueprint.doInsideSandbox{
   //code block
}
```

Sandbox:
  * Use **sandbox.end()** to stop the sandbox
```
sandbox.end()
```

Enjoy
