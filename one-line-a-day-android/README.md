# One Line a Day - Android Application

## Overview
This is the Android client for the One Line a Day application. It is built using modern Android development practices, including MVVM architecture, Room for offline persistence, and Retrofit for network synchronization.

## Features
- **Offline-First**: Uses Room Database to cache entries, enabling full functionality without internet.
- **Biometric Security**: Protects your journal with Fingerprint/FaceID login.
- **Search**: integrated search for local and remote entries.
- **Stats Dashboard**: Visualize your writing habits.
- **Dark Mode**: Fully supported via DayNight theme.

## Architecture
- **MVVM (Model-View-ViewModel)**: Separates UI logic from business logic.
- **Repository Pattern**: Mediates data operations between the API (Retrofit) and Local DB (Room).

### Key Libraries
- **Retrofit 2**: REST API client.
- **Room**: SQLite object mapping.
- **androidx.security:security-crypto**: For EncryptedSharedPreferences (secure token storage).
- **Material Components**: For modern UI elements.

## Setup & Run
1. **Prerequisites**: Android Studio Hedgehog or later, JDK 17.
2. **Configuration**:
   - Open `Constants.java` (if applicable) or check `build.gradle` to ensure the API URL points to your running backend (default: `http://10.0.2.2:8080` for emulator).
3. **Build**:
   ```bash
   ./gradlew assembleDebug
   ```
4. **Run**: Select an emulator or connected device and run via Android Studio.

## Testing
- Unit tests: located in `src/test/java`
- Instrumented tests: located in `src/androidTest/java`
