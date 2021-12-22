pipeline {
    agent any
    
    options {
        ansiColor('xterm')
    }
    
    tools {
        maven 'Maven (System Default)'
        jdk 'Open JDK 11'
    }
    
    stages {

        stage('Git') {
            steps {
                cleanWs()
                git 'https://github.com/Student-Management-System/UserSparky.git'
            }
        }

        stage ('Build') {
            steps {
                sh 'mvn -Dmaven.test.failure.ignore=true clean install' 
            }
            post {
                success {
                    junit 'target/surefire-reports/**/*.xml' 
                }
            }
        }
    }
    
    post {
        always {
             // Based on: https://stackoverflow.com/a/39178479
             load "$JENKINS_HOME/.envvars/emails.groovy" 
             step([$class: 'Mailer', recipients: "${env.elsharkawy}, ${env.krafczyk}", notifyEveryUnstableBuild: true, sendToIndividuals: false])
        }
        success {
            archiveArtifacts artifacts: 'target/*.jar'
        }
    }
}