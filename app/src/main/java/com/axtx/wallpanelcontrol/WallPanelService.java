package com.axtx.wallpanelcontrol;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class WallPanelService extends Service {

    private LEDController ledController;
    private RelaysController relaysController;
    private IOController ioController;
    private RestServer restServer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("WallPanelService", "onCreate");

        NotificationChannel channel = new NotificationChannel(
                "wallpanel_service_channel",
                "WallPanel Service Channel",
                NotificationManager.IMPORTANCE_LOW
        );

        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(channel);
            Log.d("WallPanelService", "Notification channel created");
        }

        Notification notification = new NotificationCompat.Builder(this, "wallpanel_service_channel")
                .setContentTitle("WallPanel Service")
                .setContentText("Il servizio è in esecuzione...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
                .setOngoing(true)
                .build();

        startForeground(1, notification);
        Log.d("WallPanelService", "Notification started");


        ledController = new LEDController();
        //ledController.ledoff();
        relaysController = new RelaysController();
        ioController = new IOController();
        try {
            restServer = new RestServer(8080);
            restServer.start();
            Log.d("WallPanelService", "RestServer started on port 8080");
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (restServer != null) {
            restServer.stop();
        }
    }

    private class RestServer extends NanoHTTPD {

        public RestServer(int port) {
            super(port);
        }

        @Override
        public Response serve(IHTTPSession session) {
            String uri = session.getUri();
            Method method = session.getMethod();
            Map<String, List<String>> params = session.getParameters();

            if (Method.GET.equals(method)) {
                switch (uri) {
                    case "/setLED":
                        Log.d("WallPanelService", "setLED called");
                        int color;
                        try {
                            color = Integer.parseInt(Objects.requireNonNull(params.getOrDefault("color", List.of("0"))).get(0), 16);
                        } catch (NumberFormatException e) {
                            return newFixedLengthResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "Invalid color parameter");
                        }
                        ledController.setColor(color);
                        return newFixedLengthResponse("LED color set to " + color);
                    case "/setRelay": {
                        Log.d("WallPanelService", "setRelay called");
                        boolean state;
                        int number;
                        try {
                            state = Boolean.parseBoolean(Objects.requireNonNull(params.getOrDefault("state", List.of("false"))).get(0));
                            number = Integer.parseInt(Objects.requireNonNull(params.getOrDefault("relay", List.of("0"))).get(0)) - 1;
                            if (number < 0 || number > 1) {
                                return newFixedLengthResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "Invalid relay number");
                            }
                        } catch (Exception e) {
                            return newFixedLengthResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "Invalid parameters");
                        }
                        relaysController.setRelay(number, state);
                        return newFixedLengthResponse("Relay " + number + "set to " + state);
                    }
                    case "/setIO": {
                        Log.d("WallPanelService", "setIO called");
                        boolean state;
                        int number;
                        try {
                            state = Boolean.parseBoolean(Objects.requireNonNull(params.getOrDefault("state", List.of("false"))).get(0));
                            number = Integer.parseInt(Objects.requireNonNull(params.getOrDefault("IO", List.of("0"))).get(0)) - 1;
                            if (number < 0 || number > 1) {
                                return newFixedLengthResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "Invalid IO number");
                            }
                        } catch (Exception e) {
                            return newFixedLengthResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "Invalid parameters");
                        }
                        ioController.setIO(number, state);
                        return newFixedLengthResponse("IO " + number + " set to " + state);
                    }
                    case "/getRelay": {
                        Log.d("WallPanelService", "getRelay called");
                        int number;
                        try {
                            number = Integer.parseInt(Objects.requireNonNull(params.getOrDefault("relay", List.of("0"))).get(0)) - 1;
                            if (number < 0 || number > 1) {
                                return newFixedLengthResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "Invalid relay number");
                            }
                        } catch (Exception e) {
                            return newFixedLengthResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "Invalid parameters");
                        }
                        boolean state = relaysController.getRelay(number);
                        return newFixedLengthResponse(String.valueOf(state));
                    }
                    case "/getIO": {
                        Log.d("WallPanelService", "getIO called");
                        int number;
                        try {
                            number = Integer.parseInt(Objects.requireNonNull(params.getOrDefault("IO", List.of("0"))).get(0)) - 1;
                            if (number < 0 || number > 1) {
                                return newFixedLengthResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "Invalid IO number");
                            }
                        } catch (Exception e) {
                            return newFixedLengthResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "Invalid parameters");
                        }
                        boolean state = ioController.getIO(number);
                        return newFixedLengthResponse(String.valueOf(state));
                    }
                }
            }
            return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Not Found");
        }
    }
}