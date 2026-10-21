@echo off
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0ejecutar.ps1" -Clase HelloApplication
if errorlevel 1 pause
