# Create build directory if it doesn't exist
if (-not (Test-Path build)) { New-Item -Path build -ItemType Directory }

# Find all .java files recursively and write to sources.txt
Write-Host "Collecting source files..."
(Get-ChildItem -Path src -Filter "*.java" -Recurse).FullName | Out-File -FilePath sources.txt -Encoding utf8

# Compile using the @sources.txt list
Write-Host "Building application..."
javac -d build @sources.txt

# Build the JAR file
Write-Host "Creating jar file..."
jar cfe opensort.jar com.opensort.Main -C build .

Write-Host "Build complete."
