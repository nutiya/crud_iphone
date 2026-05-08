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

                        # everything else targets prod
                        kubectl apply -f k8s/secret.yaml               -n prod
                        kubectl apply -f k8s/configmap.yaml            -n prod
                        kubectl apply -f k8s/postgres-statefulset.yaml -n prod
                        kubectl rollout status statefulset/postgres     -n prod --timeout=120s
                        kubectl apply -f k8s/postgres-service.yaml     -n prod
                        kubectl apply -f k8s/app-deployment.yaml       -n prod
                        kubectl apply -f k8s/app-service.yaml          -n prod
                        kubectl apply -f k8s/ingress.yaml              -n prod
                        kubectl apply -f k8s/hpa.yaml                  -n prod
                        kubectl set image deployment/spring-app spring-app=${DOCKER_IMAGE} -n prod
                        kubectl rollout status deployment/spring-app    -n prod --timeout=120s || \
                            (kubectl rollout undo deployment/spring-app -n prod && exit 1)
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