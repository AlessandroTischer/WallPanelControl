package com.axtx.wallpanelcontrol;


import static com.sys.gpio.gpioJni.ioctl_gpio;

public class RelaysController {

     public void setRelay(int number, boolean state) {
        ioctl_gpio(3 - number, 0, state ? 1 : 0); // 3 is the pin for relay 1, 0 is output mode, 1 is on
    }
    public boolean getRelay(int number) {
        return ioctl_gpio(3 - number, 1, 1) == 1; // 3 is the pin for relay 1, 1 is input mode
    }
}