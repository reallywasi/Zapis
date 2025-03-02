from flask import Flask, request, jsonify
from flask_cors import CORS
import cv2
import numpy as np
import faiss
import os
from PIL import Image
import base64
from io import BytesIO
from utils.preprocess import extract_face_features
from utils.postprocess import match_face

app = Flask(__name__)
CORS(app)  # Enable CORS for Spring Boot integration

# Load Haar Cascade for face detection with absolute path
cascade_path = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", "backend", "src", "main", "resources", "haarcascade_frontalface_default.xml"))
if not os.path.exists(cascade_path):
    raise FileNotFoundError(f"Haar cascade file not found at: {cascade_path}")
face_cascade = cv2.CascadeClassifier(cascade_path)
if face_cascade.empty():
    raise ValueError("Failed to load Haar cascade classifier")

# Initialize FAISS index (FlatL2 for simplicity, optimized for CPU)
dimension = 128  # Feature vector size
index = faiss.IndexFlatL2(dimension)
user_mapping = {}  # {index_id: username}
profile_images = {}  # {username: feature_vector}

# Load profile images into FAISS index
def load_profile_images():
    upload_dir = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", "backend", "uploads"))
    if not os.path.exists(upload_dir):
        print(f"Upload directory not found: {upload_dir}")
        return
    for filename in os.listdir(upload_dir):
        if filename.endswith("_profile.jpg"):
            username = filename.split("_profile.jpg")[0].replace("_", "@")
            img_path = os.path.join(upload_dir, filename)
            features = extract_face_features(img_path, face_cascade)
            if features is not None:
                index_id = index.ntotal
                index.add(np.array([features]).astype('float32'))
                user_mapping[index_id] = username
                profile_images[username] = features

@app.route('/load_faces', methods=['POST'])
def load_faces():
    load_profile_images()
    return jsonify({"message": f"Loaded {index.ntotal} faces into FAISS index"}), 200

@app.route('/recognize', methods=['POST'])
def recognize_face():
    data = request.get_json()
    if 'image' not in data:
        return jsonify({"error": "No image provided"}), 400
    
    # Decode base64 image
    img_data = base64.b64decode(data['image'].split(',')[1])
    img = Image.open(BytesIO(img_data))
    img_cv = cv2.cvtColor(np.array(img), cv2.COLOR_RGB2BGR)
    
    # Extract features from captured image
    features = extract_face_features(img_cv, face_cascade, is_file=False)
    if features is None:
        return jsonify({"error": "Face not detected"}), 400
    
    # Search in FAISS index
    D, I = index.search(np.array([features]).astype('float32'), k=1)
    if D[0][0] < 0.6:  # Threshold for similarity
        username = user_mapping.get(I[0][0], "Unknown")
        return jsonify({"username": username}), 200
    else:
        return jsonify({"error": "Face not recognized"}), 404

if __name__ == '__main__':
    load_profile_images()  # Load faces on startup
    app.run(host='0.0.0.0', port=5000, debug=True)