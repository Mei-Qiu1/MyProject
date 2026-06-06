$M2_REPO = "C:\Users\唐大帅\.m2\repository"

# Get all JAR files from Maven repository
$jars = Get-ChildItem -Path $M2_REPO -Recurse -Filter "*.jar" | Select-Object -ExpandProperty FullName

# Add target/classes to classpath
$classpathItems = @("target/classes") + $jars

# Join into classpath string
$classpath = $classpathItems -join ";"

Write-Host "Found $($jars.Count) JAR files"
Write-Host "Classpath length: $($classpath.Length)"

# Get all Java source files
$javaFiles = Get-ChildItem -Path "src/main/java" -Recurse -Filter "*.java" | Select-Object -ExpandProperty FullName

Write-Host "Found $($javaFiles.Count) Java source files"

# Create target/classes directory if it doesn't exist
if (-not (Test-Path "target/classes")) {
    New-Item -ItemType Directory -Path "target/classes" -Force | Out-Null
}

# Compile all Java files
& "E:\谷歌下载\jsp\bin\javac.exe" -d target/classes -cp $classpath $javaFiles

if ($LASTEXITCODE -eq 0) {
    Write-Host "Compilation successful!"
} else {
    Write-Host "Compilation failed with exit code $LASTEXITCODE"
}