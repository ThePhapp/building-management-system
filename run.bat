@echo off
set JAVA_HOME=C:\Program Files\Java\jdk-1.8
set PATH=%JAVA_HOME%\bin;%PATH%

if exist .env (
    for /f "usebackq tokens=1,* delims==" %%i in (".env") do (
        set line=%%i
        if not "!line:~0,1!"=="#" (
            set %%i=%%j
        )
    )
)

echo Dang khoi dong Building Management System...
echo Truy cap tai: http://localhost:18080
"%JAVA_HOME%\bin\java.exe" -jar target/building-management-system.war
pause
