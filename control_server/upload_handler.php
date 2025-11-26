<?php
$upload_dir = "uploads/";

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    if (isset($_FILES['file'])) {
        $file_type = $_POST['type'] ?? 'unknown';
        $file_name = $_FILES['file']['name'];
        $file_tmp = $_FILES['file']['tmp_name'];
        
        // إنشاء مجلد للنوع
        $type_dir = $upload_dir . $file_type . '/';
        if (!is_dir($type_dir)) {
            mkdir($type_dir, 0777, true);
        }
        
        // إنشاء اسم فريد للملف
        $timestamp = date('Ymd_His');
        $new_filename = $timestamp . '_' . $file_name;
        $destination = $type_dir . $new_filename;
        
        if (move_uploaded_file($file_tmp, $destination)) {
            echo "File uploaded successfully";
            
            // تسجيل في ملف log
            $log = date('Y-m-d H:i:s') . " - $file_type: $new_filename\n";
            file_put_contents('upload_log.txt', $log, FILE_APPEND);
        } else {
            echo "Upload failed";
        }
    } else {
        echo "No file received";
    }
} else {
    echo "Invalid request method";
}
?>
