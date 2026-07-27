$KEY = "C:\Users\SBS\Downloads\LightsailDefaultKey-ap-northeast-2.pem"
$SERVER = "ubuntu@3.36.122.85"
$JAR = "build\libs\popupmap-0.0.1-SNAPSHOT.jar"

Write-Host "1. 빌드 중..." -ForegroundColor Cyan
.\gradlew.bat bootJar
if (-not $?) { Write-Host "빌드 실패" -ForegroundColor Red; exit 1 }

Write-Host "2. 업로드 중..." -ForegroundColor Cyan
scp -i $KEY $JAR "${SERVER}:/home/ubuntu/popupmap/"
if (-not $?) { Write-Host "업로드 실패" -ForegroundColor Red; exit 1 }

Write-Host "3. 서버 재시작 중..." -ForegroundColor Cyan
ssh -i $KEY $SERVER "sudo systemctl restart popupmap"

Write-Host "배포 완료!" -ForegroundColor Green
