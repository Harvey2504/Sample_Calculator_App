pipeline{
    agent any
    
    triggers{
        pollSCM('* * * * *')
    }

    tools{
        maven 'maven-3'
        jdk 'java11'
    }

    stages{
        stage('maven-clean'){
            steps{
                bat 'mvn clean'
            }
        }
         stage('maven-validate'){
            steps{
                bat 'mvn validate'
            }
        }
         stage('maven-compile'){
            steps{
                bat 'mvn compile'
            }
        }
        stage('maven-test'){
            steps{
                bat 'mvn test'
            }
        }
        stage('maven-package'){
            steps{
                bat 'mvn package'
            }
        }
        stage('maven-install'){
            steps{
                bat 'mvn install'
            }
        }
        stage('sonar-analysis'){
            steps{
                withSonarQubeEnv('sonarqube4'){
                    bat 'mvn sonar:sonar'
                }
            }
        }
        stage('collect-artifacts'){
            steps{
                archiveArtifacts artifacts: 'target/*.jar', followSymlinks: false
            }
        }

        stage ('deploy to artifactory'){
            steps{
                rtUpload(
                    serverId: 'artifactoryserver',
                    spec: '''{
                        "files":[
                            {
                              "pattern": "target/*.jar",
                                "target": "art-doc-dev-local1"
                             }
                        ]
                    }''',
                    buildName: 'holyFrog',
                    buildNumber: '1'
                )
            }
        }
    }
}