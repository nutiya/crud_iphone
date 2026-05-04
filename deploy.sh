set -e

NAME="spring-app"
USERNAME="phalraksa"
IMAGE="$USERNAME/$NAME:latest"

echo "Checking tools..."
docker --version
kubectl version --client

echo "Logging into Docker Hub..."
echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin

echo "Building Docker image..."
docker build -t $IMAGE .

echo "Pushing Docker image..."
docker push $IMAGE

echo "Deploying to Kubernetes..."
kubectl apply -f k8s/

echo "Pods:"
kubectl get pods

echo "Services:"
kubectl get services