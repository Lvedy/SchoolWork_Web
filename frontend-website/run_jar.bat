@echo off
echo =================================================
echo Pig Website - JAR Run Script
echo =================================================

if not exist "pig-website.jar" (
    echo Cannot find pig-website.jar file!
    echo Please run build_jar.bat first to create JAR package
    pause
    exit /b 1
)

echo Starting Web Server...
echo JAR file: pig-website.jar
echo.

java -jar pig-website.jar

echo.
echo Server stopped
pause