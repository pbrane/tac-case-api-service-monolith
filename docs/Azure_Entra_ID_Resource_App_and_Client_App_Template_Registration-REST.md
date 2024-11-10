
# Registering Resource and Client Template Application Using `az rest` with Microsoft Graph

This document provides instructions for registering a Resource Application (e.g., a Spring Boot REST API) 
and a Client Application Template in Azure AD using the `az rest` command with Microsoft Graph.

## Prerequisites
1. **Azure CLI**: Ensure Azure CLI is installed and updated to the latest version. You can install or update it from [Azure CLI Installation Guide](https://docs.microsoft.com/en-us/cli/azure/install-azure-cli).
2. **Sign In**: Sign in to your Azure AD tenant using the following command:
    ```bash
    az login --allow-no-subscriptions
    ```
3. **Verify Your Azure CLI Version**:
   Make sure you are using Azure CLI version 2.37.0 or later. Check your version using:
    ```bash
    az version
    ```
   This document was prepared using version 2.64.0.

## Step 1: Set the `{tenant-id}` as an Environment Variable

The `{tenant-id}` is the unique identifier of your Azure AD tenant. To find your tenant ID:

1. Run the following command to list your Azure AD tenants:
    ```bash
    az account tenant list --query "[].tenantId"
    ```

2. Copy the tenant ID that corresponds to your Azure AD tenant.

3. Set the `{tenant-id}` as an environment variable:
    ```bash
    export TENANT_ID="<your-tenant-id>"
    ```

## Step 2: Generate Unique IDs
**Ensure `uuidgen` is Installed**:
- The `uuidgen` command is typically available on Linux and macOS by default.
- For Windows, you may need to install a separate tool or use PowerShell's `New-Guid` command.

   ```bash
   export ROLE_ADMIN_ID=$(uuidgen)
   export ROLE_USER_ID=$(uuidgen)
   export SCOPE_READ_ID=$(uuidgen)
   export SCOPE_WRITE_ID=$(uuidgen)
   ```

## Step 3: Register the Resource Application (API)
### Set a variable to store the Resource application's (Spring Boot App) Display Name

```bash
export RESOURCE_APP_DISPLAY_NAME="TAC/RMA Case API"
```
The Resource Application represents your backend service (e.g., Spring Boot REST API) 
and defines the API permissions (scopes) that client applications can request.

### Create the Resource Application
#### Create the Resource Application (Spring Boot REST API)
```bash
az rest --method POST --uri "https://graph.microsoft.com/v1.0/applications" --body '{
        "displayName": "'$RESOURCE_APP_DISPLAY_NAME'",
        "identifierUris": ["api://'$TENANT_ID'/tac-case-api-service"],
        "signInAudience": "AzureADMyOrg",
        "api": {
            "requestedAccessTokenVersion": 2,
            "oauth2PermissionScopes": [
                {
                    "adminConsentDescription": "Allows the app to read cases in the API",
                    "adminConsentDisplayName": "Read Cases",
                    "id": "'$SCOPE_READ_ID'",
                    "isEnabled": true,
                    "type": "User",
                    "value": "read.cases"
                },
                {
                    "adminConsentDescription": "Allows the app to write cases in the API",
                    "adminConsentDisplayName": "Write Cases",
                    "id": "'$SCOPE_WRITE_ID'",
                    "isEnabled": true,
                    "type": "User",
                    "value": "write.cases"
                }
            ]
        },
        "appRoles": [
            {
                "allowedMemberTypes": ["User", "Application"],
                "description": "Admin role with full access to API",
                "displayName": "Admin",
                "id": "'$ROLE_ADMIN_ID'",
                "isEnabled": true,
                "value": "Admin"
            },
            {
                "allowedMemberTypes": ["User"],
                "description": "User role with limited access",
                "displayName": "User",
                "id": "'$ROLE_USER_ID'",
                "isEnabled": true,
                "value": "User"
            }
        ]
    }'
```

- **`identifierUris`**: Use the `$tenant_id` variable to specify the Application ID URI dynamically.
- **`oauth2PermissionScopes`**: Defines the API permissions (scopes) that client applications can request.
- **`appRoles`**: Defines roles that can be assigned to users or service principals.

### Create a Service Principal for the Resource Application
#### Get the Application ID of the Resource Application
```bash
export RESOURCE_APP_ID=$(az rest --method GET --uri "https://graph.microsoft.com/v1.0/applications?\$filter=displayName eq '$RESOURCE_APP_DISPLAY_NAME'" --query "value[0].appId" -o tsv)
```

#### Create a service principal for the Resource Application
```bash
az rest --method POST --uri "https://graph.microsoft.com/v1.0/servicePrincipals" --body '{
        "appId": "'$RESOURCE_APP_ID'"
    }'
```

#### Store the ID of the Resource Application's Service Principal
```bash
export RESOURCE_APP_SP_ID=$(az rest --method GET \
  --uri "https://graph.microsoft.com/v1.0/servicePrincipals?\$filter=appId eq '$RESOURCE_APP_ID'" \
  --query "value[0].id" -o tsv)
```
## Step 4: Register the Client Application Template

The Client Application Template is used by external users to create their own client applications in their tenants.

### Set a variable for the Client App Display Name
```bash
export CLIENT_APP_DISPLAY_NAME="TAC/RMA Case Client"
```

### Create the Client Application Template
```bash
az rest --method POST --uri "https://graph.microsoft.com/v1.0/applications" --body '{
        "displayName": "'$CLIENT_APP_DISPLAY_NAME'",
        "identifierUris": ["api://'$TENANT_ID'/tac-case-api-client"],
    "signInAudience": "AzureADMyOrg",
    "api": {
      "requestedAccessTokenVersion": 2
    },
    "web": {
      "redirectUris": [
        "http://localhost:8080/login/oauth2/code/"
      ],
      "implicitGrantSettings": {
        "enableIdTokenIssuance": true,
        "enableAccessTokenIssuance": true
      }
    }
  }'
```

### Configure API Permissions for the Client Application Template
The client application template must be configured to request permissions to the Resource Application. This involves updating the service principal with the required API scopes.

#### Get the App ID of the Client Application Template
```bash
export CLIENT_APP_ID=$(az rest --method GET --uri "https://graph.microsoft.com/v1.0/applications?\$filter=displayName eq '$CLIENT_APP_DISPLAY_NAME'" --query "value[0].appId" -o tsv)
```

#### Get the Object ID of the Resource Application (this Spring Boot App)
```bash
export RESOURCE_APP_OBJ_ID=$(az rest --method GET --uri "https://graph.microsoft.com/v1.0/applications?\$filter=displayName eq '$RESOURCE_APP_DISPLAY_NAME'" --query "value[0].id" -o tsv)
```

#### Get the Resource App Service Principal ID
```bash
export RESOURCE_APP_SP_ID=$(az rest --method GET \
  --uri "https://graph.microsoft.com/v1.0/servicePrincipals?\$filter=appId eq '$RESOURCE_APP_ID'" \
  --query "value[0].id" -o tsv)
```

#### Set a Service Principal for the Client App
```bash
az rest --method POST --uri "https://graph.microsoft.com/v1.0/servicePrincipals" --body '{
    "appId": "'$CLIENT_APP_ID'"
}'
```

#### Get the Service Principal ID
```bash
export CLIENT_APP_SP_ID=$(az rest --method GET \
  --uri "https://graph.microsoft.com/v1.0/servicePrincipals?\$filter=appId eq '$CLIENT_APP_ID'" \
  --query "value[0].id" -o tsv)
```

#### Assign API Permissions
```bash
az rest --method POST --uri "https://graph.microsoft.com/v1.0/servicePrincipals/$CLIENT_APP_ID/appRoleAssignments"     --body '{
        "principalId": "'$CLIENT_APP_SP_ID'",
        "resourceId": "'$RESOURCE_APP_SP_ID'",
        "appRoleId": "e1a9fc52-7cb1-4e8e-8eb7-5fc16d907c35" #Fixme (make a variable)
    }'
```

### Grant admin consent to the API permissions requested by the Client Application (Optional)
```bash
az rest --method POST --uri "https://graph.microsoft.com/v1.0/oauth2PermissionGrants" --body '{
    "clientId": "'$CLIENT_APP_SP_ID'",
    "consentType": "AllPrincipals",
    "principalId": null,
    "resourceId": "'$RESOURCE_APP_SP_ID'",
    "scope": "read.cases write.cases"
}'
```

## Key Points

- **Microsoft Graph API**: The `az rest` command is used to interact with Microsoft Graph API endpoints.
- **OAuth2 Permission Scopes**: Ensure that permission scopes are defined properly in the Resource Application.
- **Admin Consent**: Admin consent is required for clients to access the requested scopes.

## Summary
This document provides instructions for registering a Resource Application and a Client Application Templates 
using the `az rest` command with Microsoft Graph. 

It performs app registration, permission assignments, and admin consent using Microsoft Graph API endpoints.
