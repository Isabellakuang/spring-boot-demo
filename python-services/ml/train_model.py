# ml/train_model.py
import joblib
import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.ensemble import RandomForestClassifier
from sklearn.pipeline import Pipeline
from sklearn.metrics import classification_report

def train(path: str = "clean/conversations.parquet"):
    df = pd.read_parquet(path)
    X_train, X_test, y_train, y_test = train_test_split(
        df["initial_message"], df["category"], test_size=0.2, random_state=42
    )

    model = Pipeline(
        steps=[
            ("tfidf", TfidfVectorizer(max_features=5000)),
            ("clf", RandomForestClassifier(n_estimators=200)),
        ]
    )
    model.fit(X_train, y_train)

    preds = model.predict(X_test)
    print(classification_report(y_test, preds))

    joblib.dump(model, "ml/models/vera_rf.joblib")

if __name__ == "__main__":
    train()