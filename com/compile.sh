#!/bin/bash
# Compile script for CyberHub

echo "Compiling CyberHub..."

# Create output directory
mkdir -p out

# Compile all Java files
javac -cp "lib/mysql-connector-j-8.0.33.jar" -d out $(find src -name "*.java")

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo "To run the application, use: ./run.sh"
else
    echo "Compilation failed!"
    exit 1
fi
