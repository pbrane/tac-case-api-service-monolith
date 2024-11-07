
# Azure Container Apps Deployment Guide

This README provides step-by-step instructions for building, testing, and deploying a Spring Boot application using Azure Spring Apps.

## Table of Contents
- [Azure Cloud Setup](#azure-cloud-setup)
    - [Create Azure Account](#create-azure-account)
    - [Create a Resource Group](#create-a-resource-group)
    - [Install the Azure CLI](#install-the-azure-cli)
    - [Login with the CLI](#login-with-the-cli)
    - [Set the Subscription](#set-the-subscription)
    - [Create the Azure Container App Environment](#create-the-azure-container-app-environment)
    - [Now, deploy the container](#now-deploy-the-container)

## Azure Cloud Setup

### Create Azure Account

Create an Azure Account and Subscription Tier using the browser. For this example, refer to the following:

- [Azure Pricing Options](https://azure.microsoft.com/en-us/pricing/purchase-options/azure-account)

### Create a Resource Group

Create an Azure Resource Group to contain the Azure Spring Apps services. This example uses the Resource Group: **"MolexProject"**.

- [Step-by-Step Guide: Creating an Azure Resource Group](https://techcommunity.microsoft.com/t5/startups-at-microsoft/step-by-step-guide-creating-an-azure-resource-group-on-azure/ba-p/3792368)

### Install the Azure CLI

Install the Azure CLI by following the instructions provided here:

- [Azure CLI Installation Guide](https://learn.microsoft.com/en-us/cli/azure/install-azure-cli)

### Login with the CLI

```bash
az login
```

### Set the Subscription

```bash
az account set --subscription <your-subscription-id>
```

## Create the Azure Container App Environment

### A few variables
```bash
export ENV_NAME=demo-environment
export RESOURCE_GROUP=MolexProject
export AZURE_LOCATION=westus
export CONTAINER_NAME=client-api-demo
export CONTAINER_IMG=pbranestrategy/tac-case-api-service:0.0.5
export API_PORT=8080
```

```bash
az containerapp env create \
  --name $ENV_NAME \
  --resource-group $RESOURCE_GROUP \
  --location $AZURE_LOCATION
```

## Deploy the Container

```bash
az containerapp create \
  --name $CONTAINER_NAME \
  --resource-group $RESOURCE_GROUP \
  --environment $ENV_NAME \
  --image $CONTAINER_IMG \
  --target-port $API_PORT \
  --ingress external \
  --min-replicas 1 \
  --max-replicas 3
```

## Update the Container
When you need to update the current running container with a new version, in this case, (0.0.4):
```bash
export CONTAINER_IMG=pbranestrategy/tac-case-api-service:0.0.5
az containerapp update --name client-api-demo --resource-group MolexProject --image $CONTAINER_IMG
```