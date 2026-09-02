@echo off
title AetherMind - Build
cd /d "C:\AetherMind"

set JAVA="C:\Program Files\IntelliJ IDEA 2026.1.3\jbr\bin\java.exe"
set JAVAC="C:\Program Files\IntelliJ IDEA 2026.1.3\jbr\bin\javac.exe"
set JAR="C:\Program Files\IntelliJ IDEA 2026.1.3\jbr\bin\jar.exe"

echo.
echo  [1/3] Compiling AetherMindApp.java...
%JAVAC% AetherMindApp.java
if %errorlevel% neq 0 (
    echo  ERROR: Compile failed!
    pause
    exit /b 1
)

echo  [2/3] Packaging into AetherMind.jar...
%JAR% cfm AetherMind.jar MANIFEST.MF AetherMindApp.class AetherMindApp$*.class
if %errorlevel% neq 0 (
    echo  ERROR: JAR packaging failed!
    pause
    exit /b 1
)

echo  [3/3] Done! AetherMind.jar is ready.
echo.
echo  You can now launch AetherMind from your Desktop shortcut.
echo.
pause
