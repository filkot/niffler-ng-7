#!/bin/bash
source ./docker.properties
export PROFILE=docker
export PREFIX="${IMAGE_PREFIX}"
export ALLURE_DOCKER_API=http://allure:5050/
export HEAD_COMMIT_MESSAGE="local build"
export FRONT_VERSION="2.1.0"
export COMPOSE_PROFILES=test
export ARCH=$(uname -m)

echo '### Java version ###'
java --version

# Устанавливаем значения по умолчанию
export FRONT="niffler-ng-client"
export BROWSER="chrome"

# Обрабатываем аргументы
if [[ "$1" = "gql" ]]; then
  export FRONT="niffler-ng-gql-client"
elif [[ "$1" = "firefox" ]]; then
  export BROWSER="firefox"
fi

docker compose down

docker_containers=$(docker ps -a -q)
docker_images=$(docker images --format '{{.Repository}}:{{.Tag}}' | grep 'niffler')

if [ ! -z "$docker_containers" ]; then
  echo "### Stop containers: $docker_containers ###"
  docker stop $docker_containers
  docker rm $docker_containers
fi

#if [ ! -z "$docker_images" ]; then
#  echo "### Remove images: $docker_images ###"
#  docker rmi $docker_images
#fi
#
#bash ./gradlew clean
#bash ./gradlew jibDockerBuild -x :niffler-e-2-e-tests:test
#
#if [[ "$BROWSER" = "chrome" ]]; then
#  docker pull selenoid/vnc_chrome:127.0
#else
#  docker pull selenoid/firefox:latest
#fi
#
#docker compose up -d
#docker ps -a

# Проверяем наличие локальных образов
echo "### Checking for local Docker images... ###"
declare -A images_to_check=(
  ["niffler-frontend"]="${PREFIX}/${FRONT}-docker:${FRONT_VERSION}"
  ["niffler-auth"]="${PREFIX}/niffler-auth-docker:latest"
  ["niffler-currency"]="${PREFIX}/niffler-currency-docker:latest"
  ["niffler-spend"]="${PREFIX}/niffler-spend-docker:latest"
  ["niffler-userdata"]="${PREFIX}/niffler-userdata-docker:latest"
  ["niffler-gateway"]="${PREFIX}/niffler-gateway-docker:latest"
  ["selenoid/chrome"]="selenoid/vnc_chrome:127.0"
  ["selenoid/firefox"]="selenoid/firefox:latest"
)

for image_name in "${!images_to_check[@]}"; do
  image="${images_to_check[$image_name]}"
  if [[ "$(docker images -q $image)" == "" ]]; then
    echo "### Image '$image' not found locally, building it... ###"
    if [[ "$image_name" == "selenoid/chrome" || "$image_name" == "selenoid/firefox" ]]; then
      docker pull "$image"
    else
      bash ./gradlew jibDockerBuild -x :niffler-e-2-e-tests:test
    fi
  else
    echo "### Image '$image' found locally, using it... ###"
  fi
done

# Запускаем контейнеры
echo "### Starting containers... ###"
docker compose up -d
docker ps -a