# Recipe Catalog

## About

Collection of recipes

## Prerequisites

- Java 8
- Scala SDK
- Gradle

## Quick start

```
# Run tests
./gradlew clean recipe-catalog-app:test

# Bundle the project
./gradlew clean recipe-catalog-app:shadowjar

# Run the project
./gradlew clean recipe-catalog-app:run
```

## Usage

### GET /api/v1/recipes
Retrieve all recipes

## Build Docker Images

### Application
```
cd recipe-catalog-app

# Build the bundled jar
./gradlew clean shadowJar

# Build the docker image 
docker build . -t recipecatalog

# Run the docker image
docker run recipecatalg:latest
```

### Database
```
cd recipe-catalog-database 

# Build the DB image
docker build . -t mongodb

# Run the docker image
docker run -d -p 27017:27017 -v ~/mongodb/data:/data/db -t mongodb
```

## Swagger API 
When the application is running on localhost, call the following endpoints to view the json and yaml version of the service's openAPI doc:
 
``` 
localhost:8080/api-docs/swagger.json
localhost:8080/api-docs/swagger.yaml

# UI endpoint
http://localhost:8080/swagger-ui/index.html#/
```
