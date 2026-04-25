markdown
# Library Management System - Stage 3

## Group Members
- Abdihafid Gahayr (D00283863)
- Ali Jabril (D00283862)

---

## ✅ Stage 3 Requirements Implemented

| Feature | Description | Status |
|---------|-------------|--------|
| **F17** | Binary Schema Extension (BLOB + metadata columns) | ✅ |
| **F18** | Binary File Upload (Base64 encode/decode) | ✅ |
| **F19** | Binary File Retrieval (download and save) | ✅ |
| **F20** | File Metadata Query (no BLOB) | ✅ |
| **F21** | Disconnect / Exit Protocol | ✅ |
| **F22** | JUnit 5 Unit Tests | ✅ |

---

## F17: Binary Schema

Added to `member` table in MySQL:

| Column | Type |
|--------|------|
| `file_name` | VARCHAR(255) |
| `content_type` | VARCHAR(100) |
| `file_size` | INT |
| `profile_image` | LONGBLOB |

---

## F18: File Upload

**Process:**
1. Client reads file → `Files.readAllBytes()`
2. Encodes to Base64
3. Sends `UPLOAD` request with metadata
4. Server decodes Base64
5. Stores in DB via `PreparedStatement.setBytes()`

**Request Example:**
```json
{
  "requestType": "UPLOAD",
  "payload": {
    "entityId": 1,
    "fileName": "profile.png",
    "contentType": "image/png",
    "fileSize": 12345,
    "fileData": "iVBORw0KGgo..."
  }
}
F19: File Download
Process:

Client sends DOWNLOAD request with ID

Server retrieves BLOB via ResultSet.getBytes()

Encodes to Base64

Client decodes and saves to downloads/ folder

Response Example:

json
{
  "status": "success",
  "message": "File retrieved successfully",
  "data": {
    "fileName": "profile.png",
    "fileSize": 12345,
    "fileData": "iVBORw0KGgo..."
  }
}
F20: Metadata Query
Returns file information WITHOUT fetching the BLOB:

json
{
  "status": "success",
  "data": {
    "fileName": "profile.png",
    "fileSize": 12345,
    "contentType": "image/png",
    "hasImage": true
  }
}
F21: Disconnect
Client sends before closing:

json
{ "requestType": "DISCONNECT" }
Server responds with "Goodbye" and releases thread.

F22: Unit Tests
Test Results
text
JsonUtilTest: 9 tests passed
MemberDaoTest: 7 tests passed
Total: 16 tests passed, 0 failures
Test Categories
Category	Test Method	Result
JSON Round-trip	memberToJson_andBack_returnsEqualMember	✅
JSON List	memberListToJson_andBack_returnsEqualList	✅
DAO Insert	insert_validMember_returnsPositiveId	✅
DAO Find	findById_returnsOptionalForExistingId	✅
DAO Update	update_existingMember_updatesValues	✅
DAO Delete	deleteById_withInvalidId_returnsFalse	✅
Run Tests
bash
mvn test
🚀 How to Run Stage 3
1. Start MySQL (XAMPP)
Start MySQL in XAMPP Control Panel

2. Run Server
bash
mvn exec:java -Dexec.mainClass=com.library.server.LibraryServer
3. Run Client
bash
mvn exec:java -Dexec.mainClass=com.library.client.LibraryClient
4. Test Features
Menu Option	Feature
1	Get All Members
2	Get Member by ID
3	Insert Member
4	Update Member
5	Delete Member
6	Upload Profile Image (F18)
7	Download Profile Image (F19)
8	Get File Metadata (F20)
0	Exit (F21)
📁 Project Structure
text
src/main/java/com/library/
├── client/
│   └── LibraryClient.java
├── server/
│   ├── LibraryServer.java
│   ├── ClientHandler.java
│   └── RequestDispatcher.java
├── dao/
│   ├── MemberDao.java
│   └── DaoRegistry.java
├── jdbc/
│   └── JdbcMemberDao.java
├── domain/
│   └── Member.java
├── shared/
│   ├── ClientRequest.java
│   ├── RequestType.java
│   ├── ServerResponse.java
│   └── FileUploadPayload.java
├── json/
│   └── JsonUtil.java
└── db/
    └── DatabaseConnection.java
🔗 GitHub Repository
https://github.com/AliJay2025/CA2-Library-DAO/tree/stage-3

✅ Summary
Feature	Status
F17 - Binary Schema	✅ Complete
F18 - File Upload	✅ Complete
F19 - File Download	✅ Complete
F20 - Metadata Query	✅ Complete
F21 - Disconnect	✅ Complete
F22 - Unit Tests	✅ Complete (16 passing)