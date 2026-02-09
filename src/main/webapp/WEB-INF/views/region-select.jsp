<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>지역 선택 - 전기차 보조금 조회</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen', 'Ubuntu', 'Cantarell', sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }

        .container {
            background: white;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
            max-width: 600px;
            width: 100%;
            padding: 40px;
            animation: slideUp 0.5s ease-out;
        }

        @keyframes slideUp {
            from {
                opacity: 0;
                transform: translateY(30px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        .header {
            text-align: center;
            margin-bottom: 40px;
        }

        .header h1 {
            font-size: 28px;
            color: #333;
            margin-bottom: 10px;
        }

        .header p {
            color: #666;
            font-size: 14px;
        }

        .icon {
            font-size: 60px;
            margin-bottom: 20px;
        }

        .form-group {
            margin-bottom: 25px;
        }

        .form-group label {
            display: block;
            font-weight: 600;
            color: #333;
            margin-bottom: 10px;
            font-size: 14px;
        }

        .form-group select {
            width: 100%;
            padding: 15px;
            border: 2px solid #e0e0e0;
            border-radius: 10px;
            font-size: 16px;
            color: #333;
            background-color: #f8f9fa;
            transition: all 0.3s ease;
            cursor: pointer;
        }

        .form-group select:focus {
            outline: none;
            border-color: #667eea;
            background-color: white;
        }

        .form-group select:disabled {
            background-color: #f0f0f0;
            cursor: not-allowed;
        }

        .btn-submit {
            width: 100%;
            padding: 16px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            border-radius: 10px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            margin-top: 10px;
        }

        .btn-submit:hover:not(:disabled) {
            transform: translateY(-2px);
            box-shadow: 0 10px 20px rgba(102, 126, 234, 0.4);
        }

        .btn-submit:disabled {
            background: #ccc;
            cursor: not-allowed;
        }

        .info-box {
            background: #f0f7ff;
            border-left: 4px solid #667eea;
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 25px;
        }

        .info-box p {
            color: #555;
            font-size: 14px;
            line-height: 1.6;
        }

        .selected-info {
            background: #e8f5e9;
            border-left: 4px solid #4caf50;
            padding: 15px;
            border-radius: 8px;
            margin-top: 20px;
            display: none;
        }

        .selected-info.show {
            display: block;
            animation: fadeIn 0.3s ease-out;
        }

        @keyframes fadeIn {
            from {
                opacity: 0;
            }
            to {
                opacity: 1;
            }
        }

        .selected-info p {
            color: #2e7d32;
            font-weight: 600;
            font-size: 14px;
        }

        .loading {
            display: none;
            text-align: center;
            padding: 20px;
        }

        .loading.show {
            display: block;
        }

        .spinner {
            border: 3px solid #f3f3f3;
            border-top: 3px solid #667eea;
            border-radius: 50%;
            width: 40px;
            height: 40px;
            animation: spin 1s linear infinite;
            margin: 0 auto;
        }

        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }

        @media (max-width: 480px) {
            .container {
                padding: 30px 20px;
            }

            .header h1 {
                font-size: 24px;
            }

            .icon {
                font-size: 50px;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <div class="icon">🚗</div>
            <h1>지역 선택</h1>
            <p>전기차 보조금 정보를 확인할 지역을 선택해주세요</p>
        </div>

        <div class="info-box">
            <p>💡 선택한 지역 정보는 브라우저에 저장되어 다음 방문 시에도 유지됩니다.</p>
        </div>

        <form id="regionForm">
            <div class="form-group">
                <label for="sido">시/도 선택</label>
                <select id="sido" name="sido" required>
                    <option value="">시/도를 선택하세요</option>
                    <c:forEach var="entry" items="${regionMap}">
                        <option value="${entry.key}">${entry.key}</option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <label for="region">시/군/구 선택</label>
                <select id="region" name="region" required disabled>
                    <option value="">먼저 시/도를 선택하세요</option>
                </select>
            </div>

            <button type="submit" class="btn-submit" id="submitBtn" disabled>
                선택 완료
            </button>
        </form>

        <div class="selected-info" id="selectedInfo">
            <p>✓ <span id="selectedText"></span></p>
        </div>

        <div class="loading" id="loading">
            <div class="spinner"></div>
            <p style="margin-top: 10px; color: #666;">처리 중...</p>
        </div>
    </div>

    <script>
        // 지역 데이터
        const regionData = {
            <c:forEach var="entry" items="${regionMap}" varStatus="status">
            "${entry.key}": [
                <c:forEach var="region" items="${entry.value}" varStatus="regionStatus">
                "${region}"<c:if test="${!regionStatus.last}">,</c:if>
                </c:forEach>
            ]<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        };

        const sidoSelect = document.getElementById('sido');
        const regionSelect = document.getElementById('region');
        const submitBtn = document.getElementById('submitBtn');
        const regionForm = document.getElementById('regionForm');
        const selectedInfo = document.getElementById('selectedInfo');
        const selectedText = document.getElementById('selectedText');
        const loading = document.getElementById('loading');

        // 시/도 선택 시
        sidoSelect.addEventListener('change', function() {
            const selectedSido = this.value;
            
            // 지역 선택 초기화
            regionSelect.innerHTML = '<option value="">시/군/구를 선택하세요</option>';
            regionSelect.disabled = false;
            submitBtn.disabled = true;
            
            if (selectedSido && regionData[selectedSido]) {
                // 선택된 시/도의 지역 목록 추가
                regionData[selectedSido].forEach(region => {
                    const option = document.createElement('option');
                    option.value = region;
                    option.textContent = region;
                    regionSelect.appendChild(option);
                });
            } else {
                regionSelect.disabled = true;
            }
        });

        // 지역 선택 시
        regionSelect.addEventListener('change', function() {
            submitBtn.disabled = !this.value;
        });

        // 폼 제출
        regionForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const sido = sidoSelect.value;
            const region = regionSelect.value;
            
            if (!sido || !region) {
                alert('시/도와 지역을 모두 선택해주세요.');
                return;
            }

            // 로딩 표시
            loading.classList.add('show');
            submitBtn.disabled = true;

            try {
                const response = await fetch('/api/region/select', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: new URLSearchParams({
                        sido: sido,
                        region: region
                    })
                });

                const result = await response.json();

                if (result.success) {
                    // 성공 메시지 표시
                    selectedText.textContent = sido + ' ' + region + '이(가) 선택되었습니다!';
                    selectedInfo.classList.add('show');
                    
                    // 2초 후 메인 페이지로 이동
                    setTimeout(() => {
                        window.location.href = '/';
                    }, 2000);
                } else {
                    alert('지역 선택 중 오류가 발생했습니다.');
                    loading.classList.remove('show');
                    submitBtn.disabled = false;
                }
            } catch (error) {
                console.error('Error:', error);
                alert('서버 오류가 발생했습니다.');
                loading.classList.remove('show');
                submitBtn.disabled = false;
            }
        });

        // 페이지 로드 시 이미 선택된 지역이 있는지 확인
        window.addEventListener('load', async function() {
            try {
                const response = await fetch('/api/region/selected');
                const result = await response.json();
                
                if (result.selected) {
                    // 이미 선택된 지역이 있으면 표시
                    selectedText.textContent = result.sido + ' ' + result.region + '이(가) 이미 선택되어 있습니다.';
                    selectedInfo.classList.add('show');
                    
                    // 선택 값 설정
                    sidoSelect.value = result.sido;
                    sidoSelect.dispatchEvent(new Event('change'));
                    
                    setTimeout(() => {
                        regionSelect.value = result.region;
                        regionSelect.dispatchEvent(new Event('change'));
                    }, 100);
                }
            } catch (error) {
                console.error('Error checking selected region:', error);
            }
        });
    </script>
</body>
</html>
