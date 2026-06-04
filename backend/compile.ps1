$M2_REPO = "C:\Users\唐大帅\.m2\repository"

# Get all JAR files from Maven repository
$jars = Get-ChildItem -Path $M2_REPO -Recurse -Filter "*.jar" | Select-Object -ExpandProperty FullName

# Add target/classes to classpath
$classpathItems = @("target/classes") + $jars

# Join into classpath string
$classpath = $classpathItems -join ";"

Write-Host "Found $($jars.Count) JAR files"
Write-Host "Classpath length: $($classpath.Length)"

# Compile ReportServiceImpl.java
& "E:\谷歌下载\jsp\bin\javac.exe" -d target/classes -proc:none -cp $classpath src/main/java/com/example/hospital/service/impl/ReportServiceImpl.java

if ($LASTEXITCODE -eq 0) {
    Write-Host "Compilation successful!"
} else {
    Write-Host "Compilation failed with exit code $LASTEXITCODE"
}