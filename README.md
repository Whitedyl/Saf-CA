# LockTalk - Secure Chat Application

A multi-threaded chat application with JWT authentication, AES encryption, and MongoDB storage.

## 🔒 Security Features

1. **JWT Authentication** - Stateless tokens with HMAC256 signing, 24-hour expiration
2. **Password Encryption** - AES-encrypted passwords, never stored in plain text
3. **AES Message Encryption** - All messages encrypted with AES-256 before transmission
4. **HMAC Message Integrity** - Verifies messages haven't been tampered with
5. **Secure MongoDB Connection** - Encrypted connections, credentials in `.env` file
6. **Thread-Safe Architecture** - CopyOnWriteArrayList, separate thread per client

## 📋 Functionality

- User registration and login with JWT tokens
- Real-time encrypted messaging between multiple clients
- Message history sent to new clients when they join
- Automatic message broadcasting to all connected users
- Session persistence via JWT token file storage

## 🚀 Setup & Run

### 1. Configure `.env` File

Create `.env` in the `ca1_group_k` directory:

```env
JWT_SECRET_KEY=your_secret_key_here
MONGODB_CONNECTION_STRING=mongodb+srv://username:password@cluster.mongodb.net/?appName=YourApp
MONGODB_DATABASE_NAME=YourDatabaseName
AES_SECRET_KEY=MTIzNDU2Nzg5MDEyMzQ1Ng==
HMAC_SECRET_KEY=your_super_secret_key_at_least_32_characters
```

### 2. Run Server

```bash
cd ca1_group_k
mvn compile exec:java -Dexec.mainClass="nci.security_fundamentals.server.ChatServer"
```

### 3. Run Client(s)

```bash
cd ca1_group_k
mvn compile exec:java -Dexec.mainClass="nci.security_fundamentals.client.Main"
```

### Usage

1. Choose Register (1) or Login (2)
2. Enter credentials
3. Start sending encrypted messages

**Prerequisites**: Java 21+, Maven 3.6+, MongoDB Atlas account