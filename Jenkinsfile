pipeline {
    agent { label 'built-in' }

    environment {
        APP_NAME    = "spring-app"
        DOCKER_IMAGE = "phalraksa/spring-app:latest"
        DOCKER_CREDS = "dockerhub-creds"
    }

    stages {

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

        stage('Deploy to Kubernetes') {
            steps {
                withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG')]) {
                    sh """
                        export KUBECONFIG=$KUBECONFIG
                        kubectl apply -f k8s/secret.yaml
                        kubectl apply -f k8s/configmap.yaml
                        kubectl apply -f k8s/postgres-deployment.yaml
                        kubectl apply -f k8s/postgres-service.yaml
                        kubectl rollout status deployment/postgres --timeout=120s
                        kubectl apply -f k8s/app-deployment.yaml
                        kubectl apply -f k8s/app-service.yaml
                        kubectl apply -f k8s/ingress.yaml
                        kubectl rollout restart deployment/spring-app
                        kubectl rollout status deployment/spring-app --timeout=120s
                    """
                }
            }
        }
    }

    post {
        success { echo "✅ Build, push, and Kubernetes deployment successful!" }
        failure { echo "❌ Pipeline failed" }
    }
}