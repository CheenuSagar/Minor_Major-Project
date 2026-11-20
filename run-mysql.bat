@echo off
title NextStore - MySQL Database Backend
echo ====================================================================
echo Starting NextStore with MySQL Database
echo URL: jdbc:mysql://localhost:3306/pos_inventory_db
echo ====================================================================

REM Set your MySQL credentials (override as needed):
if "%DB_USERNAME%"=="" set DB_USERNAME=root
if "%DB_PASSWORD%"=="" set DB_PASSWORD=root

set SPRING_PROFILES_ACTIVE=mysql
java -jar "target\nextstore-backend.jar" --spring.profiles.active=mysql --spring.datasource.password="%DB_PASSWORD%"

pause
