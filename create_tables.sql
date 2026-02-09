-- 사용자 테이블
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 전기차 보조금 데이터 테이블
CREATE TABLE IF NOT EXISTS ev_subsidy (
    id BIGSERIAL PRIMARY KEY,
    crawl_date DATE NOT NULL,
    sido VARCHAR(50) NOT NULL,
    region VARCHAR(50) NOT NULL,
    car_type VARCHAR(50),
    
    -- 공고 대수 (5개)
    total_announced INTEGER NOT NULL,
    priority_announced INTEGER,
    corporation_announced INTEGER,
    taxi_announced INTEGER,
    general_announced INTEGER,
    
    -- 접수 대수 (5개)
    total_received INTEGER NOT NULL,
    priority_received INTEGER,
    corporation_received INTEGER,
    taxi_received INTEGER,
    general_received INTEGER,
    
    -- 출고 대수 (5개)
    total_delivered INTEGER NOT NULL,
    priority_delivered INTEGER,
    corporation_delivered INTEGER,
    taxi_delivered INTEGER,
    general_delivered INTEGER,
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT unique_crawl UNIQUE (crawl_date, sido, region, car_type)
);

-- 인덱스 생성
CREATE INDEX IF NOT EXISTS idx_ev_subsidy_crawl_date ON ev_subsidy(crawl_date);
CREATE INDEX IF NOT EXISTS idx_ev_subsidy_sido ON ev_subsidy(sido);
CREATE INDEX IF NOT EXISTS idx_ev_subsidy_region ON ev_subsidy(region);
CREATE INDEX IF NOT EXISTS idx_ev_subsidy_car_type ON ev_subsidy(car_type);

-- 일일 변화량 테이블
CREATE TABLE IF NOT EXISTS ev_subsidy_daily (
    id BIGSERIAL PRIMARY KEY,
    target_date DATE NOT NULL,
    sido VARCHAR(50) NOT NULL,
    region VARCHAR(50) NOT NULL,
    
    daily_received INTEGER NOT NULL,
    daily_delivered INTEGER NOT NULL,
    daily_remaining_change INTEGER NOT NULL,
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT unique_daily UNIQUE (target_date, sido, region)
);

-- 인덱스 생성
CREATE INDEX IF NOT EXISTS idx_ev_subsidy_daily_target_date ON ev_subsidy_daily(target_date);
CREATE INDEX IF NOT EXISTS idx_ev_subsidy_daily_sido ON ev_subsidy_daily(sido);

-- 테이블 정보 확인
SELECT 
    table_name,
    column_name,
    data_type,
    character_maximum_length,
    is_nullable
FROM information_schema.columns
WHERE table_name IN ('ev_subsidy', 'ev_subsidy_daily')
ORDER BY table_name, ordinal_position;
