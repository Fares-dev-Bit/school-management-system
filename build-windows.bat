@echo off
chcp 65001 >nul
title Build DashboardApp for Windows

echo === Building DashboardApp Windows Distribution ===
echo.

REM Step 1: Build the JAR
echo [1/3] Building JAR with Maven...
call mvnw.cmd clean package -DskipTests
if %ERRORLEVEL% neq 0 (
    echo ERROR: Maven build failed
    pause
    exit /b 1
)

REM Step 2: Create dist directory
if not exist dist mkdir dist

REM Step 3: Run jpackage to create .exe
echo [2/3] Creating Windows .exe with jpackage...
jpackage ^
  --type exe ^
  --input target ^
  --name DashboardApp ^
  --main-jar demo-0.0.1-SNAPSHOT.jar ^
  --main-class org.springframework.boot.loader.launch.JarLauncher ^
  --app-version 1.0 ^
  --description "School Management System - Candidate Registry" ^
  --vendor "School Management System" ^
  --dest dist ^
  --win-dir-chooser ^
  --win-menu ^
  --win-shortcut ^
  --verbose

if %ERRORLEVEL% neq 0 (
    echo.
    echo ERROR: jpackage failed. Make sure JDK 17+ is installed and jpackage is available.
    pause
    exit /b 1
)

echo.
echo [3/3] Build complete!
echo Installer created at: dist\DashboardApp-1.0.exe
pause
