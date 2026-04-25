📚 Library Management System – Stage 1, 2 & 3
👥 Group Members
Abdihafid Gahayr (D00283863)
Ali Jabril (D00283862)
🏗️ System Architecture
📖 What Each Layer Does
Layer	What It Contains	Simple Explanation
Presentation Layer	Main.java	CLI menu interface
Service Layer	Methods in Main.java	Business logic
Data Access Layer	dao/ + jdbc/	Database communication
Database	library_database	MySQL with 5 tables
JSON Protocol	JsonUtil.java	Converts objects ↔ JSON
✅ Stage 1 Features (F1–F9)
Option	Feature	Description
1	F3: Get All Members	Retrieve all members
2	F4: Get Member by ID	Retrieve member by ID
3	F6: Insert Member	Add new member
4	F7: Update Member	Update member details
5	F8: Filter (Predicate)	Filter members
6	F9: JSON Conversion	Convert to/from JSON
7	F5: Delete Member	Delete member
0	Exit	Exit program
🗄️ Database Tables
Table	Description
member	Members (id, name, address, phone)
book	Books
category	Book categories
shelf	Book locations
staff	Staff members
🛠️ Running Stage 1
Start MySQL (XAMPP)
Import database (sql/create_db.sql)
Run Main.java
Use menu options
📁 Project Structure
src/main/java/com/library/
├── Main.java
├── dao/
├── jdbc/
├── domain/
├── db/
├── json/
├── server/
├── client/
└── model/
📡 Stage 2 – Client-Server System
🚀 Features (F10–F16)
Feature	Description	Status
F10	Multithreaded Server	✅
F11	ServerResponse Wrapper	✅
F12	Get Operations	✅
F13	Insert	✅
F14	Delete	✅
F15	Update	✅
F16	Error Handling	✅
📡 Communication Protocol
Request Format
{
  "requestType": "REQUEST_TYPE",
  "id": 1,
  "data": {}
}
Response Format
{
  "status": "success",
  "message": "Operation completed",
  "data": {}
}
📋 Request Types
Request	Description
GET_ALL_MEMBERS	Get all
GET_MEMBER_BY_ID	Get by ID
INSERT_MEMBER	Insert
UPDATE_MEMBER	Update
DELETE_MEMBER	Delete
❌ Error Example
{
  "status": "error",
  "message": "Member not found",
  "data": null
}
🛠️ Running Stage 2
Run LibraryServer.java
Run LibraryClient.java
Use menu options
💾 Stage 3 – Binary File Handling & Testing
🔧 F17: Database Extension

Added support for file storage:

file_name
content_type
file_size
profile_image (LONGBLOB)
ALTER TABLE member ADD COLUMN file_name VARCHAR(255) NOT NULL DEFAULT '';
ALTER TABLE member ADD COLUMN content_type VARCHAR(100) NOT NULL DEFAULT '';
ALTER TABLE member ADD COLUMN file_size INT NOT NULL DEFAULT 0;
ALTER TABLE member ADD COLUMN profile_image LONGBLOB;
📤 F18: File Upload
Process
Read file → bytes
Encode Base64
Send to server
Decode + store in DB
Example
{
  "requestType": "UPLOAD",
  "payload": {
    "entityId": 1,
    "fileName": "profile.png",
    "contentType": "image/png",
    "fileSize": 12345,
    "fileData": "BASE64..."
  }
}
📥 F19: File Download
Process
Request by ID
Retrieve BLOB
Encode Base64
Save locally
📊 F20: Metadata Query

Returns:

filename
size
type
hasImage
🔌 F21: Disconnect
{
  "requestType": "DISCONNECT"
}
🧪 F22: Unit Tests
Results
16 tests passed, 0 failures
Categories
Category	Result
JSON Tests	✅
DAO Tests	✅
▶️ Running Stage 3
mvn exec:java -Dexec.mainClass=com.library.server.LibraryServer
mvn exec:java -Dexec.mainClass=com.library.client.LibraryClient
📋 Final Feature Summary
Feature	Status
Stage 1 (CRUD + JSON)	✅
Stage 2 (Client-Server)	✅
Stage 3 (File Handling)	✅
Unit Tests	✅
🔗 GitHub Repository

https://github.com/AliJay2025/CA2-Library-DAO

📅 Deadlines
Stage 1: ✅ Completed
Stage 2: ✅ Completed
Stage 3: ✅ Completed