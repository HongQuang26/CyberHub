@echo off
REM Compile script for CyberHub on Windows

echo Compiling CyberHub...

REM Create output directory
if not exist "out" mkdir out

REM Compile all Java files
dir /s /B src\*.java > sources.txt
javac -cp "lib\mysql-connector-j-8.0.33.jar" -d out @sources.txt
del sources.txt

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
    echo To run the application, use: run.bat
) else (
    echo Compilation failed!
    exit /b 1
)
