package com.axtx.wallpanelcontrol;

import static com.example.elcapi.jnielc.ledseek;
import static com.example.elcapi.jnielc.seekstart;
import static com.example.elcapi.jnielc.seekstop;

public class LEDController {



    private static final int seek_red = 0xa1;
    private static final int seek_green = 0xa2;
    private static final int seek_blue = 0xa3;
    public native void ledoff();

    private void setColor(int red, int green, int blue) {
        seekstart();
        ledseek(seek_red, red);
        ledseek(seek_green, green);
        ledseek(seek_blue, blue);
        seekstop();
    }

    public void setColor(int color) {
        int red = ((color >> 16) & 0xFF) / 16;
        int green = ((color >> 8) & 0xFF) / 16;
        int blue = (color & 0xFF) / 16;
        setColor(red, green, blue);
    }
}