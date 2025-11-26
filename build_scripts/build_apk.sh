#!/bin/bash

echo "Building Apocalypse Engine APK..."

# الانتقال لمجلد المشروع
cd android_app

# تنظيف المشروع
./gradlew clean

# بناء APK
./gradlew assembleDebug

# التحقق من وجود APK
if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
    echo "APK built successfully!"
    cp app/build/outputs/apk/debug/app-debug.apk ../payloads/malicious_payload.apk
    echo "APK copied to payloads folder"
else
    echo "APK build failed!"
    exit 1
fi

echo "Build process completed!"
