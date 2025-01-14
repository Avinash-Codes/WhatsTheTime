# WhatsTheTime

<div align="center">
  <p float="left">
    <img src="https://github.com/user-attachments/assets/20d73b70-9a02-470f-9869-907ee65294b2" width="250" />
    <img src="https://github.com/user-attachments/assets/88394a54-075f-4a05-a08b-190f891b2ea3" width="250" />
  </p>
</div>

<div align="center">
  <p float="left">
    <img src="https://github.com/user-attachments/assets/126caf59-0989-4d04-80f6-19eb589f96f6" width="500" />
  </p>
</div>

WhatsTheTime is a cross-platform application that helps users keep track of time across different cities around the world. The app provides a user-friendly interface to view the current time in various time zones and offers additional features such as analog and digital clock displays.


https://github.com/user-attachments/assets/15d752fa-d75d-4fc8-a691-07efd332d922


## Features

1. **Home Screen**: Displays a list of cities with their respective current times. Users can add or remove cities from this list.
2. **City Search Bar**: Allows users to search for cities and add them to their list.
3. **Analog Watch**: Provides an analog clock display for the selected city.
4. **Digital Clock**: Displays the current time in a digital format for the selected city.
5. **Settings**: Allows users to configure app settings such as time format (12-hour or 24-hour) and theme (light or dark mode).

## Architecture

This app follows the Model-View-ViewModel (MVVM) architecture to ensure a clear separation of concerns and maintainability.

## Data Source

The app uses the World Time API to fetch the current time for different cities:
[World Time API](https://worldtimeapi.org/)

## Libraries Used

|      Use      |     Library    |
| ------------- | ------------- |
|  Navigation  |    Jetpack Compose Navigation    |
|  Data Store  | AndroidX DataStore  |
|   HTTP Client/Serialization  | Retrofit  |
|   ViewModel  | AndroidX ViewModel  |

## Setup Instructions

### Prerequisites

> **Warning**
> You need a Mac with macOS to write and run iOS-specific code on simulated or real devices. This is an Apple requirement.

To work with this project, you need the following:

* A machine running a recent version of macOS
* [Xcode](https://apps.apple.com/us/app/xcode/id497799835)
* [Android Studio](https://developer.android.com/studio)
* The [Kotlin Multiplatform Mobile plugin](https://plugins.jetbrains.com/plugin/14936-kotlin-multiplatform-mobile)
* The [CocoaPods dependency manager](https://kotlinlang.org/docs/native-cocoapods.html)

### Running on Android

#### Using an Emulator

1. Ensure you have an Android virtual device available. Otherwise, [create one](https://developer.android.com/studio/run/managing-avds#createavd).
2. In Android Studio, select `androidApp` from the list of run configurations.
3. Choose your virtual device and click **Run**.

#### Using Gradle

To install the Android application on a real Android device or emulator, run the following command in the terminal:

```bash
./gradlew installDebug
```

### Running on iOS

#### Using a Simulator

1. In Android Studio, select **Edit Configurations** from the run configurations menu.
2. Navigate to **iOS Application** > **iosApp**.
3. In the **Execution target** list, select your target device and click **OK**.
4. Select the `iosApp` run configuration and click **Run** next to your virtual device.

#### Using a Real Device

To run the app on a real iOS device, you need the following:

* The `TEAM_ID` associated with your [Apple ID](https://support.apple.com/en-us/HT204316)
* The iOS device registered in Xcode

Follow the steps in the [official guide](https://developer.apple.com/documentation/xcode/running-your-app-on-a-device) to set up your device.

## License

This project is licensed under the MIT License. See the LICENSE file for more details.

