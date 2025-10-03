@echo off
REM Run script for CyberHub on Windows

REM Check if compiled
if not exist "out" (
    echo Application not compiled. Running compile.bat...
    call compile.bat
)

echo Starting CyberHub...
java -cp "out;lib\mysql-connector-j-8.0.33.jar" com.yourcompany.cyberhub.Main
