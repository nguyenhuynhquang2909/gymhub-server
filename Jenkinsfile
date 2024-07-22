pipeline {
    agent any

    tools { 
        maven 'my-maven' 
    }
    environment {
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

        stage('Build with Maven') {
            steps {
                sh 'mvn --version'
                sh 'java -version'
                sh 'mvn clean package -Dmaven.test.failure.ignore=true'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t quangnguyen2909/gymhub .'
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
