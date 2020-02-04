docker-compose down
mvn clean install
docker build -t feed-json-transformer ./feed-json-transformer-app/
docker build -t feed-data-store ./feed-data-store/
docker-compose up