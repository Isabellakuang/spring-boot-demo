# scripts/api_integration.py
import requests
import pandas as pd

API_URL = "https://api.partner.com/v1/conversations"

def fetch_conversations(page: int = 1) -> pd.DataFrame:
    response = requests.get(API_URL, params={"page": page}, timeout=10)
    response.raise_for_status()
    data = response.json()["items"]
    return pd.DataFrame(data)

if __name__ == "__main__":
    frames = [fetch_conversations(page) for page in range(1, 6)]
    pd.concat(frames).to_csv("raw_conversations.csv", index=False)