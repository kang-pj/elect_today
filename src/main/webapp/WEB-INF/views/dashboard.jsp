<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>전기차 보조금 대시보드</title>
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', sans-serif;
            background: #f5f7fa;
            min-height: 100vh;
        }

        .header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 20px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }

        .header-content {
            max-width: 1200px;
            margin: 0 auto;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .header h1 {
            font-size: 24px;
            font-weight: 600;
        }

        .region-info {
            display: flex;
            align-items: center;
            gap: 15px;
        }

        .region-badge {
            background: rgba(255,255,255,0.2);
            padding: 8px 16px;
            border-radius: 20px;
            font-size: 14px;
        }

        .btn-change-region {
            background: white;
            color: #667eea;
            border: none;
            padding: 8px 16px;
            border-radius: 20px;
            cursor: pointer;
            font-weight: 600;
            transition: all 0.3s;
        }

        .btn-change-region:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0,0,0,0.2);
        }

        .container {
            max-width: 1200px;
            margin: 30px auto;
            padding: 0 20px;
        }

        .controls {
            background: white;
            padding: 20px;
            border-radius: 12px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            margin-bottom: 20px;
            display: flex;
            gap: 20px;
            align-items: center;
            flex-wrap: wrap;
        }

        .control-group {
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .control-group label {
            font-weight: 600;
            color: #333;
            font-size: 14px;
        }

        .toggle-btn {
            display: flex;
            background: #f0f0f0;
            border-radius: 8px;
            overflow: hidden;
        }

        .toggle-btn button {
            padding: 10px 20px;
            border: none;
            background: transparent;
            cursor: pointer;
            font-weight: 600;
            transition: all 0.3s;
            color: #666;
        }

        .toggle-btn button.active {
            background: #667eea;
            color: white;
        }

        .chart-container {
            background: white;
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            margin-bottom: 20px;
        }

        .chart-wrapper {
            position: relative;
            height: 400px;
        }

        .stats-grid {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: 20px;
            margin-bottom: 20px;
        }

        .stats-grid-today {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 20px;
            margin-bottom: 20px;
        }

        .stat-card {
            background: white;
            padding: 20px;
            border-radius: 12px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            position: relative;
            overflow: hidden;
        }

        .stat-card::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            width: 4px;
            height: 100%;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }

        .stat-card.realtime::before {
            background: linear-gradient(135deg, #4caf50 0%, #45a049 100%);
            animation: pulse 2s infinite;
        }

        @keyframes pulse {
            0%, 100% { opacity: 1; }
            50% { opacity: 0.5; }
        }

        .stat-card h3 {
            font-size: 14px;
            color: #666;
            margin-bottom: 10px;
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .stat-card .badge {
            background: #4caf50;
            color: white;
            font-size: 10px;
            padding: 2px 8px;
            border-radius: 10px;
            font-weight: 600;
        }

        .stat-card .value {
            font-size: 32px;
            font-weight: 700;
            color: #333;
        }

        .stat-card .unit {
            font-size: 16px;
            color: #999;
            margin-left: 5px;
        }

        .stat-card .detail {
            margin-top: 10px;
            font-size: 12px;
            color: #999;
        }

        .stat-card .change {
            margin-top: 8px;
            font-size: 13px;
            font-weight: 600;
        }

        .stat-card .change.up {
            color: #f44336;
        }

        .stat-card .change.down {
            color: #4caf50;
        }

        .btn-refresh {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            padding: 12px 24px;
            border-radius: 8px;
            cursor: pointer;
            font-weight: 600;
            font-size: 14px;
            transition: all 0.3s ease;
            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
            display: inline-flex;
            align-items: center;
            gap: 8px;
        }

        .btn-refresh:hover:not(:disabled) {
            transform: translateY(-2px);
            box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
        }

        .btn-refresh:disabled {
            background: linear-gradient(135deg, #ccc 0%, #999 100%);
            cursor: not-allowed;
            transform: none;
            box-shadow: none;
        }

        .loading {
            text-align: center;
            padding: 60px 20px;
        }

        .spinner {
            border: 4px solid #f3f3f3;
            border-top: 4px solid #667eea;
            border-radius: 50%;
            width: 50px;
            height: 50px;
            animation: spin 1s linear infinite;
            margin: 0 auto 20px;
        }

        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }

        .error {
            background: #fee;
            color: #c33;
            padding: 20px;
            border-radius: 8px;
            text-align: center;
        }

        @media (max-width: 768px) {
            .header-content {
                flex-direction: column;
                gap: 15px;
            }

            .controls {
                flex-direction: column;
                align-items: stretch;
            }

            .control-group {
                flex-direction: column;
                align-items: stretch;
            }

            .chart-wrapper {
                height: 300px;
            }

            .stats-grid {
                grid-template-columns: 1fr;
            }

            .stats-grid-today {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
    <div class="header">
        <div class="header-content">
            <h1>🚗 전기차 보조금 대시보드</h1>
            <div class="region-info">
                <div class="region-badge" id="regionBadge">
                    <c:if test="${not empty sido && not empty region}">
                        ${sido} ${region}
                    </c:if>
                </div>
                <button class="btn-change-region" onclick="changeRegion()">
                    지역 변경
                </button>
            </div>
        </div>
    </div>

    <div class="container">
        <div class="controls">
            <div class="control-group">
                <label>데이터 유형:</label>
                <div class="toggle-btn">
                    <button id="btnRemaining" class="active" onclick="setDataType('remaining')">
                        잔여
                    </button>
                    <button id="btnReceived" onclick="setDataType('received')">
                        신청
                    </button>
                </div>
            </div>

            <div class="control-group">
                <label>기준:</label>
                <div class="toggle-btn">
                    <button id="btnTotal" class="active" onclick="setCategory('total')">
                        전체
                    </button>
                    <button id="btnPriority" onclick="setCategory('priority')">
                        우선순위
                    </button>
                    <button id="btnCorporation" onclick="setCategory('corporation')">
                        법인·기관
                    </button>
                    <button id="btnTaxi" onclick="setCategory('taxi')">
                        택시
                    </button>
                    <button id="btnGeneral" onclick="setCategory('general')">
                        일반
                    </button>
                </div>
            </div>
        </div>

        <div id="statsContainer"></div>
        <div id="chartContainer"></div>
        <div id="loadingContainer" class="loading">
            <div class="spinner"></div>
            <p>데이터를 불러오는 중...</p>
        </div>
    </div>

    <script>
        let chartInstance = null;
        let statsData = null;
        let realtimeData = null;
        let currentDataType = 'remaining';
        let currentCategory = 'total';

        const sido = '${sido}';
        const region = '${region}';

        window.addEventListener('load', function() {
            if (sido && region) {
                loadData();
            } else {
                document.getElementById('loadingContainer').innerHTML = 
                    '<div class="error">지역이 선택되지 않았습니다.</div>';
            }
        });

        async function loadData() {
            try {
                const url = '/api/ev-subsidy/region-stats?sido=' + encodeURIComponent(sido) + '&region=' + encodeURIComponent(region);
                const response = await fetch(url);
                const data = await response.json();
                
                if (data.error) {
                    throw new Error(data.error);
                }
                
                statsData = data;
                realtimeData = data.realtime; // DB에서 realtime 타입 데이터 로드
                renderStats();
                renderChart();
                
                document.getElementById('loadingContainer').style.display = 'none';
                
            } catch (error) {
                console.error('Error loading data:', error);
                document.getElementById('loadingContainer').innerHTML = 
                    '<div class="error">데이터를 불러오는 중 오류가 발생했습니다: ' + error.message + '</div>';
            }
        }

        async function refreshRealtime() {
            const btn = document.getElementById('btnRefresh');
            btn.disabled = true;
            btn.textContent = '⏳ 갱신 중...';
            
            try {
                const url = '/api/ev-subsidy/update-realtime?sido=' + encodeURIComponent(sido) + '&region=' + encodeURIComponent(region);
                const response = await fetch(url, {
                    method: 'POST'
                });
                const data = await response.json();
                
                if (data.error) {
                    throw new Error(data.error);
                }
                
                // realtime 데이터 업데이트
                realtimeData = {
                    totalReceived: data.totalReceived,
                    totalDelivered: data.totalDelivered,
                    todayReceived: data.todayReceived,
                    todayDelivered: data.todayDelivered,
                    updatedAt: data.updatedAt
                };
                
                renderStats();
                
                btn.textContent = '✅ 갱신 완료';
                setTimeout(() => {
                    btn.textContent = '🔄 실시간 갱신';
                    btn.disabled = false;
                }, 2000);
                
            } catch (error) {
                console.error('Error refreshing data:', error);
                alert('실시간 갱신 중 오류가 발생했습니다: ' + error.message);
                btn.textContent = '🔄 실시간 갱신';
                btn.disabled = false;
            }
        }

        function renderStats() {
            const latest = statsData.latest;
            
            // 현재 카테고리에 따른 값 가져오기
            let announcedValue, receivedValue, deliveredValue, remainingValue;
            let todayReceivedValue = 0, todayDeliveredValue = 0;
            
            if (currentCategory === 'total') {
                announcedValue = latest.totalAnnounced;
                receivedValue = realtimeData ? realtimeData.totalReceived : latest.totalReceived;
                deliveredValue = realtimeData ? realtimeData.totalDelivered : latest.totalDelivered;
                remainingValue = realtimeData ? realtimeData.totalRemaining : latest.remaining;
                if (realtimeData) {
                    todayReceivedValue = realtimeData.todayReceived;
                    todayDeliveredValue = realtimeData.todayDelivered;
                }
            } else if (currentCategory === 'priority') {
                announcedValue = latest.priorityAnnounced;
                receivedValue = realtimeData ? realtimeData.priorityReceived : latest.priorityReceived;
                deliveredValue = realtimeData ? realtimeData.priorityDelivered : latest.priorityDelivered;
                remainingValue = realtimeData ? realtimeData.priorityRemaining : (latest.priorityAnnounced - latest.priorityReceived);
                if (realtimeData) {
                    todayReceivedValue = realtimeData.todayPriorityReceived;
                    todayDeliveredValue = realtimeData.todayPriorityDelivered;
                }
            } else if (currentCategory === 'corporation') {
                announcedValue = latest.corporationAnnounced;
                receivedValue = realtimeData ? realtimeData.corporationReceived : latest.corporationReceived;
                deliveredValue = realtimeData ? realtimeData.corporationDelivered : latest.corporationDelivered;
                remainingValue = realtimeData ? realtimeData.corporationRemaining : (latest.corporationAnnounced - latest.corporationReceived);
                if (realtimeData) {
                    todayReceivedValue = realtimeData.todayCorporationReceived;
                    todayDeliveredValue = realtimeData.todayCorporationDelivered;
                }
            } else if (currentCategory === 'taxi') {
                announcedValue = latest.taxiAnnounced;
                receivedValue = realtimeData ? realtimeData.taxiReceived : latest.taxiReceived;
                deliveredValue = realtimeData ? realtimeData.taxiDelivered : latest.taxiDelivered;
                remainingValue = realtimeData ? realtimeData.taxiRemaining : (latest.taxiAnnounced - latest.taxiReceived);
                if (realtimeData) {
                    todayReceivedValue = realtimeData.todayTaxiReceived;
                    todayDeliveredValue = realtimeData.todayTaxiDelivered;
                }
            } else if (currentCategory === 'general') {
                announcedValue = latest.generalAnnounced;
                receivedValue = realtimeData ? realtimeData.generalReceived : latest.generalReceived;
                deliveredValue = realtimeData ? realtimeData.generalDelivered : latest.generalDelivered;
                remainingValue = realtimeData ? realtimeData.generalRemaining : (latest.generalAnnounced - latest.generalReceived);
                if (realtimeData) {
                    todayReceivedValue = realtimeData.todayGeneralReceived;
                    todayDeliveredValue = realtimeData.todayGeneralDelivered;
                }
            }
            
            const receivedRate = announcedValue > 0 ? ((receivedValue / announcedValue) * 100).toFixed(1) : '0.0';
            
            // 실시간 데이터 시간 포맷팅
            let realtimeTimeStr = '';
            if (realtimeData && realtimeData.updatedAt) {
                const dt = new Date(realtimeData.updatedAt);
                const h = dt.getHours().toString().padStart(2, '0');
                const m = dt.getMinutes().toString().padStart(2, '0');
                const s = dt.getSeconds().toString().padStart(2, '0');
                realtimeTimeStr = h + ':' + m + ':' + s + ' 기준';
            }
            
            // 오늘 증가량 표시
            let todayReceivedBadge = '';
            let todayDeliveredBadge = '';
            let todayCompareCard = '';
            
            if (realtimeData && todayReceivedValue !== undefined) {
                todayReceivedBadge = '<div class="change up">📈 오늘 +' + todayReceivedValue.toLocaleString() + '대 신청</div>';
                todayDeliveredBadge = '<div class="change up">🚗 오늘 +' + todayDeliveredValue.toLocaleString() + '대 출고</div>';
                
                // 오늘 접수 카드
                const todayReceivedCard = '<div class="stat-card realtime">' +
                    '<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;">' +
                    '<h3 style="margin: 0;">오늘 접수 <span class="badge">실시간</span></h3>' +
                    '<button class="btn-refresh" id="btnRefresh" onclick="refreshRealtime()">🔄 갱신</button>' +
                    '</div>' +
                    '<div style="font-size: 12px; color: #667eea; margin-bottom: 10px;">🕐 ' + realtimeTimeStr + '</div>' +
                    '<div class="value" style="font-size: 42px; color: #f44336;">' + todayReceivedValue.toLocaleString() + '<span class="unit">대</span></div>' +
                    '<div class="detail" style="margin-top: 10px;">📝 오늘 신청한 대수</div>' +
                    '</div>';
                
                // 오늘 출고 카드
                const todayDeliveredCard = '<div class="stat-card realtime">' +
                    '<h3>오늘 출고</h3>' +
                    '<div style="font-size: 12px; color: #667eea; margin-bottom: 10px;">🕐 ' + realtimeTimeStr + '</div>' +
                    '<div class="value" style="font-size: 42px; color: #4caf50;">' + todayDeliveredValue.toLocaleString() + '<span class="unit">대</span></div>' +
                    '<div class="detail" style="margin-top: 10px;">🚗 오늘 출고된 대수</div>' +
                    '</div>';
                
                todayCompareCard = '<div class="stats-grid-today">' + todayReceivedCard + todayDeliveredCard + '</div>';
            }
            
            const realtimeClass = realtimeData ? 'realtime' : '';
            const realtimeBadge = realtimeData ? '<span class="badge">실시간</span>' : '';
            const realtimeTime = realtimeData ? '<div style="font-size: 11px; color: #4caf50; margin-bottom: 5px;">🕐 ' + realtimeTimeStr + '</div>' : '';
            
            // 00시 기준 값 (카테고리별)
            let baseReceivedValue, baseDeliveredValue;
            if (currentCategory === 'total') {
                baseReceivedValue = latest.totalReceived;
                baseDeliveredValue = latest.totalDelivered;
            } else if (currentCategory === 'priority') {
                baseReceivedValue = latest.priorityReceived;
                baseDeliveredValue = latest.priorityDelivered;
            } else if (currentCategory === 'corporation') {
                baseReceivedValue = latest.corporationReceived;
                baseDeliveredValue = latest.corporationDelivered;
            } else if (currentCategory === 'taxi') {
                baseReceivedValue = latest.taxiReceived;
                baseDeliveredValue = latest.taxiDelivered;
            } else if (currentCategory === 'general') {
                baseReceivedValue = latest.generalReceived;
                baseDeliveredValue = latest.generalDelivered;
            }
            
            const html = '<div class="stats-grid">' +
                '<div class="stat-card">' +
                '<h3>공고 대수</h3>' +
                '<div class="value">' + announcedValue.toLocaleString() + '<span class="unit">대</span></div>' +
                '<div class="detail">' +
                '우선 ' + latest.priorityAnnounced.toLocaleString() + ' | ' +
                '법인 ' + latest.corporationAnnounced.toLocaleString() + ' | ' +
                '택시 ' + latest.taxiAnnounced.toLocaleString() + ' | ' +
                '일반 ' + latest.generalAnnounced.toLocaleString() +
                '</div>' +
                '</div>' +
                '<div class="stat-card ' + realtimeClass + '">' +
                '<h3>접수 대수 ' + realtimeBadge + '</h3>' +
                realtimeTime +
                '<div class="value">' + receivedValue.toLocaleString() + '<span class="unit">대</span></div>' +
                '<div class="detail">00시 기준: ' + baseReceivedValue.toLocaleString() + '대</div>' +
                todayReceivedBadge +
                '</div>' +
                '<div class="stat-card ' + realtimeClass + '">' +
                '<h3>출고 대수 ' + realtimeBadge + '</h3>' +
                realtimeTime +
                '<div class="value">' + deliveredValue.toLocaleString() + '<span class="unit">대</span></div>' +
                '<div class="detail">00시 기준: ' + baseDeliveredValue.toLocaleString() + '대</div>' +
                todayDeliveredBadge +
                '</div>' +
                '<div class="stat-card">' +
                '<h3>잔여 대수</h3>' +
                '<div class="value">' + remainingValue.toLocaleString() + '<span class="unit">대</span></div>' +
                '<div class="detail">접수율: ' + receivedRate + '%</div>' +
                '</div>' +
                '</div>' +
                todayCompareCard;
            
            document.getElementById('statsContainer').innerHTML = html;
        }

        function renderChart() {
            const chartHtml = '<div class="chart-container">' +
                '<div class="chart-wrapper">' +
                '<canvas id="mainChart"></canvas>' +
                '</div>' +
                '</div>';
            
            document.getElementById('chartContainer').innerHTML = chartHtml;
            
            updateChart();
        }

        function updateChart() {
            const ctx = document.getElementById('mainChart');
            
            if (chartInstance) {
                chartInstance.destroy();
            }
            
            const data = getChartData();
            const isRemaining = currentDataType === 'remaining';
            
            chartInstance = new Chart(ctx, {
                type: 'line',
                data: {
                    labels: statsData.dates,
                    datasets: [{
                        label: data.label,
                        data: data.values,
                        borderColor: isRemaining ? '#f44336' : '#4caf50',
                        backgroundColor: isRemaining ? 'rgba(244, 67, 54, 0.1)' : 'rgba(76, 175, 80, 0.1)',
                        borderWidth: 4,
                        tension: 0.3,
                        fill: true,
                        pointRadius: 6,
                        pointHoverRadius: 9,
                        pointBackgroundColor: isRemaining ? '#f44336' : '#4caf50',
                        pointBorderColor: '#fff',
                        pointBorderWidth: 3,
                        pointHoverBackgroundColor: isRemaining ? '#d32f2f' : '#388e3c',
                        pointHoverBorderWidth: 4
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    interaction: {
                        intersect: false,
                        mode: 'index'
                    },
                    plugins: {
                        legend: {
                            display: true,
                            position: 'top',
                            labels: {
                                font: {
                                    size: 15,
                                    weight: 'bold'
                                },
                                padding: 15,
                                usePointStyle: true,
                                pointStyle: 'circle'
                            }
                        },
                        tooltip: {
                            backgroundColor: 'rgba(0, 0, 0, 0.85)',
                            padding: 15,
                            titleFont: {
                                size: 15,
                                weight: 'bold'
                            },
                            bodyFont: {
                                size: 14
                            },
                            bodySpacing: 8,
                            cornerRadius: 8,
                            displayColors: true,
                            callbacks: {
                                title: function(context) {
                                    return '📅 ' + context[0].label;
                                },
                                label: function(context) {
                                    const value = context.parsed.y.toLocaleString();
                                    const emoji = isRemaining ? '📉' : '📈';
                                    return emoji + ' ' + context.dataset.label + ': ' + value + '대';
                                },
                                afterLabel: function(context) {
                                    if (context.dataIndex > 0) {
                                        const current = context.parsed.y;
                                        const previous = context.dataset.data[context.dataIndex - 1];
                                        const diff = current - previous;
                                        const sign = diff > 0 ? '+' : '';
                                        const arrow = diff > 0 ? '↑' : '↓';
                                        return arrow + ' 전일대비: ' + sign + diff.toLocaleString() + '대';
                                    }
                                    return '';
                                }
                            }
                        }
                    },
                    scales: {
                        y: {
                            beginAtZero: true,
                            ticks: {
                                callback: function(value) {
                                    return value.toLocaleString() + '대';
                                },
                                font: {
                                    size: 13,
                                    weight: '500'
                                },
                                padding: 10
                            },
                            grid: {
                                color: 'rgba(0, 0, 0, 0.06)',
                                drawBorder: false
                            },
                            title: {
                                display: true,
                                text: isRemaining ? '잔여 대수' : '접수 대수',
                                font: {
                                    size: 14,
                                    weight: 'bold'
                                },
                                padding: 10
                            }
                        },
                        x: {
                            ticks: {
                                font: {
                                    size: 12,
                                    weight: '500'
                                },
                                padding: 8
                            },
                            grid: {
                                display: false,
                                drawBorder: false
                            },
                            title: {
                                display: true,
                                text: '날짜',
                                font: {
                                    size: 14,
                                    weight: 'bold'
                                },
                                padding: 10
                            }
                        }
                    },
                    animation: {
                        duration: 800,
                        easing: 'easeInOutQuart'
                    }
                }
            });
        }

        function getChartData() {
            const categoryLabels = {
                'total': '전체',
                'priority': '우선순위',
                'corporation': '법인·기관',
                'taxi': '택시',
                'general': '일반'
            };
            
            const categoryLabel = categoryLabels[currentCategory];
            let values;
            
            if (currentDataType === 'remaining') {
                if (currentCategory === 'total') {
                    values = statsData.remaining;
                } else {
                    values = statsData[currentCategory].remaining;
                }
                return {
                    label: '잔여 대수 (' + categoryLabel + ')',
                    values: values
                };
            } else {
                if (currentCategory === 'total') {
                    values = statsData.received;
                } else {
                    values = statsData[currentCategory].received;
                }
                return {
                    label: '접수 대수 (' + categoryLabel + ')',
                    values: values
                };
            }
        }

        function setDataType(type) {
            currentDataType = type;
            
            document.getElementById('btnRemaining').classList.toggle('active', type === 'remaining');
            document.getElementById('btnReceived').classList.toggle('active', type === 'received');
            
            updateChart();
        }

        function setCategory(category) {
            currentCategory = category;
            
            document.getElementById('btnTotal').classList.toggle('active', category === 'total');
            document.getElementById('btnPriority').classList.toggle('active', category === 'priority');
            document.getElementById('btnCorporation').classList.toggle('active', category === 'corporation');
            document.getElementById('btnTaxi').classList.toggle('active', category === 'taxi');
            document.getElementById('btnGeneral').classList.toggle('active', category === 'general');
            
            updateChart();
            renderStats();  // 통계 카드도 업데이트
        }

        function changeRegion() {
            window.location.href = '/region-select';
        }
    </script>
</body>
</html>
