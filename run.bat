@echo off
echo =================================
echo  DUNGEON RPG - Build and Run
echo =================================
echo.

echo [1/3] Git pull...
git pull
if %ERRORLEVEL% NEQ 0 (
    echo [ERRORE] git pull fallito!
    pause
    exit /b 1
)

echo.
echo [2/3] Build...
call gradlew.bat build
if %ERRORLEVEL% NEQ 0 (
    echo [ERRORE] Build fallita!
    pause
    exit /b 1
)

echo.
echo [3/3] Avvio gioco...
call gradlew.bat run

pause
