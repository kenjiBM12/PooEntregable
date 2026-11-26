@echo off
REM ============================================================
REM MEJORA 10: generar el .exe del sistema con jpackage
REM ------------------------------------------------------------
REM jpackage viene incluido en el JDK 21 (no hay que instalar nada).
REM El resultado es una carpeta con el programa y su propio Java:
REM no hace falta tener Java instalado en la otra computadora.
REM
REM Uso:
REM   1. Cerrar el programa si esta abierto (si el .exe anterior sigue
REM      corriendo, Windows no deja borrar target\dist).
REM   2. Doble clic en este archivo, o desde consola: empaquetar_exe.bat
REM   3. El ejecutable queda en:
REM        target\exe\SistemaCineUPeU\SistemaCineUPeU.exe
REM
REM Nota: NO se usa "mvnw clean" a proposito. Si quedo una ventana
REM abierta del ejecutable anterior, la carpeta target\dist esta en uso
REM y "clean" falla. Con "package" se recompila igual sin borrar todo.
REM ============================================================

setlocal

REM Java 21: si JAVA_HOME no esta definido, se usa el del sistema
if "%JAVA_HOME%"=="" (
    set JPACKAGE=jpackage
    set JAVA_BIN=java
) else (
    set JPACKAGE=%JAVA_HOME%\bin\jpackage
    set JAVA_BIN=%JAVA_HOME%\bin\java
)

echo [1/3] Compilando el proyecto...
call mvnw.cmd -DskipTests package
if errorlevel 1 goto error

echo [2/3] Copiando el jar del proyecto junto a sus dependencias...
if not exist target\libs mkdir target\libs
copy /Y target\gestioo_de_cine-1.0-SNAPSHOT.jar target\libs\
if errorlevel 1 goto error

echo [3/3] Generando el ejecutable...
"%JPACKAGE%" --type app-image ^
  --name SistemaCineUPeU ^
  --input target\libs ^
  --main-jar gestioo_de_cine-1.0-SNAPSHOT.jar ^
  --main-class pe.edu.upeu.gestioo_de_cine.Launcher ^
  --dest target\exe
if errorlevel 1 goto error

echo.
echo LISTO. El ejecutable esta en:
echo   target\exe\SistemaCineUPeU\SistemaCineUPeU.exe
goto fin

:error
echo.
echo ERROR: no se pudo generar el ejecutable. Revise el mensaje de arriba.

:fin
endlocal
pause
