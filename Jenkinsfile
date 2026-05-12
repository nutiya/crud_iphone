pipeline {
    
    agent any

    environment {
        APP_NAME     = "spring-app"
        IMAGE_REPO   = "phalraksa/spring-app"
        IMAGE_TAG    = "${env.GIT_COMMIT[0..6]}"   // short SHA e.g. a3f91bc
        DOCKER_IMAGE = "${IMAGE_REPO}:${IMAGE_TAG}"
        DOCKER_CREDS = "dockerhub-creds"
        HELM_RELEASE = "crud-iphone"
        HELM_CHART   = "./helm/crud-iphone"
        NAMESPACE    = "prod"
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

        stage('Deploy to Kubernetes (Helm)') {
            steps {
                withCredentials([
                    file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG'),
                    string(credentialsId: 'postgres-password', variable: 'DB_PASS')
                ]) {
                    sh '''
                        export KUBECONFIG=$KUBECONFIG

                        helm upgrade --install $HELM_RELEASE $HELM_CHART \
                            --namespace $NAMESPACE \
                            --create-namespace \
                            --set springApp.tag=$IMAGE_TAG \
                            --set secret.postgresPassword=$DB_PASS \
                            --atomic \
                            --timeout 3m
                    '''
                }
            }
        }
    }

    post {
        success {
            echo "✅ Deployed ${IMAGE_TAG} to namespace ${NAMESPACE} successfully"
        }
        failure {
            echo "❌ Deployment of ${IMAGE_TAG} failed — Helm rolled back automatically"
        }
    }
}