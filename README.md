# One Line a Day 📝

> **Capture your life, one line at a time.**

**One Line a Day** is a modern, minimalist daily journaling application designed to help you build a consistent writing habit. With a seamless offline-first mobile app and a robust synchronization backend, your memories are always safe and accessible.

---

## ✨ Key Features

### 📱 Android App
*   **Offline-First Architecture**: Write entries without internet. Data syncs automatically when online using a robust Room Database + Repository pattern.
*   **Smart Timeline**: View your memories in a continuous scrolling timeline.
*   **Search**: Instantly find past entries with local + network hybrid search.
*   **Biometric Security**: Protect your journal with Fingerprint or FaceID (via `EncryptedSharedPreferences`).
*   **Stats Dashboard**: Track your writing streaks and total word counts.
*   **Multi-Language Support**: Fully localized for English 🇺🇸, Polish 🇵🇱, and Spanish 🇪🇸.
*   **Dark Mode**: Optimized for late-night journaling.

### ☁️ Backend API
*   **Secure Authentication**: JWT-based stateless authentication with Spring Security.
*   **Data Synchronization**: RESTful API endpoints for syncing journal entries.
*   **Analytics**: Custom event tracking for user engagement (e.g., `app_opened`, `entry_saved`).
*   **Robust Error Handling**: Global exception handling with standard HTTP status codes (401, 403, 404).
*   **Database Migrations**: Flyway integration for reliable schema versioning.

---

## 🛠️ Tech Stack

### Android (Client)
*   **Language**: Java, XML
*   **Architecture**: MVVM-ish (Activity + Repository Pattern)
*   **Database**: Room (SQLite Abstraction)
*   **Network**: Retrofit 2 + OkHttp
*   **Security**: AndroidX Biometric, EncryptedSharedPreferences (Jetpack Security)
*   **Build**: Gradle

### Spring Boot (Server)
*   **Language**: Java 17
*   **Framework**: Spring Boot 3.2
*   **Database**: H2 (Dev) / PostgreSQL (Prod ready)
*   **Security**: Spring Security + JWT
*   **Documentation**: Swagger UI / OpenAPI
*   **Build**: Maven

---

## 🚀 Getting Started

### Prerequisites
*   Java JDK 17
*   Android Studio Iguana or newer
*   Git

### 1. Backend Setup
The backend powers the synchronization and authentication.

```bash
cd one-line-a-day
./mvnw spring-boot:run
```
*   The server will start at `http://localhost:8080`.
*   **Swagger UI**: `http://localhost:8080/swagger-ui.html`
*   **Default User**: Register a new user via the App or Swagger.

### 2. Android Setup
The mobile app connects to the backend.

1.  Open `one-line-a-day-android` in Android Studio.
2.  Update `ApiClient.java` (or `res/values/strings.xml` if configured) with your computer's IP address if running on a physical device.
    *   *Emulator default*: `http://10.0.2.2:8080` is pre-configured.
3.  Build and Run on Emulator or Device.

```bash
cd one-line-a-day-android
# Build Debug APK
gradle assembleDebug
```

---

## 🔒 Security & Privacy
*   **Data Encryption**: Auth tokens are stored in `EncryptedSharedPreferences`.
*   **Biometrics**: Optional biometric lock ensures only YOU can access your journal.
*   **Traffic**: All API communication is designed for HTTPS (configured for HTTP in dev).

---

## 🤝 Contributing
1.  Fork the repository.
2.  Create a feature branch (`git checkout -b feature/amazing-feature`).
3.  Commit your changes (`git commit -m 'Add amazing feature'`).
4.  Push to the branch.
5.  Open a Pull Request.

---

## 📄 License
Distributed under the MIT License. See `LICENSE` for more information.
