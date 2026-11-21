@echo off
title NextStore - Retail Billing & Management System (MySQL Mode)
echo ====================================================================
echo Starting NextStore (Spring Boot 3.3.5)
echo Profile: MYSQL (Connected to MySQL Server on localhost:3306)
echo Database: pos_inventory_db
echo Port: 8080
echo ====================================================================
echo.

if "%DB_USERNAME%"=="" set DB_USERNAME=pos_app
if "%DB_PASSWORD%"=="" set DB_PASSWORD=

set SPRING_PROFILES_ACTIVE=mysql

echo Launching server with MySQL...
echo Browser will automatically open http://localhost:8080 in 5 seconds...
echo.

:: Automatically open browser once server begins starting up
start "" cmd /c "timeout /t 5 /nobreak >nul & start http://localhost:8080"

java -jar "target\nextstore-backend.jar" --spring.profiles.active=mysql --spring.datasource.username="%DB_USERNAME%" --spring.datasource.password="%DB_PASSWORD%"

pause
