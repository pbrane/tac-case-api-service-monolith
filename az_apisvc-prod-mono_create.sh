#!/bin/bash

source ./monolith-prod.env
source ./tac-api-version.env

az containerapp create \
  --name "$AZ_CONTAINER_NAME" \
  --resource-group "$AZ_RESOURCE_GROUP" \
  --environment "$AZ_CONTAINERAPP_ENV" \
  --image "pbranestrategy/tac-case-api-service-mono:$TAC_API_VERSION" \
  --env-vars \
    API_ENV="$API_ENV" \
    SERVER_PORT="$SERVER_PORT" \
    DB_HOST="$DB_HOST" \
    DB_PORT="$DB_PORT" \
    API_SVC_ENV="$API_SVC_ENV" \
    AUTH_SVR_ENV="$AUTH_SVR_ENV" \
    AUTH_SVR_REPO="$AUTH_SVR_REPO" \
    CLIENT_ID="$CLIENT_ID" \
    CLIENT_SECRET="$CLIENT_SECRET" \
    CLIENT_NAME="$CLIENT_NAME" \
    PG_DATASOURCE_URL="$PG_DATASOURCE_URL" \
    PG_DATASOURCE_USERNAME="$PG_DATASOURCE_USERNAME" \
    PG_DATASOURCE_PASSWORD="$PG_DATASOURCE_PASSWORD" \
    ACCESS_TOKEN_EXPIRATION_MIN="$ACCESS_TOKEN_EXPIRATION_MIN" \
    REFRESH_TOKEN_EXPIRATION_MIN="$REFRESH_TOKEN_EXPIRATION_MIN" \
  --exposed-port 8080 \
  --target-port "$SERVER_PORT" \
  --transport auto \
  --ingress external \
  --min-replicas 1 \
  --max-replicas 3

