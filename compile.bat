@echo off
echo ===================================================
echo   Compiling Employee Management System...
echo ===================================================

if not exist bin mkdir bin

javac -cp ".;lib\*" -d bin src\com\ems\model\*.java src\com\ems\util\*.java src\com\ems\dao\*.java src\com\ems\service\*.java src\com\ems\ui\*.java src\com\ems\Main.java

if %ERRORLEVEL% EQU 0 (
    copy resources\* bin\ > nul 2>&1
    echo.
    echo [SUCCESS] Compilation finished successfully!
    echo Binary class files placed in 'bin/' folder.
) else (
    echo.
    echo [ERROR] Compilation failed. Please check the errors above.
)
pause
