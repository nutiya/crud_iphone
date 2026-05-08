pipeline {
    agent { label 'built-in' }

    environment {
        APP_NAME     = "spring-app"
        IMAGE_REPO   = "phalraksa/spring-app"
        IMAGE_TAG    = "${env.GIT_COMMIT[0..6]}"   // short SHA e.g. a3f91bc
        DOCKER_IMAGE = "${IMAGE_REPO}:${IMAGE_TAG}"
        DOCKER_CREDS = "dockerhub-creds"
    }

    stages {

        stage('Build JAR') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw package -B -DskipTests'
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

        stage('Deploy to Kubernetes') {
            steps {
                withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG')]) {
                    sh '''
                        export KUBECONFIG=$KUBECONFIG

                        # namespace has no -n flag (it defines the namespace itself)
                        kubectl apply -f k8s/namespace.yaml

                        kubectl apply -f k8s/secret.yaml
                        kubectl apply -f k8s/configmap.yaml
                        kubectl apply -f k8s/postgres-statefulset.yaml
                        kubectl rollout status statefulset/postgres     --timeout=120s
                        kubectl apply -f k8s/postgres-service.yaml
                        kubectl apply -f k8s/app-deployment.yaml
                        kubectl apply -f k8s/app-service.yaml
                        kubectl apply -f k8s/ingress.yaml
                        kubectl apply -f k8s/hpa.yaml
                        kubectl set image deployment/spring-app spring-app=${DOCKER_IMAGE}
                        kubectl rollout status deployment/spring-app    --timeout=120s || \
                            (kubectl rollout undo deployment/spring-app && exit 1)
                    '''
                }
            }
        }
    }

    post {
        success {
            echo "✅ Deployed ${IMAGE_TAG} to namespace prod successfully"
        }
        failure {
            echo "❌ Deployment of ${IMAGE_TAG} failed — rolled back"
        }
    }
}