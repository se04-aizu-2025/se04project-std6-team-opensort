#!/bin/bash

# Create build directory if it doesn't exist
mkdir -p build

# Find all .java files recursively and write to sources.txt
echo "Collecting source files..."
find src -name "*.java" > sources.txt

# Compile using the @sources.txt list
echo "Building application..."
javac -d build @sources.txt

# Build the JAR file
echo "Creating jar file..."
jar cfe opensort.jar com.opensort.Main -C build .

echo "Build complete."

