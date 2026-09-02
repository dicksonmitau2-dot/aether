@echo off
title AetherMind - Uninstall
echo.
echo  ============================================
echo   AetherMind Uninstaller
echo  ============================================
echo.
echo  This will remove:
echo    - C:\AetherMind  (all app files)
echo    - Desktop shortcut (AetherMind.bat)
echo.
set /p confirm=  Type YES to confirm uninstall: 

if /i not "%confirm%"=="YES" (
    echo  Uninstall cancelled.
    pause
    exit /b 0
)

echo.
echo  Removing app folder...
cd /d "C:\"
rmdir /s /q "C:\AetherMind"

echo  Removing desktop shortcut...
del /f /q "%USERPROFILE%\Desktop\AetherMind.bat" 2>nul
del /f /q "%USERPROFILE%\Desktop\RunAetherMind.bat" 2>nul

echo.
echo  AetherMind has been removed from your PC.
echo  Source files in C:\Users\hp\ were kept.
echo.
pause
