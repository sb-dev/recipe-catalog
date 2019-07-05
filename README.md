# Recipe Catalog

## About

## Prerequisites

## Quick start

Start database:

```
docker build . -t mongodb
docker run -d -p 27017:27017 -v ~/mongodb/data:/data/db -t mongodb
```

## Usage

### GET /api/v1/recipes
Retrieve all recipes

## Swagger API 
When the application is running on localhost, call the following endpoints to view the json and yaml version of the service's openAPI doc:
 
``` 
localhost:8080/api-docs/swagger.json
localhost:8080/api-docs/swagger.yaml
```
