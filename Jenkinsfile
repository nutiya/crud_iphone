pipeline {

    agent any

    tools {
        maven 'Maven 3.9'
    }

    environment {
        APP_NAME     = "spring-app"
        IMAGE_REPO   = "phalraksa/spring-app"
        IMAGE_TAG    = "${env.GIT_COMMIT[0..6]}"   // short SHA e.g. a3f91bc
        DOCKER_IMAGE = "${IMAGE_REPO}:${IMAGE_TAG}"
        DOCKER_CREDS = "dockerhub-creds"
        HELM_RELEASE = "crud-iphone"
        HELM_CHART   = "./helm"
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


// pipeline {

//     agent any

//     tools {
//         maven 'Maven 3.9'
//         jdk   'JDK 17'
//     }

//     environment {
//         APP_NAME     = "spring-app"
//         IMAGE_REPO   = "phalraksa/spring-app"
//         DOCKER_CREDS = "dockerhub-creds"
//         HELM_RELEASE = "crud-iphone"
//         NAMESPACE    = "prod"
//         CHART_REPO   = "https://your-org/helm-charts.git"   // ← update this
//         CHART_BRANCH = "main"
//     }

//     stages {

//         // ── 1. Declarative: Checkout SCM ─────────────────────────────────────
//         stage('Checkout SCM') {
//             steps {
//                 checkout scm
//             }
//         }

//         // ── 2. Declarative: Tool Install ──────────────────────────────────────
//         // Handled automatically by the tools {} block above.
//         // Add an explicit verify step so the log shows the versions in use.
//         stage('Tool Install') {
//             steps {
//                 sh 'java -version'
//                 sh 'mvn  -version'
//                 sh 'docker --version'
//                 sh 'helm  version --short'
//             }
//         }

//         // ── 3. Determine Latest Version ───────────────────────────────────────
//         // Derives a semantic version from the latest Git tag and appends a
//         // short commit SHA so every build is uniquely traceable.
//         // e.g.  v1.4.2-a3f91bc
//         stage('Determine Latest Version') {
//             steps {
//                 script {
//                     def tag = sh(
//                         script: "git describe --tags --abbrev=0 2>/dev/null || echo 'v0.0.0'",
//                         returnStdout: true
//                     ).trim()

//                     def sha = env.GIT_COMMIT[0..6]

//                     env.APP_VERSION  = "${tag}-${sha}"
//                     env.IMAGE_TAG    = env.APP_VERSION
//                     env.DOCKER_IMAGE = "${IMAGE_REPO}:${IMAGE_TAG}"

//                     echo "Building version: ${env.APP_VERSION}"
//                 }
//             }
//         }

//         // ── 4. Build Source ───────────────────────────────────────────────────
//         stage('Build Source') {
//             steps {
//                 sh 'chmod +x mvnw'
//                 sh './mvnw package -B -DskipTests'
//                 archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
//             }
//         }

//         // ── 5. Build and Push Image ───────────────────────────────────────────
//         stage('Build and Push Image') {
//             steps {
//                 withCredentials([usernamePassword(
//                     credentialsId: "$DOCKER_CREDS",
//                     usernameVariable: 'USER',
//                     passwordVariable: 'PASS'
//                 )]) {
//                     sh """
//                         echo \$PASS | docker login -u \$USER --password-stdin
//                         docker build -t ${DOCKER_IMAGE} .
//                         docker push  ${DOCKER_IMAGE}
//                         docker rmi   ${DOCKER_IMAGE} || true
//                     """
//                 }
//             }
//         }

//         // ── 6. Update Chart Config ────────────────────────────────────────────
//         // Patches values.yaml in the app repo with the new image tag so the
//         // chart always reflects what was just built (GitOps-ready).
//         stage('Update Chart Config') {
//             steps {
//                 script {
//                     sh """
//                         sed -i 's|tag:.*|tag: "${IMAGE_TAG}"|' helm/values.yaml
//                     """

//                     // Commit back only if the file actually changed
//                     def changed = sh(
//                         script: "git diff --quiet helm/values.yaml; echo \$?",
//                         returnStdout: true
//                     ).trim()

//                     if (changed == '1') {
//                         sh """
//                             git config user.email "jenkins@ci"
//                             git config user.name  "Jenkins"
//                             git add helm/values.yaml
//                             git commit -m "ci: bump image tag to ${IMAGE_TAG} [skip ci]"
//                             git push origin HEAD
//                         """
//                     } else {
//                         echo "values.yaml already up to date — no commit needed."
//                     }
//                 }
//             }
//         }

//         // ── 7. Checkout Base Chart ────────────────────────────────────────────
//         // Checks out the Helm chart from a dedicated infra/chart repository
//         // into a local 'chart/' directory, separate from the app source.
//         stage('Checkout Base Chart') {
//             steps {
//                 dir('chart') {
//                     git url: "${CHART_REPO}", branch: "${CHART_BRANCH}"
//                 }
//             }
//         }

//         // ── 8. Deploy Chart Package to OpenShift ─────────────────────────────
//         stage('Deploy Chart Package to OpenShift') {
//             steps {
//                 withCredentials([
//                     file(credentialsId: 'kubeconfig',         variable: 'KUBECONFIG'),
//                     string(credentialsId: 'postgres-password', variable: 'DB_PASS')
//                 ]) {
//                     sh """
//                         export KUBECONFIG=\$KUBECONFIG

//                         helm upgrade --install ${HELM_RELEASE} ./chart \
//                             --namespace   ${NAMESPACE}         \
//                             --create-namespace                 \
//                             --set springApp.image=${IMAGE_REPO}\
//                             --set springApp.tag=${IMAGE_TAG}   \
//                             --set secret.postgresPassword=\$DB_PASS \
//                             --atomic                           \
//                             --timeout 3m
//                     """
//                 }
//             }
//         }
//     }

//     // ── 9. Declarative: Post Action ───────────────────────────────────────────
//     post {
//         success {
//             echo "✅ [${APP_NAME}] ${APP_VERSION} deployed to namespace '${NAMESPACE}' successfully."
//         }
//         failure {
//             echo "❌ [${APP_NAME}] ${APP_VERSION} deployment failed — Helm rolled back automatically."
//         }
//         always {
//             // Clean up any dangling docker credentials from the agent
//             sh 'docker logout || true'
//             cleanWs()
//         }
//     }
// }