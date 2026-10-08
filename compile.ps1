# PowerShell compilation script for Employee Management System
Write-Host "===================================================" -ForegroundColor Cyan
Write-Host "  Compiling Employee Management System (Java 25)..." -ForegroundColor Cyan
Write-Host "===================================================" -ForegroundColor Cyan

if (-not (Test-Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

javac -cp ".;lib\*" -d bin src\com\ems\model\*.java src\com\ems\util\*.java src\com\ems\dao\*.java src\com\ems\service\*.java src\com\ems\ui\*.java src\com\ems\Main.java

if ($LASTEXITCODE -eq 0) {
    Copy-Item -Path "resources\*" -Destination "bin\" -Force -ErrorAction SilentlyContinue
    Write-Host "`n[SUCCESS] Compilation finished successfully!" -ForegroundColor Green
    Write-Host "Class files are ready in 'bin/' directory." -ForegroundColor Green
} else {
    Write-Host "`n[ERROR] Compilation failed. Please inspect error messages above." -ForegroundColor Red
}
