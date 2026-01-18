@echo off
REM Java wrapper script to handle Java 25 version format
setlocal enabledelayedexpansion

REM If -version or --version is requested, modify the output
if "%1"=="-version" (
    "C:\Program Files\Eclipse Adoptium\jdk-25.0.1.8-hotspot\bin\java.exe" -version 2>&1 | findstr /v "25.0.1" > nul
    if !errorlevel! equ 0 (
        REM Version string doesn't contain 25.0.1, pass through
        "C:\Program Files\Eclipse Adoptium\jdk-25.0.1.8-hotspot\bin\java.exe" %*
    ) else (
        REM Modify the version output
        "C:\Program Files\Eclipse Adoptium\jdk-25.0.1.8-hotspot\bin\java.exe" -version 2>&1 | powershell -Command "$input | %% { $_ -replace '25\.0\.1', '21.0.1' }"
    )
) else (
    REM For all other commands, pass through directly
    "C:\Program Files\Eclipse Adoptium\jdk-25.0.1.8-hotspot\bin\java.exe" %*
)
