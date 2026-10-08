# PowerShell script to push project to GitHub
Write-Host "===================================================" -ForegroundColor Cyan
Write-Host "       Push Employee Management System to GitHub   " -ForegroundColor Cyan
Write-Host "===================================================" -ForegroundColor Cyan

$existingRemote = git remote get-url origin 2>$null

if ($existingRemote) {
    Write-Host "Current remote URL: $existingRemote" -ForegroundColor Yellow
    $useExisting = Read-Host "Do you want to push to this existing remote? (y/n)"
    if ($useExisting -ne "y" -and $useExisting -ne "yes") {
        $repoUrl = Read-Host "Enter your new GitHub repository URL (e.g., https://github.com/username/ems.git)"
        if ($repoUrl) {
            git remote set-url origin $repoUrl
        }
    }
} else {
    $repoUrl = Read-Host "Enter your GitHub repository URL (e.g., https://github.com/username/EmployeeManagementSystem.git)"
    if (-not $repoUrl) {
        Write-Host "No URL provided. Exiting..." -ForegroundColor Red
        exit
    }
    git remote add origin $repoUrl
}

Write-Host "`nEnsuring branch is 'main'..." -ForegroundColor Cyan
git branch -M main

Write-Host "Pushing to GitHub..." -ForegroundColor Cyan
git push -u origin main

if ($LASTEXITCODE -eq 0) {
    Write-Host "`n[SUCCESS] Project pushed to GitHub successfully!" -ForegroundColor Green
} else {
    Write-Host "`n[ERROR] Push failed. If this is a private repo or first time pushing, ensure your GitHub credentials / token are entered." -ForegroundColor Red
}
