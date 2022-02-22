#### Two changes we did while torqifying the code (manual steps, not auto refactoring) - which we're not sure of:
1. sandbox-jenkins-plugin\src\main\java\org\jenkinsci\plugins\torque\Config.java
34-35 (changed @symbol("colony") to @symbol("torque"))
1. Changed colony.iml in root to torque.iml
