# PowerShell script to launch CLI mode
Write-Host "Launching Employee Management System (CLI Console)..." -ForegroundColor Cyan
java -cp "bin;lib\*" com.ems.Main --cli
