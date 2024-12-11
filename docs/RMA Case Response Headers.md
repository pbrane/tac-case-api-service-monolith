Here is the documentation specifying the HTTP response headers for each endpoint in the `RmaCaseController`:

---

### Documentation: HTTP Response Headers for RMA Case Endpoints

---

### Endpoints

#### 1. **List All RMA Cases**
   ```
   GET /api/v1/rmaCases
   ```
- **Response Headers**:
    - `Content-Type`: `application/json`

---

#### 2. **Get RMA Case by ID**
   ```
   GET /api/v1/rmaCases/{id}
   ```
- **Response Headers**:
    - `Content-Type`: `application/json` (if found)
    - None (if `404 NOT FOUND`)

---

#### 3. **Create a New RMA Case**
   ```
   POST /api/v1/rmaCases
   ```
- **Response Headers**:
    - `Content-Type`: `application/json`
    - `Location`: The URL of the created resource (optional but recommended for RESTful APIs)

---

#### 4. **Update an RMA Case**
   ```
   PUT /api/v1/rmaCases/{id}
   ```
- **Response Headers**:
    - `Content-Type`: `application/json` (if successful)
    - None (if `404 NOT FOUND`)

---

#### 5. **Delete an RMA Case**
   ```
   DELETE /api/v1/rmaCases/{id}
   ```
- **Response Headers**:
    - `X-Message`: A custom header confirming the deletion, e.g., `"RMA Case ID: {id} deleted"`
    - None (other than `204 NO CONTENT`)

---

#### 6. **Upload Attachment**
   ```
   POST /api/v1/rmaCases/{id}/attachments
   ```
- **Response Headers**:
    - `Content-Type`: `application/json` (if successful)
    - None for `400 BAD REQUEST` or `500 INTERNAL SERVER ERROR`

---

#### 7. **Get All Attachments for an RMA Case**
   ```
   GET /api/v1/rmaCases/{id}/attachments
   ```
- **Response Headers**:
    - `Content-Type`: `application/json`

---

#### 8. **Get Attachment by ID**
   ```
   GET /api/v1/rmaCases/{caseId}/attachments/{attachmentId}
   ```
- **Response Headers**:
    - `Content-Type`: `application/json` (if found)
    - None (if `404 NOT FOUND`)

---

#### 9. **Delete Attachment**
   ```
   DELETE /api/v1/rmaCases/{caseId}/attachments/{attachmentId}
   ```
- **Response Headers**:
    - None (other than `204 NO CONTENT`)

---

#### 10. **Delete All Attachments for an RMA Case**
   ```
   DELETE /api/v1/rmaCases/{id}/attachments
   ```
- **Response Headers**:
    - None (other than `204 NO CONTENT`)

---

#### 11. **Download Attachment**
   ```
   GET /api/v1/rmaCases/{caseId}/attachments/{attachmentId}/download
   ```
- **Response Headers**:
    - `Content-Disposition`: Specifies the file download, e.g., `attachment; filename="file.txt"`
    - `Content-Type`: MIME type of the file, e.g., `application/pdf`, `image/png`

---

#### 12. **Upload Note**
   ```
   POST /api/v1/rmaCases/{id}/notes
   ```
- **Response Headers**:
    - `Content-Type`: `application/json` (if successful)
    - None for `400 BAD REQUEST` or `500 INTERNAL SERVER ERROR`

---

#### 13. **Get All Notes for an RMA Case**
   ```
   GET /api/v1/rmaCases/{id}/notes
   ```
- **Response Headers**:
    - `Content-Type`: `application/json`

---

#### 14. **Get Note by ID**
   ```
   GET /api/v1/rmaCases/{caseId}/notes/{noteId}
   ```
- **Response Headers**:
    - `Content-Type`: `application/json` (if found)
    - None (if `404 NOT FOUND`)

---

#### 15. **Delete Note**
   ```
   DELETE /api/v1/rmaCases/{caseId}/notes/{noteId}
   ```
- **Response Headers**:
    - None (other than `204 NO CONTENT`)

---

#### 16. **Delete All Notes for an RMA Case**
   ```
   DELETE /api/v1/rmaCases/{id}/notes
   ```
- **Response Headers**:
    - None (other than `204 NO CONTENT`)

---

#### 17. **Download Note**
   ```
   GET /api/v1/rmaCases/{caseId}/notes/{noteId}/download
   ```
- **Response Headers**:
    - `Content-Disposition`: Specifies the note file download, e.g., `attachment; filename="note.txt"`
    - `Content-Type`: MIME type of the note file, e.g., `text/plain`, `application/json`

---

This documentation assumes typical HTTP header behavior based on the provided controller code.