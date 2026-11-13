@echo off
cd /d "%~dp0"
java -jar IrisOptical.jar
if errorlevel 1 pause
