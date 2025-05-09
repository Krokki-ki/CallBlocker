package com.krokki.callblocker;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;

public class CallBlockerService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Логика блокировки звонков будет здесь
        return START_STICKY; // Сервис перезапустится, если его убьёт система
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // Для сервисов без привязки
    }
}