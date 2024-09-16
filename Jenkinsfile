pipeline {
    agent any

    tools {
        maven 'my-maven'
    }
    environment {
        DOCKER_CREDENTIALS_ID = 'dockerhub-token'
        DOCKER_IMAGE = 'quangnguyen2909/gymhub'
        SSH_CREDENTIALS_ID = 'server-ssh-credentials-id'
        SERVER_2_IP = '14.241.129.58'
        SSH_PORT = '163'
        GITHUB_CREDENTIALS_ID = 'github-token'
    }
    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', credentialsId: GITHUB_CREDENTIALS_ID, url: 'https://github.com/nguyenhuynhquang2909/gymhub-server.git'
            }
        }

        stage('Pull Latest Docker Image') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', DOCKER_CREDENTIALS_ID) {
                        sh "docker pull ${DOCKER_IMAGE}:latest || true"
                    }
                }
            }
        }

        stage('Build with Maven') {
            steps {
                sh 'mvn --version'
                sh 'java -version'
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    def currentHash = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
                    def latestHash = sh(returnStdout: true, script: 'docker inspect --format="{{index .Config.Labels \\"commit\\"}}" ${DOCKER_IMAGE}:latest || echo "none"').trim()

                    if (currentHash == latestHash) {
                        echo "No changes in the source code. Skipping Docker build."
                    } else {
                        echo "Changes detected. Building new Docker image."
                        sh "docker buildx build --platform linux/amd64 -t ${DOCKER_IMAGE}:latest --label commit=${currentHash} . --push"
                    }
                }
            }
        }

        stage('Push Docker Image to Docker Hub') {
            when {
                expression {
                    def currentHash = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
                    def latestHash = sh(returnStdout: true, script: 'docker inspect --format="{{index .Config.Labels \\"commit\\"}}" ${DOCKER_IMAGE}:latest || echo "none"').trim()
                    return currentHash != latestHash
                }
            }
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', DOCKER_CREDENTIALS_ID) {
                        echo "Pushing new Docker image."
                        sh "docker push ${DOCKER_IMAGE}:latest"
                    }
                }
            }
        }

        stage('Deploy Spring Boot to Server 2') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: SSH_CREDENTIALS_ID, usernameVariable: 'SSH_USER', passwordVariable: 'SSH_PASSWORD')]) {
                        sh """
                            sshpass -p ${SSH_PASSWORD} ssh -o StrictHostKeyChecking=no -p ${SSH_PORT} ${SSH_USER}@${SERVER_2_IP} '
                                docker pull ${DOCKER_IMAGE}:latest &&
                                docker container stop springboot || echo "this container does not exist" &&
                                docker container rm springboot || echo "this container does not exist" &&
                                docker run -d --name springboot -p 8081:8080 ${DOCKER_IMAGE}:latest
                            '
                        """
                    }
                }
            }
        }
    }
    post {
        always {
            cleanWs()
        }
    }
}
