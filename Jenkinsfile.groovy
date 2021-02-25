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
                script{
                    last_started=env.STAGE_NAME
                }
                bat 'mvn clean'
            }
        }
         stage('maven-validate'){
            steps{
                script{
                    last_started=env.STAGE_NAME
                }
                bat 'mvn validate'
            }
        }
         stage('maven-compile'){
            steps{
                script{
                    last_started=env.STAGE_NAME
                }
                bat 'mvn compile'
            }
        }
        stage('maven-test'){
            steps{
                script{
                    last_started=env.STAGE_NAME
                }
                sh 'mvn test'
            }
        }
        stage('maven-package'){
            steps{
                script{
                    last_started=env.STAGE_NAME
                }
                bat 'mvn package'
            }
        }
        stage('maven-install'){
            steps{
                script{
                    last_started=env.STAGE_NAME
                }
                bat 'mvn install'
            }
        }
        stage('sonar-analysis'){
            steps{
                script{
                    last_started=env.STAGE_NAME
                }
                withSonarQubeEnv('sonarqube4'){
                    bat 'mvn sonar:sonar'
                }
            }
        }
        stage('collect-artifacts'){
            steps{
                script{
                    last_started=env.STAGE_NAME
                }
                archiveArtifacts artifacts: 'target/*.jar', followSymlinks: false
            }
        }
        stage ('deploy to artifactory'){
            steps{
                script{
                    last_started=env.STAGE_NAME
                }
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
        post{
            failure{
                mail bcc: '', body: "<b>Failure</b><br>Project: ${env.JOB_NAME} <br>Build Number: ${env.BUILD_NUMBER} <br>Stage Name: $last_started <br> URL de build: ${env.BUILD_URL}", cc: '', charset: 'UTF-8', from: '', mimeType: 'text/html', replyTo: '', subject: "CI Pipeline Info: Project name -> ${env.JOB_NAME}", to: "samalati96@gmail.com";
            }
        }



    }
}