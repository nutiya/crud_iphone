pipeline {
    agent { label 'built-in' }

    environment {
        APP_NAME = "spring-app"
        DOCKER_IMAGE = "phalraksa/spring-app:latest"
        DOCKER_CREDS = "dockerhub-creds"
    }

    stages {
        stage('Test Compose') {
            steps {
                sh '''
                    docker --version
                    docker-compose --version
                '''
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker build -t $DOCKER_IMAGE ."
            }
        }

        stage('Login to Docker Hub') {
            steps {
                withCredentials([usernamePassword(credentialsId: "$DOCKER_CREDS",
                    usernameVariable: 'USER',
                    passwordVariable: 'PASS')]) {
                    sh 'echo $PASS | docker login -u $USER --password-stdin'
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                sh "docker push $DOCKER_IMAGE"
            }
        }

        stage('Deploy') {
            steps {
                sh """
                    docker compose down || true
                    docker compose up -d --build
                """
            }
        }
    }

    post {
        success { echo "✅ Build, Docker push and deployment successful!" }
        failure { echo "❌ Pipeline failed" }
    }
}