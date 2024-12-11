Here is the documentation specifying the required HTTP headers for each endpoint in the `RmaCaseController` class:

---

### Documentation: HTTP Header Requirements for RMA Case Endpoints

#### General Requirements
- **Authorization Header**: All endpoints are assumed to require an `Authorization` header in the format:
  ```
  Authorization: Bearer <access_token>
  ```
  unless otherwise specified.

---

### Endpoints

#### 1. **List All RMA Cases**
   ```
   GET /api/v1/rmaCases
   ```
- **Headers**:
    - `Authorization`: Required for authentication.

---

#### 2. **Get RMA Case by ID**
   ```
   GET /api/v1/rmaCases/{id}
   ```
- **Headers**:
    - `Authorization`: Required for authentication.

---

#### 3. **Create a New RMA Case**
   ```
   POST /api/v1/rmaCases
   ```
- **Headers**:
    - `Authorization`: Required for authentication.
    - `Content-Type`: `application/json` (the request body is in JSON format).

---

#### 4. **Update an RMA Case**
   ```
   PUT /api/v1/rmaCases/{id}
   ```
- **Headers**:
    - `Authorization`: Required for authentication.
    - `Content-Type`: `application/json` (the request body is in JSON format).

---

#### 5. **Delete an RMA Case**
   ```
   DELETE /api/v1/rmaCases/{id}
   ```
- **Headers**:
    - `Authorization`: Required for authentication.
    - `X-Message`: Custom header used to confirm case deletion (returned in response).

---

#### 6. **Upload Attachment**
   ```
   POST /api/v1/rmaCases/{id}/attachments
   ```
- **Headers**:
    - `Authorization`: Required for authentication.
    - `Content-Type`: `multipart/form-data` (file uploads).

---

#### 7. **Get All Attachments for an RMA Case**
   ```
   GET /api/v1/rmaCases/{id}/attachments
   ```
- **Headers**:
    - `Authorization`: Required for authentication.

---

#### 8. **Get Attachment by ID**
   ```
   GET /api/v1/rmaCases/{caseId}/attachments/{attachmentId}
   ```
- **Headers**:
    - `Authorization`: Required for authentication.

---

#### 9. **Delete Attachment**
   ```
   DELETE /api/v1/rmaCases/{caseId}/attachments/{attachmentId}
   ```
- **Headers**:
    - `Authorization`: Required for authentication.

---

#### 10. **Delete All Attachments for an RMA Case**
   ```
   DELETE /api/v1/rmaCases/{id}/attachments
   ```
- **Headers**:
    - `Authorization`: Required for authentication.

---

#### 11. **Download Attachment**
   ```
   GET /api/v1/rmaCases/{caseId}/attachments/{attachmentId}/download
   ```
- **Headers**:
    - `Authorization`: Required for authentication.
    - `Content-Disposition`: Returned in the response to specify the file download.

---

#### 12. **Upload Note**
   ```
   POST /api/v1/rmaCases/{id}/notes
   ```
- **Headers**:
    - `Authorization`: Required for authentication.
    - `Content-Type`: `application/json` (the request body is in JSON format).

---

#### 13. **Get All Notes for an RMA Case**
   ```
   GET /api/v1/rmaCases/{id}/notes
   ```
- **Headers**:
    - `Authorization`: Required for authentication.

---

#### 14. **Get Note by ID**
   ```
   GET /api/v1/rmaCases/{caseId}/notes/{noteId}
   ```
- **Headers**:
    - `Authorization`: Required for authentication.

---

#### 15. **Delete Note**
   ```
   DELETE /api/v1/rmaCases/{caseId}/notes/{noteId}
   ```
- **Headers**:
    - `Authorization`: Required for authentication.

---

#### 16. **Delete All Notes for an RMA Case**
   ```
   DELETE /api/v1/rmaCases/{id}/notes
   ```
- **Headers**:
    - `Authorization`: Required for authentication.

---

#### 17. **Download Note**
   ```
   GET /api/v1/rmaCases/{caseId}/notes/{noteId}/download
   ```
- **Headers**:
    - `Authorization`: Required for authentication.
    - `Content-Disposition`: Returned in the response to specify the note download.

---

This documentation assumes all endpoints use Bearer token-based authentication.