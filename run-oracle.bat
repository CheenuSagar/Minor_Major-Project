@echo off
title POS and Inventory Management System - Oracle 21c XE
echo ====================================================================
echo Starting POS and Inventory Management System with Oracle 21c XE
echo Default URL: jdbc:oracle:thin:@//localhost:1521/XEPDB1
echo ====================================================================

REM Uncomment and set your Oracle DB credentials if different:
REM set DB_URL=jdbc:oracle:thin:@//localhost:1521/XEPDB1
REM set DB_USERNAME=pos_user
REM set DB_PASSWORD=pos_pass

java -jar "target\pos-inventory-backend-1.0.0.jar"

pause
