@echo off
echo =================================================
echo Pig Website - JAR Build Script
echo =================================================

echo Current Directory: %cd%
echo Compiling Java source files...

:: Compile Java files
javac -cp . src\com\lvedy\SimpleWebServer.java

if %errorlevel% neq 0 (
    echo Compilation failed! Please check Java environment.
    pause
    exit /b 1
)

echo Compilation successful!
echo Creating JAR package...

:: Create temp directory structure
if not exist "temp" mkdir temp
if not exist "temp\com\lvedy" mkdir temp\com\lvedy

:: Copy class files
copy "src\com\lvedy\*.class" "temp\com\lvedy\"

:: Copy static resource files
xcopy "src\com\lvedy\html" "temp\com\lvedy\html\" /E /I /Y
xcopy "src\com\lvedy\CSS" "temp\com\lvedy\CSS\" /E /I /Y
xcopy "src\com\lvedy\JavaScript" "temp\com\lvedy\JavaScript\" /E /I /Y
xcopy "src\com\lvedy\image" "temp\com\lvedy\image\" /E /I /Y

:: Create MANIFEST.MF file
echo Manifest-Version: 1.0 > temp\MANIFEST.MF
echo Main-Class: com.lvedy.SimpleWebServer >> temp\MANIFEST.MF
echo. >> temp\MANIFEST.MF

:: Package JAR file using Java's jar tool
cd temp

:: Try to use jar command from JAVA_HOME
if defined JAVA_HOME (
    "%JAVA_HOME%\bin\jar" cfm ..\pig-website.jar MANIFEST.MF com\
) else (
    :: Try to find jar in PATH or use alternative method
    where jar >nul 2>&1
    if %errorlevel% equ 0 (
        jar cfm ..\pig-website.jar MANIFEST.MF com\
    ) else (
        echo Warning: jar command not found in PATH or JAVA_HOME
        echo Creating JAR manually using zip method...
        
        :: Create JAR using PowerShell (alternative method)
        powershell -Command "Add-Type -AssemblyName System.IO.Compression.FileSystem; [System.IO.Compression.ZipFile]::CreateFromDirectory('.', '../pig-website.jar')"
    )
)

cd ..

:: Clean temp files
rmdir /s /q temp

if exist "pig-website.jar" (
    echo JAR package created successfully: pig-website.jar
    echo File size:
    dir pig-website.jar | findstr pig-website.jar
    echo.
    echo Run JAR command:
    echo java -jar pig-website.jar
    echo.
    echo Access URL: http://localhost:8080
) else (
    echo JAR package creation failed!
)

echo =================================================
pause