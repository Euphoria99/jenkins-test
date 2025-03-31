#!/bin/bash

# Run the Jenkins agent in the background
nohup java -jar agent.jar -url http://52.18.70.161:8080/ -secret ae9328e847e7f928a265fd62e52c45c83c0b12147fa8959b8e0a410907f7a582 -name "dev-server" -webSocket -workDir "/home/ubuntu/jenkins" > jenkins-agent.log 2>&1 &
