package com.krokki.callblocker;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class CallBlockerService extends Service {
    private static final String TAG = "CallBlockerService";
    private TelephonyManager telephonyManager;
    private PhoneStateListener phoneStateListener;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Инициализация сервиса");

        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        registerCallListener();
    }

    private void registerCallListener() {
        phoneStateListener = new PhoneStateListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onCallStateChanged(int state, String phoneNumber) {
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    handleIncomingCall(phoneNumber);
                }
            }
        };

        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void handleIncomingCall(String phoneNumber) {
        if (!ContactUtils.isContact(getApplicationContext(), phoneNumber)) {
            blockCall(phoneNumber);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void blockCall(String phoneNumber) {
        try {
            TelecomManager telecomManager = (TelecomManager) getSystemService(TELECOM_SERVICE);
            if (telecomManager != null && telecomManager.isInCall()) {
                telecomManager.endCall();
                logBlockedCall(phoneNumber);
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка блокировки: " + e.getMessage());
        }
    }

    private void logBlockedCall(String phoneNumber) {
        Log.d(TAG, "Заблокирован вызов от: " + phoneNumber);
        // TODO: Реализовать добавление в Blacklist
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (telephonyManager != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}