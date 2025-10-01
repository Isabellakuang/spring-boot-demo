# ml/infer.py
import joblib

model = joblib.load("ml/models/vera_rf.joblib")

def predict_category(text: str) -> str:
    return model.predict([text])[0]