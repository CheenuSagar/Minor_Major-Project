@echo off
title NextStore - Spring Boot Backend
echo ====================================================================
echo Starting NextStore (Spring Boot 3.3.5)
echo Profile: DEV (Standalone with in-memory DB + seed data)
echo Port: 8080
echo ====================================================================

set SPRING_PROFILES_ACTIVE=dev
java -jar "target\nextstore-backend.jar" --spring.profiles.active=dev

pause
