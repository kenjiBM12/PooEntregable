# Empaqueta el sistema de cine con jpackage.
#   .\empaquetar.ps1                -> imagen de app + instalador .exe
#   .\empaquetar.ps1 -SoloImagen    -> solo la carpeta con el .exe (no requiere WiX)
#
# Requisitos: JDK 21 (jpackage) y, para el instalador, WiX Toolset v3 en el PATH
# o instalado en "C:\Program Files (x86)\WiX Toolset v3.14".

param(
    [string]$Version = "1.0",
    [switch]$SoloImagen
)

$ErrorActionPreference = "Stop"
$raiz = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $raiz

$modulo = "pe.edu.upeu.taller_colaborativo"
$nombreApp = "CineUPeU"
$jar = "target\taller_colaborativo-1.0-SNAPSHOT.jar"
$img = "src\main\resources\pe\edu\upeu\taller_colaborativo\img"
$ico = "$img\logo.ico"
$png = "$img\logo.png"

# 1. Ubicar jpackage (no siempre esta en el PATH)
$jpackage = "jpackage"
if (-not (Get-Command jpackage -ErrorAction SilentlyContinue)) {
    $candidato = Join-Path $env:JAVA_HOME "bin\jpackage.exe"
    if ($env:JAVA_HOME -and (Test-Path $candidato)) {
        $jpackage = $candidato
    } else {
        throw "No se encontro jpackage. Agrega %JAVA_HOME%\bin al PATH o define JAVA_HOME."
    }
}
Write-Host "jpackage: $jpackage"

# 2. Compilar y copiar las dependencias a target\libs
Write-Host "Compilando con Maven..."
& ".\mvnw.cmd" -B clean package
if ($LASTEXITCODE -ne 0) { throw "Fallo el build de Maven." }

# 3. Generar el .ico a partir del .png si no existe (jpackage no acepta .png)
if (-not (Test-Path $ico)) {
    Add-Type -AssemblyName System.Drawing
    $bmp = [System.Drawing.Bitmap]::FromFile((Resolve-Path $png))
    $icono = [System.Drawing.Icon]::FromHandle($bmp.GetHicon())
    $flujo = [System.IO.File]::Create((Join-Path $raiz $ico))
    $icono.Save($flujo)
    $flujo.Close()
    $icono.Dispose()
    $bmp.Dispose()
    Write-Host "Generado $ico"
}

$parametros = @(
    "--name", $nombreApp,
    "--app-version", $Version,
    "--vendor", "UPeU",
    "--dest", "target\dist",
    "--module-path", "target\libs;$jar",
    "--module", "$modulo/$modulo.HelloApplication",
    "--icon", $ico
)

Remove-Item "target\dist" -Recurse -Force -ErrorAction SilentlyContinue

Write-Host "Generando imagen de app..."
& $jpackage --type app-image @parametros
if ($LASTEXITCODE -ne 0) { throw "Fallo jpackage al crear la imagen de app." }
Write-Host "  target\dist\$nombreApp\$nombreApp.exe"

if (-not $SoloImagen) {
    Write-Host "Generando instalador .exe (necesita WiX Toolset v3)..."
    & $jpackage --type exe @parametros --win-shortcut --win-menu --win-dir-chooser
    if ($LASTEXITCODE -ne 0) {
        throw "Fallo jpackage al crear el instalador. Verifica que WiX Toolset v3 este instalado."
    }
    Write-Host "  target\dist\$nombreApp-$Version.exe"
}

Write-Host "Listo." -ForegroundColor Green
