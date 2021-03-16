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
                    } else {
                        echo "Current branch " + env.BRANCH_NAME + " won't run this step"
                    }
                }
            }
            post {
                success {
                    junit '**/target/surefire-reports/TEST-*.xml'
                    archiveArtifacts 'target/*.jar'
                }
            }
        }

        stage("Quality Gate") {
            steps {
                script {
                    if (env.BRANCH_NAME == 'master' || env.BRANCH_NAME == 'develop') {
                        timeout(time: 5, unit: 'MINUTES') {
                            waitForQualityGate abortPipeline: true
                        }
                    } else {
                        echo "Current branch " + env.BRANCH_NAME + " won't run this step"
                    }
                }
            }
        }

        stage('Publish') {
            environment {
                registryCredential = 'dockerhub'
            }

            steps{
                script {
                    if (env.BRANCH_NAME == 'master') {
                        def appimage = docker.build registry
                        docker.withRegistry( '', registryCredential ) {
                            appimage.push()
                            appimage.push('latest')
                        }
                    } else {
                        echo "Current branch " + env.BRANCH_NAME + " won't run this step"
                    }
                }
            }
        }

        stage('Acceptance Tests') {
            steps {
                script {
                    if (env.BRANCH_NAME == 'master') {
                        echo "Acceptance Tests"
                    } else {
                        echo "Current branch " + env.BRANCH_NAME + " won't run this step"
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    if (env.BRANCH_NAME == 'master') {
                        sh "kubectl apply -f deployment.yml"
                        sh "kubectl apply -f service.yml"
                    } else {
                       echo "Current branch " + env.BRANCH_NAME + " won't run this step"
                    }
                }
            }
        }
    }
}
