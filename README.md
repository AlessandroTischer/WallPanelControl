# WallPanelControl

WallPanelControl is an Android application designed to control an android conference room display via HTTP requests. The app allows you to manage LEDs, relays, input/output (IO) states. It includes an integrated REST server to receive commands from remote devices.

It can be used with the [Home Assistant Integration](https://github.com/AlessandroTischer/HA-WallPanelControl) or with any other service!

### Key Features

- **LED Control**: Change the LED color using a color picker.
- **Relay Management**: Turn relays on or off using toggles.
- **IO Management**: Control the state of digital inputs/outputs.
<!-- - **Temperature and Humidity Monitoring**: Display real-time data. -->
- **REST Server**: Receive HTTP commands to control the device.
- **Auto Start**: The service starts automatically when the device boots.

---

### Requirements

- **Android SDK**: Minimum version 26 (Android 8.0 Oreo).
- **Permissions Required**:
  - `INTERNET`: For HTTP communication.
  - `FOREGROUND_SERVICE`: To run the service in the foreground.
  - `RECEIVE_BOOT_COMPLETED`: To start the service when the device boots.
  - `POST_NOTIFICATIONS`: For ongoing notification.

---

## Compatible devices

The app was developed with a closed source java library sent by the store I bought the tablet from. I know for sure this library works on many models, but I cannot guarantee it will work on yours.

The tablet I'm using is [this](https://www.alibaba.com/product-detail/9-7-Inch-Android-Intelligent-Central_1601238011512.html?spm=a2700.shop_plgr.41413.23.72e57121hUKIkI), which is often called SMT97. You can send me other model numbers once you checked are working creating a post [here](https://community.home-assistant.io/t/support-for-leds-on-meeting-room-tablets/804178/5), and I'll add them as supported.

### Supported Devices

| Device Model | Status    |
|--------------|-----------|
| SMT97        | Supported |
| SMT101       | Supported |

---

## Installation

Simply download the APK from the [Releases](https://github.com/AlessandroTischer/WallPanelControl/releases) page and install it on the tablet, or compile it yourself using the source code and Android Studio.
