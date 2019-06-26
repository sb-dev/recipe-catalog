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
