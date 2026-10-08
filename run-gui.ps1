# PowerShell script to launch GUI mode
Write-Host "Launching Employee Management System (Swing Desktop GUI)..." -ForegroundColor Green
Start-Process javaw -ArgumentList "-cp `"bin;lib\*`" com.ems.Main --gui"
