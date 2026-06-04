# SensorLogger

A functional Android application designed to log and record sensor data from the device. This project is optimized for modern Android development and includes a automated CI/CD pipeline for generating builds.

## Features

- **Real-time Logging**: Captures data from available device sensors.
- **Automated Builds**: Integrated GitHub Actions for continuous integration.
- **Clean Architecture**: Built using Kotlin and modern Gradle configurations.

## Getting Started

### Prerequisites

- [Android Studio Iguana](https://developer.android.com/studio) or newer.
- JDK 17.
- An Android device or emulator running API 24 or higher.

### Installation

1. Clone the repository:
   `ash
   git clone https://github.com/[your-username]/SensorLogger.git
   `
2. Open the project in Android Studio.
3. Sync the project with Gradle files.

## Development

### Building via CLI

To build the debug APK locally on Windows:
`powershell
.\gradlew.bat assembleDebug
`

The resulting APK will be located at pp\build\outputs\apk\debug\app-debug.apk.

## CI/CD Pipeline

This project uses **GitHub Actions** to automate the build process. Every push to the master or main branches triggers a workflow that:
1. Sets up the Windows build environment.
2. Configures Java 17.
3. Compiles the project using the Gradle wrapper.
4. Uploads the final APK as a downloadable GitHub artifact.

## License

This project is licensed under the MIT License - see the LICENSE file for details.
