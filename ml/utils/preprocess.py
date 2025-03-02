import cv2
import numpy as np

def extract_face_features(image, face_cascade, is_file=True):
    if is_file:
        img = cv2.imread(image)
    else:
        img = image
    
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    faces = face_cascade.detectMultiScale(gray, scaleFactor=1.1, minNeighbors=5, minSize=(30, 30))
    
    if len(faces) == 0:
        return None
    
    # Use the first detected face
    (x, y, w, h) = faces[0]
    face = gray[y:y+h, x:x+w]
    
    # Resize to a fixed size and flatten as a simple feature vector
    face_resized = cv2.resize(face, (64, 64))  # Reduced size for efficiency
    features = face_resized.flatten() / 255.0  # Normalize
    return features[:128]  # Truncate/pad to match FAISS dimension (128)