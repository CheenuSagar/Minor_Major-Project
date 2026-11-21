@echo off
title NextStore - MySQL Database Backend
echo ====================================================================
echo Starting NextStore with MySQL Database (Automatic Bypass Mode)
echo URL: jdbc:mysql://localhost:3306/pos_inventory_db
echo ====================================================================

REM Automatic local passwordless user pos_app (no password required):
if "%DB_USERNAME%"=="" set DB_USERNAME=pos_app
if "%DB_PASSWORD%"=="" set DB_PASSWORD=

set SPRING_PROFILES_ACTIVE=mysql

:: Automatically open browser once server begins starting up
start "" cmd /c "timeout /t 5 /nobreak >nul & start http://localhost:8080"

java -jar "target\nextstore-backend.jar" --spring.profiles.active=mysql --spring.datasource.username="%DB_USERNAME%" --spring.datasource.password="%DB_PASSWORD%"

pause
