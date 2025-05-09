package com.krokki.callblocker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
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

        // Инициализация UI
        startStopButton = findViewById(R.id.startStopButton);
        setupButton(); // Это было пропущено!

        // Запрос разрешений для Android 10+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestPhoneStatePermission();
        }
    }

    private void requestPhoneStatePermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    PERMISSIONS_REQUEST_CODE
            );
        }
    }

    private void setupButton() {
        startStopButton.setOnClickListener(v -> {
            if (hasAllPermissions()) {
                toggleService();
            } else {
                requestAllPermissions();
            }
        });
    }

    private void requestAllPermissions() {
        ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                PERMISSIONS_REQUEST_CODE
        );
    }

    private void toggleService() {
        Intent serviceIntent = new Intent(this, CallBlockerService.class);
        if (isServiceRunning) {
            stopService(serviceIntent);
            updateButtonUi(false);
        } else {
            startService(serviceIntent);
            updateButtonUi(true);
        }
        isServiceRunning = !isServiceRunning;
    }

    private void updateButtonUi(boolean isRunning) {
        startStopButton.setText(isRunning ? "Стоп" : "Старт");
        startStopButton.setBackgroundColor(
                getResources().getColor(
                        isRunning ? R.color.red : R.color.green
                )
        );
    }

    private boolean hasAllPermissions() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
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