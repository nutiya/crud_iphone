set -e

NAME="spring-app"
USERNAME="phalraksa"
IMAGE="$USERNAME/$NAME:latest"

echo "Building Docker image..."
docker build -t $IMAGE .

echo "Pushing Docker image to Docker Hub..."
docker push $IMAGE

echo "Apply Kubernetes manifest..."
kubectl apply -f k8s/

echo "Getting pods..."
kubectl get pods

echo "Getting services..."
kubectl get services

echo "Fetching the main service"
kubectl get services $NAME-service 