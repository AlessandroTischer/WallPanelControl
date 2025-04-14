package com.axtx.wallpanelcontrol;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String SERVER_URL = "http://127.0.0.1:8080";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityResultLauncher<String> requestNotificationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        System.out.println("Notification permission granted");
                    } else {
                        System.out.println("Notification permission denied");
                    }
                }
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);

        // Label
        TextView label = new TextView(this);
        label.setText("WallPanel Control");
        layout.addView(label);

        // Color Picker Button
        Button colorPickerButton = new Button(this);
        colorPickerButton.setText("Choose LED Color");
        colorPickerButton.setOnClickListener(v -> openColorPicker());
        layout.addView(colorPickerButton);

        // Off Button
        Button offButton = new Button(this);
        offButton.setText("Turn Off LED");
        offButton.setOnClickListener(v -> sendHttpRequest("/setLED?color=0"));
        layout.addView(offButton);

        // Relay Toggles
        Switch relay1Switch = new Switch(this);
        relay1Switch.setText("Relay 1");
        relay1Switch.setOnCheckedChangeListener((buttonView, isChecked) -> 
            sendHttpRequest("/setRelay?relay=1&state=" + isChecked));
        layout.addView(relay1Switch);

        Switch relay2Switch = new Switch(this);
        relay2Switch.setText("Relay 2");
        relay2Switch.setOnCheckedChangeListener((buttonView, isChecked) -> 
            sendHttpRequest("/setRelay?relay=2&state=" + isChecked));
        layout.addView(relay2Switch);

        // IO Toggles
        Switch io1Switch = new Switch(this);
        io1Switch.setText("IO 1");
        io1Switch.setOnCheckedChangeListener((buttonView, isChecked) -> 
            sendHttpRequest("/setIO?IO=1&state=" + isChecked));
        layout.addView(io1Switch);

        Switch io2Switch = new Switch(this);
        io2Switch.setText("IO 2");
        io2Switch.setOnCheckedChangeListener((buttonView, isChecked) -> 
            sendHttpRequest("/setIO?IO=2&state=" + isChecked));
        layout.addView(io2Switch);

        Button serviceButton = new Button(this);
        updateServiceButton(serviceButton); // Imposta il testo iniziale del pulsante

        serviceButton.setOnClickListener(v -> {
            if (isServiceRunning(WallPanelService.class)) {
                // Ferma il servizio
                Intent stopIntent = new Intent(this, WallPanelService.class);
                stopService(stopIntent);

                // Mostra un AlertDialog per notificare l'utente
                new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Service Stopped")
                    .setMessage("The WallPanel service has been successfully stopped.")
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .show();
            } else {
                // Avvia il servizio
                Intent startIntent = new Intent(this, WallPanelService.class);
                startForegroundService(startIntent);

                // Mostra un AlertDialog per notificare l'utente
                new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Service Started")
                    .setMessage("The WallPanel service has been successfully started.")
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .show();
            }

            // Aggiorna il testo del pulsante
            updateServiceButton(serviceButton);
        });
        layout.addView(serviceButton);

        // Get Device IP Address
        String deviceIp = getDeviceIpAddress();

        // API Information
        TextView apiDescription = new TextView(this);
        apiDescription.setText(
            "Welcome to WallPanel Control!\n\n" +
            "This app allows you to control the device via HTTP requests.\n\n" +
            "To use the app, ensure that this device is connected to the same network as the controlling device.\n" +
            "You can control the LED color, relays, and IO states.\n\n" +
            "From now on, the app will now start and create an HTTP rest server as soon as the tablet is turned on, allowing you to control the device remotely.\n\n" +
            "API Information:\n" +
            "Base URL: http://" + deviceIp + ":8080\n\n" +
            "Available Endpoints:\n" +
            "1. /setLED?color=<HEX_COLOR> (GET): Set LED color (e.g., FF0000 for red).\n" +
            "   Response: 'LED color set to <color>' or 'Invalid color parameter'.\n\n" +
            "2. /setRelay?relay=<1|2>&state=<true|false> (GET): Set relay state.\n" +
            "   Response: 'Relay <number> set to <state>' or 'Invalid parameters'.\n\n" +
            "3. /setIO?IO=<1|2>&state=<true|false> (GET): Set IO state.\n" +
            "   Response: 'IO <number> set to <state>' or 'Invalid parameters'.\n\n" +
            "4. /getRelay?relay=<1|2> (GET): Get relay state.\n" +
            "   Response: 'true' or 'false'.\n\n" +
            "5. /getIO?IO=<1|2> (GET): Get IO state.\n" +
            "   Response: 'true' or 'false'.\n\n" +
            // "6. /getTemperatureHumidity (GET): Get temperature and humidity.\n" +
            // "   Response: JSON {\"temperature\": <value>, \"humidity\": <value>}.\n\n" +
            "Note: Device IP not detected. Replace <DEVICE_IP> with the IP address of the device."
        );
        apiDescription.setPadding(16, 16, 16, 16);
        layout.addView(apiDescription);

        setContentView(layout);
    }

    private void openColorPicker() {
        new ColorPickerDialog.Builder(this)
            .setTitle("Pick a Color")
            .setPreferenceName("ColorPickerDialog")
            .setPositiveButton("Confirm", new ColorEnvelopeListener() {
                @Override
                public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                    // Remove the alpha value (first 2 characters) from the hexadecimal color
                    String hexColor = envelope.getHexCode().substring(2); // Get only RGB
                    Log.d("ColorPicker", "Selected color (RGB): " + hexColor);
                    sendHttpRequest("/setLED?color=" + hexColor);
                }
            })
            .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
            .attachAlphaSlideBar(false) // Hide the alpha slider
            .attachBrightnessSlideBar(true) // Show the brightness slider
            .show();
    }

    private void sendHttpRequest(String endpoint) {
        new Thread(() -> {
            try {
                URL url = new URL(SERVER_URL + endpoint);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    System.out.println("Response: " + response.toString());
                } else {
                    System.out.println("HTTP error code: " + responseCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private String getDeviceIpAddress() {
        try {
            for (java.util.Enumeration<java.net.NetworkInterface> en = java.net.NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                java.net.NetworkInterface intf = en.nextElement();
                for (java.util.Enumeration<java.net.InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    java.net.InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof java.net.Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "<DEVICE_IP>"; // Fallback if no IP is found
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        android.app.ActivityManager manager = (android.app.ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (android.app.ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void updateServiceButton(Button button) {
        if (isServiceRunning(WallPanelService.class)) {
            button.setText("Stop WallPanel Service");
        } else {
            button.setText("Start WallPanel Service");
        }
    }
}