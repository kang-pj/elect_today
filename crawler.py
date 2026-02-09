#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
전기차 보조금 크롤러 (Python)
Java에서 호출 가능
"""

import json
import sys
from datetime import datetime
from pathlib import Path

# 여기에 파이썬 크롤링 코드를 넣어주세요
# 예시:
def crawl_ev_subsidy():
    """전기차 보조금 데이터 크롤링"""
    # TODO: 실제 크롤링 로직
    data = []
    
    # 샘플 데이터 (실제 크롤링 결과로 교체)
    data = [
        {
            "sido": "서울",
            "region": "강남구",
            "totalAnnounced": 1000,
            "totalReceived": 850,
            "totalDelivered": 700,
            "totalRemaining": 300
        }
    ]
    
    return data

def save_to_json(data):
    """JSON 파일로 저장"""
    folder = Path("data/ev_subsidy")
    folder.mkdir(parents=True, exist_ok=True)
    
    today = datetime.now().strftime("%Y%m%d")
    
    # 시퀀스 번호 계산
    existing_files = list(folder.glob(f"{today}_*.json"))
    sequence = len(existing_files) + 1
    
    filename = f"{today}_{sequence}.json"
    filepath = folder / filename
    
    result = {
        "crawlTime": datetime.now().isoformat(),
        "date": today,
        "sequence": sequence,
        "totalCount": len(data),
        "data": data
    }
    
    with open(filepath, 'w', encoding='utf-8') as f:
        json.dump(result, f, ensure_ascii=False, indent=2)
    
    return str(filepath)

if __name__ == "__main__":
    try:
        print("크롤링 시작...", file=sys.stderr)
        data = crawl_ev_subsidy()
        
        if data:
            filepath = save_to_json(data)
            print(json.dumps({"success": True, "file": filepath, "count": len(data)}))
        else:
            print(json.dumps({"success": False, "error": "데이터 없음"}))
            sys.exit(1)
            
    except Exception as e:
        print(json.dumps({"success": False, "error": str(e)}))
        sys.exit(1)
