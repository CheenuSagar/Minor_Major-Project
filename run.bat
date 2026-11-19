@echo off
title POS and Inventory Management System - Spring Boot Backend
echo ====================================================================
echo Starting POS and Inventory Management System (Spring Boot 3.3.5)
echo Profile: DEV (Standalone with in-memory DB + seed data)
echo Port: 8080
echo ====================================================================

set SPRING_PROFILES_ACTIVE=dev
java -jar "target\pos-inventory-backend-1.0.0.jar"

pause
