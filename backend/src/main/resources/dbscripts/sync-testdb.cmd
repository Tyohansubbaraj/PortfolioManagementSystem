@echo off
REM ============================================
REM Portfolio Management System - Database Sync
REM ============================================

REM IMPORTANT:
REM Do NOT put quotes inside the variable value

SET MYSQL_BIN=C:\Program Files\MySQL\MySQL Server 8.0\bin
SET SOURCE_DB=portfolio
SET TARGET_DB=portfoliotest
SET SQL_FILE=portfolio_dump.sql
SET DB_USER=root

echo.
echo ============================================
echo Portfolio Management System
echo Database Dump & Refresh
echo ============================================
echo.
echo Source DB : %SOURCE_DB%
echo Target DB : %TARGET_DB%
echo SQL File  : %SQL_FILE%
echo.

REM ------------------------------------------------
REM STEP 1: Dump SOURCE database
REM ------------------------------------------------
echo [1/3] Dumping database %SOURCE_DB% ...
echo.

"%MYSQL_BIN%\mysqldump.exe" -u %DB_USER% -p --routines --triggers --events %SOURCE_DB% > "%SQL_FILE%"

IF ERRORLEVEL 1 (
    echo.
    echo [ERROR] Failed to dump source database
    echo.
    pause
    exit /b 1
)

echo [OK] Dump completed successfully.
echo.

REM ------------------------------------------------
REM STEP 2: Create TARGET database if not exists
REM ------------------------------------------------
echo [2/3] Creating database %TARGET_DB% if not exists ...
echo.

"%MYSQL_BIN%\mysql.exe" -u %DB_USER% -p -e "DROP DATABASE IF EXISTS %TARGET_DB%; CREATE DATABASE %TARGET_DB%;"

IF ERRORLEVEL 1 (
    echo.
    echo [ERROR] Failed to create target database
    echo.
    pause
    exit /b 1
)

echo [OK] Database %TARGET_DB% is ready.
echo.

REM ------------------------------------------------
REM STEP 3: Import dump into TARGET database
REM ------------------------------------------------
echo [3/3] Importing data into %TARGET_DB% ...
echo.

"%MYSQL_BIN%\mysql.exe" -u %DB_USER% -p %TARGET_DB% < "%SQL_FILE%"

IF ERRORLEVEL 1 (
    echo.
    echo [ERROR] Failed to import SQL file
    echo.
    pause
    exit /b 1
)

echo [OK] Data imported successfully.
echo.
echo ============================================
echo Database sync completed successfully!
echo ============================================
echo.
pause
