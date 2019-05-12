#!/bin/bash
export MAVEN_OPTS="-Xmx768m -Xms256m"
mvn clean package -Dmaven.test.skip=true 
