def match_face(features, index, user_mapping, threshold=0.6):
    D, I = index.search(np.array([features]).astype('float32'), k=1)
    if D[0][0] < threshold:
        return user_mapping.get(I[0][0], "Unknown")
    return None