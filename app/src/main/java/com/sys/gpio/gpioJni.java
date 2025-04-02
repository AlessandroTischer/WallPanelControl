package com.sys.gpio;

/**
 * Created by hyman_y on 27/11/2019.
         gpioJni.ioctl_gpio(1,0,0);//设置gpio1  低电平
         gpioJni.ioctl_gpio(0,0,0);//设置gpio0  低电平
         gpioJni.ioctl_gpio(1,0,1);//设置gpio1  高电平
         gpioJni.ioctl_gpio(0,0,1);//设置gpio0  高电平
        value = gpioJni.ioctl_gpio(1,1,1);//获取gpio1电平状态  返回value，1高电平0低电平
        Log.d("goio0","value="+value);
        value = gpioJni.ioctl_gpio(0,1,1);//获取gpio0电平状态
        Log.d("gpio1","value="+value);
 */

public class gpioJni {
    public static native int ioctl_gpio(int gpio_num,int cmd,int value);
    static {
        System.loadLibrary("gpio_control");
    }
}
