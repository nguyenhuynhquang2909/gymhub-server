pipeline {
    agent any

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
                git credentialsId: GITHUB_CREDENTIALS_ID, url: 'https://github.com/nguyenhuynhquang2909/gymhub-server.git'
            }
        }
        stage('Build') {
            steps {
                script {
                    dockerImage = docker.build(DOCKER_IMAGE)
                }
            }
        }
        stage('Test') {
            steps {
                sh './mvnw test'
            }
        }
        stage('Push to Docker Hub') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', DOCKER_CREDENTIALS_ID) {
                        dockerImage.push("${env.BUILD_NUMBER}")
                        dockerImage.push("latest")
                    }
                }
            }
        }
        stage('Deploy') {
            steps {
                script {
                    sshagent([SSH_CREDENTIALS_ID]) {
                        sh """
                            ssh -p ${SSH_PORT} -o StrictHostKeyChecking=no user@${SERVER_2_IP} '
                                docker pull ${DOCKER_IMAGE}:latest &&
                                docker stop app || true &&
                                docker rm app || true &&
                                docker run -d --name app -p 8080:8080 ${DOCKER_IMAGE}:latest
                            '
                        """
                        sh """
                            ssh -p ${SSH_PORT} -o StrictHostKeyChecking=no user@${SERVER_2_IP} '
                                docker ps -a
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
