@echo off
setlocal

:: Configuration
set MAIN_CLASS=org.martin.Main
set SKIP_TESTS=false

:: Build command based on whether tests are skipped
if "%SKIP_TESTS%"=="true" (
    set BUILD_CMD=mvn clean install -DskipTests
) else (
    set BUILD_CMD=mvn clean install
)

echo ==> Building project...
call %BUILD_CMD%
if %ERRORLEVEL% neq 0 (
    echo Build failed!
    pause
    exit /b %ERRORLEVEL%
)

echo ==> Starting Spark server...
mvn exec:java -Dexec.mainClass="%MAIN_CLASS%"

pause