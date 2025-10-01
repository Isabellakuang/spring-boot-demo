from airflow import DAG
from airflow.operators.python import PythonOperator
from airflow.providers.postgres.hooks.postgres import PostgresHook
from airflow.providers.http.operators.http import SimpleHttpOperator
from datetime import datetime, timedelta
import pandas as pd
import requests

default_args = {
    'owner': 'data-team',
    'depends_on_past': False,
    'start_date': datetime(2025, 1, 1),
    'email_on_failure': True,
    'email_on_retry': False,
    'retries': 2,
    'retry_delay': timedelta(minutes=5),
}

dag = DAG(
    'etl_conversation_pipeline',
    default_args=default_args,
    description='ETL pipeline for conversation data',
    schedule_interval='@daily',
    catchup=False,
    tags=['etl', 'conversations', 'data-platform']
)

def extract_conversations(**context):
    """Extract: 从数据库提取对话数据"""
    hook = PostgresHook(postgres_conn_id='postgres_default')
    
    sql = """
        SELECT 
            c.id,
            c.subject,
            c.customer_email,
            c.status,
            c.started_at,
            COUNT(m.id) as message_count,
            MAX(m.sent_at) as last_message_at
        FROM conversations c
        LEFT JOIN conversation_messages m ON c.id = m.conversation_id
        WHERE DATE(c.started_at) = CURRENT_DATE - INTERVAL '1 day'
        GROUP BY c.id
    """
    
    df = hook.get_pandas_df(sql)
    
    # 保存到 XCom
    context['task_instance'].xcom_push(key='extracted_data', value=df.to_json())
    
    return f"Extracted {len(df)} conversations"

def transform_conversations(**context):
    """Transform: 数据清洗和转换"""
    import json
    
    # 从 XCom 获取数据
    extracted_json = context['task_instance'].xcom_pull(
        task_ids='extract', 
        key='extracted_data'
    )
    df = pd.read_json(extracted_json)
    
    # 数据转换
    df['response_time'] = (df['last_message_at'] - df['started_at']).dt.total_seconds() / 60
    df['status_category'] = df['status'].map({
        'OPEN': 'active',
        'IN_PROGRESS': 'active',
        'RESOLVED': 'closed',
        'CLOSED': 'closed'
    })
    
    # 异常值处理
    df = df[df['message_count'] > 0]
    df = df[df['response_time'] >= 0]
    
    # 保存转换后的数据
    context['task_instance'].xcom_push(key='transformed_data', value=df.to_json())
    
    return f"Transformed {len(df)} records"

def load_to_warehouse(**context):
    """Load: 加载数据到数据仓库"""
    import json
    
    transformed_json = context['task_instance'].xcom_pull(
        task_ids='transform',
        key='transformed_data'
    )
    df = pd.read_json(transformed_json)
    
    hook = PostgresHook(postgres_conn_id='warehouse_postgres')
    
    # 加载到数据仓库表
    df.to_sql(
        'fact_conversations_daily',
        hook.get_sqlalchemy_engine(),
        if_exists='append',
        index=False
    )
    
    return f"Loaded {len(df)} records to warehouse"

def trigger_rag_indexing(**context):
    """触发 RAG 索引更新"""
    transformed_json = context['task_instance'].xcom_pull(
        task_ids='transform',
        key='transformed_data'
    )
    df = pd.read_json(transformed_json)
    
    # 调用 ML Service 的 RAG 索引 API
    for _, row in df.iterrows():
        document = f"""
        Conversation ID: {row['id']}
        Subject: {row['subject']}
        Customer: {row['customer_email']}
        Status: {row['status']}
        Message Count: {row['message_count']}
        """
        
        try:
            response = requests.post(
                'http://ml-service:8001/api/rag/index',
                json={
                    'document': document,
                    'metadata': {
                        'conversation_id': int(row['id']),
                        'customer_email': row['customer_email'],
                        'indexed_at': datetime.now().isoformat()
                    },
                    'doc_id': f"conv_{row['id']}"
                }
            )
            response.raise_for_status()
        except Exception as e:
            print(f"Error indexing conversation {row['id']}: {str(e)}")
    
    return f"Indexed {len(df)} conversations to RAG"

# 定义任务
extract_task = PythonOperator(
    task_id='extract',
    python_callable=extract_conversations,
    dag=dag
)

transform_task = PythonOperator(
    task_id='transform',
    python_callable=transform_conversations,
    dag=dag
)

load_task = PythonOperator(
    task_id='load',
    python_callable=load_to_warehouse,
    dag=dag
)

rag_index_task = PythonOperator(
    task_id='rag_indexing',
    python_callable=trigger_rag_indexing,
    dag=dag
)

# 定义依赖关系
extract_task >> transform_task >> [load_task, rag_index_task]