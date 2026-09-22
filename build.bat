@echo off
title AetherMind - Build
cd /d "%~dp0"

where javac >nul 2>&1
if errorlevel 1 (
    echo  javac not found. Install a JDK and add it to PATH, or set JAVA_HOME.
    echo  Then run build.bat again.
    exit /b 1
)

where jar >nul 2>&1
if errorlevel 1 (
    echo  jar not found. Install a full JDK and add it to PATH, or set JAVA_HOME.
    echo  A Java runtime or compiler alone cannot package AetherMind.jar.
    exit /b 1
)

if defined JAVA_HOME (
    set "JAVAC=%JAVA_HOME%\bin\javac.exe"
    set "JAR=%JAVA_HOME%\bin\jar.exe"
) else (
    set JAVAC=javac
    set JAR=jar
)

echo.
echo  [1/3] Syncing knowledge.json...
copy /Y shared\knowledge.json web\knowledge.json >nul

echo  [2/3] Compiling...
"%JAVAC%" --release 11 AetherMindApp.java AetherBrain.java
if errorlevel 1 (
    echo  ERROR: Compile failed.
    pause
    exit /b 1
)

echo  [3/3] Packaging AetherMind.jar...
"%JAR%" cfm AetherMind.jar MANIFEST.MF AetherMindApp.class AetherBrain.class AetherMindApp$*.class AetherBrain$*.class -C shared knowledge.json
if errorlevel 1 (
    echo  ERROR: JAR packaging failed.
    pause
    exit /b 1
)

echo  Done. Run: java -jar AetherMind.jar
pause
