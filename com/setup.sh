#!/bin/bash
# Setup script for CyberHub

echo "Setting up CyberHub..."

# Create lib directory if it doesn't exist
mkdir -p lib

# Download MySQL Connector/J if not present
if [ ! -f "lib/mysql-connector-j-8.0.33.jar" ]; then
    echo "Downloading MySQL Connector/J..."
    wget -P lib https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar
    echo "MySQL Connector downloaded successfully!"
else
    echo "MySQL Connector already exists."
fi

# Create output directory
mkdir -p out

echo "Setup complete!"
echo ""
echo "Next steps:"
echo "1. Set up the database: mysql -u root -p < ../database_schema.sql"
echo "2. Update database credentials in src/com/yourcompany/cyberhub/util/DatabaseConnector.java"
echo "3. Compile: javac -cp \"lib/mysql-connector-j-8.0.33.jar\" -d out \$(find src -name \"*.java\")"
echo "4. Run: java -cp \"out:lib/mysql-connector-j-8.0.33.jar\" com.yourcompany.cyberhub.Main"
