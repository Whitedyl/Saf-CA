# Saf-CA - Secure File Transfer System

A Maven-based secure file transfer application developed for the Security Fundamentals module at NCI. This project implements multiple security features including JWT authentication, encrypted messaging, and secure file exchange using MongoDB for storage.

## Project Overview

This application provides a comprehensive secure communication and file transfer platform with focus on cryptographic security principles. The system enables users to securely exchange messages, images, and files with end-to-end encryption.

## Features

### Core Functionalities

- **JWT Authentication**: Token-based authentication system for secure user sessions
- **Image Password Protection**: Secure image-based password authentication mechanism
- **Encrypted Messaging**: Real-time encrypted message exchange between users
- **Encrypted Image Exchange**: Secure transmission and storage of encrypted images
- **File Storage System**: Encrypted file upload, storage, and retrieval
- **MongoDB Database**: Complete database integration for storing users, messages, and files
- **Datagram Encryption**: UDP datagram encryption for messages and file transfers
- **GUI Interface**: User-friendly Java-based graphical interface

### Security Features

- End-to-end encryption for all communications
- JWT-based session management
- Secure image handling and encryption
- MongoDB encrypted document storage
- Network packet encryption using datagrams

## Technology Stack

- **Language**: Java 21
- **Build Tool**: Maven
- **Database**: MongoDB
- **Authentication**: JWT (java-jwt 4.5.0)
- **Database Driver**: MongoDB Driver Sync 5.2.0
- **GUI**: Java Swing/AWT (to be confirmed)
- **Additional**: Python integration (planned for login and encrypted image features)


## Prerequisites

- Java 21 or higher
- Maven 3.6+
- MongoDB instance (local or remote)
- (Optional) Python 3.x for extended features

## Installation

1. Clone the repository:
```bash
git clone https://github.com/Whitedyl/Saf-CA.git
cd Saf-CA/ca1_group_k
```

2. Install dependencies:
```bash
mvn clean install
```

3. Configure MongoDB connection in the application properties

4. Run the application:
```bash
mvn exec:java
```

## Dependencies

```xml
<!-- JWT Authentication -->
<dependency>
    <groupId>com.auth0</groupId>
    <artifactId>java-jwt</artifactId>
    <version>4.5.0</version>
</dependency>

<!-- MongoDB Driver -->
<dependency>
    <groupId>org.mongodb</groupId>
    <artifactId>mongodb-driver-sync</artifactId>
    <version>5.2.0</version>
</dependency>
```

## Python Integration (Planned)

Python components will be integrated for:
- Enhanced login authentication
- Advanced encrypted image processing
- Machine learning-based security features

## Security Considerations

⚠️ **Important**: This is an educational project for the Security Fundamentals module. Ensure proper security practices are followed in production environments:

- Never commit sensitive credentials
- Use environment variables for configuration
- Regularly update dependencies
- Follow secure coding practices
- Implement proper key management

## Module Information

**Module**: Security Fundamentals  
**Institution**: National College of Ireland (NCI)  
**Group**: Group K  
**Project**: CA1 Assignment

## License

This project is developed for educational purposes as part of the Security Fundamentals module at NCI.

## Contributors

- Group K Members

## Development Status

🚧 **In Active Development** - This project is currently under development as part of the CA1 assignment.

---

*Last Updated: October 2025*
