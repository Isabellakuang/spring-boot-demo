from flask import Flask, request, jsonify
import pandas as pd
import numpy as np
from typing import Dict, List
import logging
from datetime import datetime

app = Flask(__name__)
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

@app.route('/health', methods=['GET'])
def health_check():
    return jsonify({"status": "healthy", "service": "data-processing"})

@app.route('/api/data/process', methods=['POST'])
def process_data():
    """
    处理数据：清洗、转换、聚合
    """
    try:
        data = request.get_json()
        
        # 转换为 DataFrame
        df = pd.DataFrame(data.get('data', []))
        
        logger.info(f"Processing {len(df)} records")
        
        # 数据清洗
        df = clean_data(df)
        
        # 数据转换
        df = transform_data(df)
        
        # 统计信息
        stats = calculate_statistics(df)
        
        return jsonify({
            "processed_data": df.to_dict(orient='records'),
            "statistics": stats,
            "record_count": len(df),
            "timestamp": datetime.now().isoformat()
        })
        
    except Exception as e:
        logger.error(f"Error processing data: {str(e)}")
        return jsonify({"error": str(e)}), 500

def clean_data(df: pd.DataFrame) -> pd.DataFrame:
    """数据清洗"""
    # 删除重复行
    df = df.drop_duplicates()
    
    # 处理缺失值
    df = df.fillna(method='ffill')
    
    # 删除异常值（示例：使用 IQR 方法）
    numeric_cols = df.select_dtypes(include=[np.number]).columns
    for col in numeric_cols:
        Q1 = df[col].quantile(0.25)
        Q3 = df[col].quantile(0.75)
        IQR = Q3 - Q1
        df = df[~((df[col] < (Q1 - 1.5 * IQR)) | (df[col] > (Q3 + 1.5 * IQR)))]
    
    return df

def transform_data(df: pd.DataFrame) -> pd.DataFrame:
    """数据转换"""
    # 标准化数值列
    numeric_cols = df.select_dtypes(include=[np.number]).columns
    for col in numeric_cols:
        mean = df[col].mean()
        std = df[col].std()
        if std > 0:
            df[f'{col}_normalized'] = (df[col] - mean) / std
    
    return df

def calculate_statistics(df: pd.DataFrame) -> Dict:
    """计算统计信息"""
    stats = {}
    
    numeric_cols = df.select_dtypes(include=[np.number]).columns
    for col in numeric_cols:
        stats[col] = {
            "mean": float(df[col].mean()),
            "median": float(df[col].median()),
            "std": float(df[col].std()),
            "min": float(df[col].min()),
            "max": float(df[col].max())
        }
    
    return stats

@app.route('/api/data/aggregate', methods=['POST'])
def aggregate_data():
    """
    数据聚合
    """
    try:
        data = request.get_json()
        df = pd.DataFrame(data.get('data', []))
        
        group_by = data.get('group_by', [])
        agg_functions = data.get('aggregations', {})
        
        if not group_by or not agg_functions:
            return jsonify({"error": "Missing group_by or aggregations"}), 400
        
        result = df.groupby(group_by).agg(agg_functions).reset_index()
        
        return jsonify({
            "aggregated_data": result.to_dict(orient='records'),
            "groups": len(result)
        })
        
    except Exception as e:
        logger.error(f"Error aggregating data: {str(e)}")
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8002, debug=False)