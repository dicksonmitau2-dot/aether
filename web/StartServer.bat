@echo off
title AetherMind - Web Server
cd /d "C:\AetherMind\web"

echo.
echo  ============================================
echo   AetherMind Web Server
echo  ============================================
echo.

:: Try Python 3 first
python --version >nul 2>&1
if %errorlevel% == 0 (
    echo  Starting server with Python...
    echo.
    for /f "tokens=2 delims=:" %%a in ('ipconfig ^| findstr /i "IPv4"') do (
        set IP=%%a
        goto :found
    )
    :found
    set IP=%IP: =%
    echo  ============================================
    echo   Open this on your phone browser:
    echo.
    echo     http://%IP%:8080
    echo.
    echo   Make sure your phone is on the same WiFi!
    echo  ============================================
    echo.
    echo  Press Ctrl+C to stop the server.
    echo.
    python -m http.server 8080
    goto :end
)

:: Try Python 2 fallback
python2 --version >nul 2>&1
if %errorlevel% == 0 (
    python2 -m SimpleHTTPServer 8080
    goto :end
)

:: No Python — show manual instructions
echo  Python is not installed on this PC.
echo.
echo  To run the web app on your phone, you can:
echo.
echo  Option 1 - Install Python (free):
echo    https://www.python.org/downloads/
echo    Then run this file again.
echo.
echo  Option 2 - Open index.html directly:
echo    Copy the C:\AetherMind\web folder to your phone
echo    and open index.html in your phone browser.
echo.
:end
pause
