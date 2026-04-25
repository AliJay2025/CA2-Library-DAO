📦 Library Management System – Stage 3
👥 Group Members
Abdihafid Gahayr (D00283863)
Ali Jabril (D00283862)
✅ Stage 3 Requirements Implemented
Feature	Description	Status
F17	Binary Schema Extension (BLOB + metadata columns)	✅
F18	Binary File Upload (Base64 encode/decode)	✅
F19	Binary File Retrieval (download and save)	✅
F20	File Metadata Query (no BLOB)	✅
F21	Disconnect / Exit Protocol	✅
F22	JUnit 5 Unit Tests	✅
🗄️ F17: Binary Schema

Added the following columns to the member table:

Column	Type
file_name	VARCHAR(255)
content_type	VARCHAR(100)
file_size	INT
profile_image	LONGBLOB
📤 F18: File Upload
Process
Client reads file using Files.readAllBytes()
Encodes file to Base64
Sends UPLOAD request with metadata
Server decodes Base64
Stores file using PreparedStatement.setBytes()
Request Example
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
📥 F19: File Download
Process
Client sends DOWNLOAD request with member ID
Server retrieves BLOB using ResultSet.getBytes()
Server encodes file to Base64
Client decodes and saves file to downloads/ folder
Response Example
{
  "status": "success",
  "message": "File retrieved successfully",
  "data": {
    "fileName": "profile.png",
    "fileSize": 12345,
    "fileData": "iVBORw0KGgo..."
  }
}
📊 F20: Metadata Query

Returns file information without retrieving the BLOB:

{
  "status": "success",
  "data": {
    "fileName": "profile.png",
    "fileSize": 12345,
    "contentType": "image/png",
    "hasImage": true
  }
}
🔌 F21: Disconnect Protocol

Client sends before closing connection:

{
  "requestType": "DISCONNECT"
}

Server responds with a "Goodbye" message and releases the thread.

🧪 F22: Unit Tests
Test Results
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
✅ Summary
Feature	Status
F17 – Binary Schema	✅
F18 – File Upload	✅
F19 – File Download	✅
F20 – Metadata Query	✅
F21 – Disconnect	✅
F22 – Unit Tests	✅ (16 passing)