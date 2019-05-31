#!/usr/bin/env groovy

// Declarative //
pipeline {
    agent any
    stages {
        stage('checkout') {
            steps {
                withMaven() {
                    checkout scm
                }
            }
        }

        stage('check java') {
            steps {
                withMaven() {
                    sh "java -version"
                }
            }
        }

        stage('clean') {
            steps {
                withMaven() {
                    sh "mvn clean"
                }
            }
        }

        stage('tests') {
            steps {
                withMaven() {
                    sh "mvn test"
                }
            }
        }

        stage('Build') {
            steps {
                withMaven() {
                    sh "mvn compile"
                }
            }
        }

        stage('Install to local maven repository ') {
            when {
                branch 'master'
            }
            steps {
                withMaven() {
                    sh "mvn install -DskipTests"
                }
            }
        }

//        stage('package & publish to nexus') {
//            sh "mvn compile package -DskipTests"
//            // FIXME: Temporarily disabled 'publish to nexus'
//            sh "mvn deploy -Prelease-phase -DskipTests"
//        }
    }
}