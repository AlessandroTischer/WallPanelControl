package com.axtx.wallpanelcontrol;

import static com.sys.gpio.gpioJni.ioctl_gpio;

public class IOController {
    public void setIO(int number, boolean state) {
        ioctl_gpio(number, 0, state ? 1 : 0); // 0 is the pin for IO 1, 0 is output mode, 1 is on
    }
    public boolean getIO(int number) {
        return ioctl_gpio(number, 1, 1) == 1; // 0 is the pin for IO 1, 1 is input mode
    }

}
