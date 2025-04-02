package com.axtx.wallpanelcontrol;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private int ledColor = 0;
    private boolean relay1State = false;
    private boolean relay2State = false;
    private boolean io1State = false;
    private boolean io2State = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityResultLauncher<String> requestNotificationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        System.out.println("Permesso notifiche concesso");
                    } else {
                        System.out.println("Permesso notifiche negato");
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

        TextView ledControlText = new TextView(this);
        ledControlText.setText("LED Control");
        layout.addView(ledControlText);

        LinearLayout ledButtons = new LinearLayout(this);
        ledButtons.setOrientation(LinearLayout.HORIZONTAL);

        Button redButton = new Button(this);
        redButton.setText("Red");
        redButton.setOnClickListener(v -> ledColor = 0xFF0000);
        ledButtons.addView(redButton);

        Button greenButton = new Button(this);
        greenButton.setText("Green");
        greenButton.setOnClickListener(v -> ledColor = 0x00FF00);
        ledButtons.addView(greenButton);

        Button blueButton = new Button(this);
        blueButton.setText("Blue");
        blueButton.setOnClickListener(v -> ledColor = 0x0000FF);
        ledButtons.addView(blueButton);

        layout.addView(ledButtons);

        TextView relayControlText = new TextView(this);
        relayControlText.setText("Relay Control");
        layout.addView(relayControlText);

        Switch relay1Switch = new Switch(this);
        relay1Switch.setText("Relay 1");
        relay1Switch.setChecked(relay1State);
        relay1Switch.setOnCheckedChangeListener((buttonView, isChecked) -> relay1State = isChecked);
        layout.addView(relay1Switch);

        Switch relay2Switch = new Switch(this);
        relay2Switch.setText("Relay 2");
        relay2Switch.setChecked(relay2State);
        relay2Switch.setOnCheckedChangeListener((buttonView, isChecked) -> relay2State = isChecked);
        layout.addView(relay2Switch);

        TextView ioControlText = new TextView(this);
        ioControlText.setText("IO Control");
        layout.addView(ioControlText);

        Switch io1Switch = new Switch(this);
        io1Switch.setText("IO 1");
        io1Switch.setChecked(io1State);
        io1Switch.setOnCheckedChangeListener((buttonView, isChecked) -> io1State = isChecked);
        layout.addView(io1Switch);

        Switch io2Switch = new Switch(this);
        io2Switch.setText("IO 2");
        io2Switch.setChecked(io2State);
        io2Switch.setOnCheckedChangeListener((buttonView, isChecked) -> io2State = isChecked);
        layout.addView(io2Switch);

        Button startServiceButton = new Button(this);
        startServiceButton.setText("Start WallPanel Service");
        startServiceButton.setOnClickListener(v -> {
            Intent serviceIntent = new Intent(this, WallPanelService.class);
            startForegroundService(serviceIntent);
        });
        layout.addView(startServiceButton);


        setContentView(layout);
    }
}