pipeline {
    agent any
    environment {
        registry = "mjordalopez/modeling-java"
    }

    tools {
        // Install the Maven version configured as "M3" and add it to the path.
        maven "M3"
    }

    stages {
        stage('Build & SonarQube analysis') {
            steps {
                // Get some code from a GitHub repository
                git branch: 'main',
                credentialsId: 'github',
                url: 'https://github.com/miquelin9/modeling-java.git'

                // Run Maven on a Unix agent.
                withSonarQubeEnv('SonarQube') {
                    sh "mvn -Dmaven.test.failure.ignore=true clean install compile sonar:sonar"
                }
            }
        }

        stage("Quality Gate") {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Test') {
            steps {
                // Run Maven on a Unix agent.
                sh "mvn -Dmaven.test.failure.ignore=true test"
            }
            post {
                // If Maven was able to run the tests, even if some of the test
                // failed, record the test results and archive the jar file.
                success {
                    junit '**/target/surefire-reports/TEST-*.xml'
                    archiveArtifacts 'target/*.jar'
                }
            }
        }

        stage('Publish') {
            environment {
                registryCredential = 'dockerhub'
            }

            steps{
                script {
                    def appimage = docker.build registry
                    docker.withRegistry( '', registryCredential ) {
                        appimage.push()
                        appimage.push('latest')
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                sh "kubectl apply -f deployment.yml"
                sh "kubectl apply -f service.yml"
            }
        }
    }
}