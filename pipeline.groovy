pipeline {
    agent any

    environment {
        AWS_ACCOUNT_ID = "445567081408"
        AWS_DEFAULT_REGION = "eu-west-1"
        IMAGE_REPO_NAME = "springboot"
        IMAGE_TAG = "latest"
        REPOSITORY_URI = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com/${IMAGE_REPO_NAME}"
        CONTAINER_NAME = "springcont"
        PORT = "8080"
        NETWORK="springnetwork"
        IMG_ID=''

        // SonarQube details
        SONARQUBE_URL = "http://localhost:9000"
        SONARQUBE_TOKEN = "squ_3d6a57dc5b1de6751c684b3e76bda032025d5efa" 
    }

    tools {
        maven 'maven-3.9.9' 
    }

    stages {

        stage('Logging into AWS ECR') {
            steps {
                script {
                    sh "aws ecr get-login-password --region ${AWS_DEFAULT_REGION} | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com"
                }
            }
        }

        stage('Cloning Git') {
            steps {
                checkout scmGit(branches: [[name: '*/main']], 
                extensions: [], 
                userRemoteConfigs: [[credentialsId: 'pavan-git', url: 'https://github.com/Euphoria99/jenkins-test']])
            }
        }

        stage('Code Analysis with SonarQube') {
            steps {
                script {
                    sh '''
                        mvn clean verify sonar:sonar \
                          -Dsonar.projectKey=springboot-app \
                          -Dsonar.host.url=${SONARQUBE_URL} \
                          -Dsonar.login=${SONARQUBE_TOKEN} \
                          -Dsonar.java.binaries=target/classes
                    '''
                }
            }
        }

        stage('Run Tests & Generate Coverage Report') {
            steps {
                script {
                    jacoco(
                        execPattern: '**/jacoco.exec',
                        classPattern: '**/classes',
                        sourcePattern: '**/src/main/java',
                        classDirectories: [[pattern: '**/classes']],
                        sourceDirectories: [[pattern: '**/src/main/java']]
                    )
                }
            }
        }


        stage('Building Image') {
            steps {
                script {
                    dockerImage = docker.build "${REPOSITORY_URI}:${IMAGE_TAG}"
                }
            }
        }

        stage('Pushing to ECR') {
            steps {
                script {
                    sh "docker push ${REPOSITORY_URI}:${IMAGE_TAG}"
                }
            }
        }

        stage("Logged in into Agent") {
            agent { label 'dev-server' }
            steps {
                script {
                    sh "aws ecr get-login-password --region ${AWS_DEFAULT_REGION} | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com"
                }
            }
        }

        stage('Pull Updated Image') {
            agent { label 'dev-server' }
            steps {
                script {
                    sh "docker pull ${REPOSITORY_URI}:${IMAGE_TAG}"
                }
            }
        }

        stage('Stop Old Container') {
            agent { label 'dev-server' }
            steps {
                script {
                    sh "docker stop ${CONTAINER_NAME} || true"
                }
            }
        }

        stage('Remove Old Container') {
            agent { label 'dev-server' }
            steps {
                script {
                    sh "docker rm ${CONTAINER_NAME} || true"
                }
            }
        }

        stage("Run New container") {
            agent { label 'dev-server' }
            steps {
                script {
                    sh "docker run --name ${CONTAINER_NAME} -d --restart always -p ${PORT}:${PORT}  ${REPOSITORY_URI}:${IMAGE_TAG}"
                }
            }
        }
        
        stage('Prune Dangling Images') {
            agent { label 'dev-server' }
            steps {
                script {
                    sh "docker images --quiet --filter=dangling=true | xargs --no-run-if-empty docker rmi"
                }
            }
        }
    }

    post {
        always {
            script {
                echo "All stages completed successfully!"
            }
        }
    }
}
