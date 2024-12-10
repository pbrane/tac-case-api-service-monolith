Here is the documentation specifying the required HTTP headers for each endpoint in the provided `TacCaseController` class.

---

### Documentation: HTTP Header Requirements

#### General Requirements
- **Authorization Header**: All endpoints are assumed to require an `Authorization` header in the format:
  ```
  Authorization: Bearer <access_token>
  ```
  unless otherwise specified.

#### Endpoints

1. **List All TAC Cases**
   ```
   GET /api/v1/tacCases
   ```
    - **Headers**:
        - `Authorization`: Required for authentication.

2. **Get TAC Case by ID**
   ```
   GET /api/v1/tacCases/{id}
   ```
    - **Headers**:
        - `Authorization`: Required for authentication.

3. **Create a New TAC Case**
   ```
   POST /api/v1/tacCases
   ```
    - **Headers**:
        - `Authorization`: Required for authentication.
        - `Content-Type`: `application/json` (request body is in JSON format).

4. **Update a TAC Case**
   ```
   PUT /api/v1/tacCases/{id}
   ```
    - **Headers**:
        - `Authorization`: Required for authentication.
        - `Content-Type`: `application/json` (request body is in JSON format).

5. **Delete a TAC Case**
   ```
   DELETE /api/v1/tacCases/{id}
   ```
    - **Headers**:
        - `Authorization`: Required for authentication.
        - `X-Message`: Custom header used to confirm case deletion (returned in response).

6. **List All RMAs for a TAC Case**
   ```
   GET /api/v1/tacCases/{id}/rmaCases
   ```
    - **Headers**:
        - `Authorization`: Required for authentication.

7. **Upload Attachment**
   ```
   POST /api/v1/tacCases/{id}/attachments
   ```
    - **Headers**:
        - `Authorization`: Required for authentication.
        - `Content-Type`: `multipart/form-data` (file uploads).

8. **Get All Attachments**
   ```
   GET /api/v1/tacCases/{id}/attachments
   ```
    - **Headers**:
        - `Authorization`: Required for authentication.

9. **Get Attachment by ID**
   ```
   GET /api/v1/tacCases/{caseId}/attachments/{attachmentId}
   ```
    - **Headers**:
        - `Authorization`: Required for authentication.

10. **Delete Attachment**
    ```
    DELETE /api/v1/tacCases/{caseId}/attachments/{attachmentId}
    ```
    - **Headers**:
        - `Authorization`: Required for authentication.

11. **Delete All Attachments**
    ```
    DELETE /api/v1/tacCases/{id}/attachments
    ```
    - **Headers**:
        - `Authorization`: Required for authentication.

12. **Download Attachment**
    ```
    GET /api/v1/tacCases/{caseId}/attachments/{attachmentId}/download
    ```
    - **Headers**:
        - `Authorization`: Required for authentication.
        - `Content-Disposition`: Returned in the response to specify the file download.

13. **Upload Note**
    ```
    POST /api/v1/tacCases/{id}/notes
    ```
    - **Headers**:
        - `Authorization`: Required for authentication.
        - `Content-Type`: `application/json` (request body is in JSON format).

14. **Get All Notes**
    ```
    GET /api/v1/tacCases/{id}/notes
    ```
    - **Headers**:
        - `Authorization`: Required for authentication.

15. **Get Note by ID**
    ```
    GET /api/v1/tacCases/{caseId}/notes/{noteId}
    ```
    - **Headers**:
        - `Authorization`: Required for authentication.

16. **Delete Note**
    ```
    DELETE /api/v1/tacCases/{caseId}/notes/{noteId}
    ```
    - **Headers**:
        - `Authorization`: Required for authentication.

17. **Delete All Notes**
    ```
    DELETE /api/v1/tacCases/{id}/notes
    ```
    - **Headers**:
        - `Authorization`: Required for authentication.

18. **Download Note**
    ```
    GET /api/v1/tacCases/{caseId}/notes/{noteId}/download
    ```
    - **Headers**:
        - `Authorization`: Required for authentication.
        - `Content-Disposition`: Returned in the response to specify the note download.

---

This documentation assumes all endpoints use Bearer token-based authentication.