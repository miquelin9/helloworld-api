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
                script {
                    if (env.BRANCH_NAME == 'master' || env.BRANCH_NAME == 'develop') {
                        withSonarQubeEnv('SonarQube') {
                            sh "mvn -Dmaven.test.failure.ignore=true clean package sonar:sonar"
                        }
                    }
                }

                //
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

        stage("Quality Gate") {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
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