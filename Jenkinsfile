pipeline {
    agent any

    environment {
        DOCKER_CREDENTIALS_ID = 'your-docker-credentials-id'
        DOCKER_IMAGE = 'your-dockerhub-username/your-app'
        SSH_CREDENTIALS_ID = 'your-ssh-credentials-id'
        SERVER_2_IP = 'your-server-2-ip'
    }

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/your-username/your-repo.git'
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
                sh './gradlew test' // Or './mvnw test' if using Maven
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
                            ssh -o StrictHostKeyChecking=no user@${SERVER_2_IP} '
                                docker pull ${DOCKER_IMAGE}:latest &&
                                docker stop app || true &&
                                docker rm app || true &&
                                docker run -d --name app -p 8080:8080 ${DOCKER_IMAGE}:latest
                            '
                        """
                        sh """
                            ssh -o StrictHostKeyChecking=no user@${SERVER_2_IP} '
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
