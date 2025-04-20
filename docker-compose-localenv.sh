#!/bin/bash

# Указываем явно какой compose-файл использовать
COMPOSE_FILE="docker-compose_localenv.yml"

# Загружаем переменные окружения
source ./docker.properties
export PROFILE=docker
export PREFIX="${IMAGE_PREFIX}"
export FRONT_VERSION="2.1.0"
export ARCH=$(uname -m)

echo '### Java version ###'
java --version

# Определяем фронтенд (обычный или gql)
if [[ "$1" = "gql" ]]; then
  export FRONT="niffler-ng-gql-client"
else
  export FRONT="niffler-ng-client"
fi

# Останавливаем и удаляем только сервисы из нашего compose-файла
echo "### Stopping and removing containers defined in $COMPOSE_FILE ###"
docker compose -f $COMPOSE_FILE down

# Очищаем все связанные с проектом контейнеры и образы
echo "### Cleaning up all niffler containers and images ###"
docker_containers=$(docker ps -a -q --filter "label=com.docker.compose.project=niffler")
docker_images=$(docker images --format '{{.Repository}}:{{.Tag}}' | grep 'niffler')

if [ ! -z "$docker_containers" ]; then
  echo "### Stop containers: $docker_containers ###"
  docker stop $docker_containers
  docker rm $docker_containers
fi

if [ ! -z "$docker_images" ]; then
  echo "### Remove images: $docker_images ###"
  docker rmi $docker_images
fi

# Собираем проект
bash ./gradlew clean
if [ "$1" = "push" ] || [ "$2" = "push" ]; then
  echo "### Build & push images ###"
  bash ./gradlew jib -x :niffler-e-2-e-tests:test
  docker compose -f $COMPOSE_FILE push frontend.niffler.dc
else
  echo "### Build images ###"
  bash ./gradlew jibDockerBuild -x :niffler-e-2-e-tests:test
fi

# Запускаем сервисы
echo "### Starting services from $COMPOSE_FILE ###"
docker compose -f $COMPOSE_FILE up -d

# Показываем статус сервисов
echo "### Current containers status ###"
docker compose -f $COMPOSE_FILE ps