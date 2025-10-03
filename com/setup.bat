@echo off
REM Setup script for CyberHub on Windows

echo Setting up CyberHub...

REM Create lib directory if it doesn't exist
if not exist "lib" mkdir lib

REM Check if MySQL Connector exists
if not exist "lib\mysql-connector-j-8.0.33.jar" (
    echo Downloading MySQL Connector/J...
    powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar' -OutFile 'lib\mysql-connector-j-8.0.33.jar'"
    echo MySQL Connector downloaded successfully!
) else (
    echo MySQL Connector already exists.
)

REM Create output directory
if not exist "out" mkdir out

echo Setup complete!
echo.
echo Next steps:
echo 1. Set up the database: mysql -u root -p ^< ..\database_schema.sql
echo 2. Update database credentials in src\com\yourcompany\cyberhub\util\DatabaseConnector.java
echo 3. Compile and run using your IDE or command line
pause
