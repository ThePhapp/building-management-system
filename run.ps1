# Script chạy dự án Building Management System
$javaPath = "C:\Program Files\Java\jdk-1.8\bin\java.exe"
if (-not (Test-Path $javaPath)) {
    Write-Warning "Không tìm thấy Java 8 tại $javaPath, sử dụng java mặc định từ PATH..."
    $javaPath = "java"
}

# Đọc các biến từ file .env nếu có
if (Test-Path ".env") {
    Get-Content .env | ForEach-Object {
        $line = $_.Trim()
        if ($line -and -not $line.StartsWith("#") -and $line.Contains("=")) {
            $parts = $line.Split("=", 2)
            $name = $parts[0].Trim()
            $val = $parts[1].Trim()
            [System.Environment]::SetEnvironmentVariable($name, $val, "Process")
        }
    }
}

Write-Host "Đang khởi động Building Management System..." -ForegroundColor Green
Write-Host "URL: http://localhost:$($env:SERVER_PORT ?? 18080)" -ForegroundColor Cyan
& $javaPath -jar target/building-management-system.war
