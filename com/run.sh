#!/bin/bash
# Run script for CyberHub

# Check if compiled
if [ ! -d "out" ]; then
    echo "Application not compiled. Running compile.sh..."
    ./compile.sh
fi

echo "Starting CyberHub..."
java -cp "out:lib/mysql-connector-j-8.0.33.jar" com.yourcompany.cyberhub.Main
