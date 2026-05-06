pipeline {
    agent any

    environment {
        APP_NAME = "spring-app"
        DOCKER_IMAGE = "phalraksa/spring-app:latest"
        DOCKER_CREDS = "docker-hub-creds"
    }

    tools {
        maven "Maven3"   // or remove if using docker maven image
        jdk "JDK17"
    }

    stages {

        stage('Checkout Code') {
            steps {
                git branch: 'main',
                url: 'https://github.com/nutiya/crud_iphone.git'
            }
        }

        stage('Build with Maven') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Run Tests') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh """
                docker build -t $DOCKER_IMAGE .
                """
            }
        }

        stage('Login to Docker Hub') {
            steps {
                withCredentials([usernamePassword(credentialsId: "$DOCKER_CREDS",
                    usernameVariable: 'USER',
                    passwordVariable: 'PASS')]) {

                    sh """
                    echo $PASS | docker login -u $USER --password-stdin
                    """
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
                docker stop $APP_NAME || true
                docker rm $APP_NAME || true

                docker run -d --name $APP_NAME -p 8080:8080 $DOCKER_IMAGE
                """
            }
        }
    }

    post {
        success {
            echo "✅ Build, Docker push and deployment successful!"
        }

        failure {
            echo "❌ Pipeline failed"
        }
    }
}