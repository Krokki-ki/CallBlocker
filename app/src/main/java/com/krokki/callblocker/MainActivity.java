package com.krokki.callblocker;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private Button startStopButton;
    private boolean isServiceRunning = false;
    private static final int PERMISSIONS_REQUEST_CODE = 101;
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CONTACTS
    };
    private static final String initialPermissions = "Для корректной работы приложения необходимо разрешить доступ к Контактам и Файлам";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startStopButton = findViewById(R.id.startStopButton);
        setupButton();
        checkPermissions();
    }

    private void setupButton() {
        startStopButton.setOnClickListener(v -> {
            if (hasAllPermissions()) {
                toggleService();
            } else {
                checkPermissions();
            }
        });
    }

    private void toggleService() {
        Intent serviceIntent = new Intent(this, CallBlockerService.class);
        if (isServiceRunning) {
            stopService(serviceIntent);
            startStopButton.setText("Старт");
            startStopButton.setBackgroundColor(getResources().getColor(R.color.green));
        } else {
            startService(serviceIntent);
            startStopButton.setText("Стоп");
            startStopButton.setBackgroundColor(getResources().getColor(R.color.red));
        }
        isServiceRunning = !isServiceRunning;
    }

    private boolean hasAllPermissions() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void checkPermissions() {
        if (!hasAllPermissions()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (hasAllPermissions()) {
                toggleService();
            } else {
                Toast.makeText(this, initialPermissions, Toast.LENGTH_LONG).show();
            }
        }
    }
}