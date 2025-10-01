# scripts/data_cleaning.py
import pandas as pd

def load_and_clean(path: str) -> pd.DataFrame:
    df = pd.read_csv(path)
    df["created_at"] = pd.to_datetime(df["created_at"], errors="coerce")
    df = df.dropna(subset=["customer_email", "subject"])
    df["subject"] = df["subject"].str.strip().str.lower()
    return df

if __name__ == "__main__":
    df_clean = load_and_clean("raw_conversations.csv")
    df_clean.to_parquet("clean/conversations.parquet")