# airflow/dags/vera_daily_ingestion.py
from airflow import DAG
from airflow.operators.bash import BashOperator
from airflow.operators.python import PythonOperator
from datetime import datetime

def run_cleaning():
    import subprocess
    subprocess.run(["python", "scripts/data_cleaning.py"], check=True)

def run_training():
    import subprocess
    subprocess.run(["python", "ml/train_model.py"], check=True)

default_args = {
    "owner": "vera-data",
    "retries": 1,
}

with DAG(
    dag_id="vera_daily_ingestion",
    default_args=default_args,
    schedule_interval="@daily",
    start_date=datetime(2024, 1, 1),
    catchup=False,
) as dag:
    fetch_raw = BashOperator(
        task_id="fetch_raw",
        bash_command="python scripts/api_integration.py",
    )

    clean_data = PythonOperator(task_id="clean_data", python_callable=run_cleaning)
    train_model = PythonOperator(task_id="train_model", python_callable=run_training)

    fetch_raw >> clean_data >> train_model