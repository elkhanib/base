#!/usr/bin/env groovy

node {

    withMaven(){  // use pipeline-maven-plugin to config the maven

        stage('checkout') {
            checkout scm
        }

        stage('check java') {
            sh "java -version"
        }

        stage('clean') {
            sh "mvn clean"
        }

        stage('tests') {
            sh "mvn test"
        }

        stage('package & publish to nexus') {
            //sh "mvn package -Pwebpack,dev,development-phase -DskipTests"
            // FIXME: Temporarily disabled 'publish to nexus'
             sh "mvn deploy -Prelease-phase -DskipTests"
        }
    }
}
