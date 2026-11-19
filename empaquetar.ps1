# Empaqueta el sistema de cine con jpackage.
#   .\empaquetar.ps1                -> imagen de app + instalador .exe
#   .\empaquetar.ps1 -SoloImagen    -> solo la carpeta con el .exe (no requiere WiX)
#
# Requisitos: JDK 17 o superior (jpackage) y, para el instalador, WiX Toolset v3.

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

# Genera un .ico valido (entradas BMP de 16 a 256 px) a partir del .png.
# Windows rechaza el .ico que devuelve Bitmap.GetHicon para tamanos pequenos.
function New-Icono {
    param([string]$entrada, [string]$salida)

    Add-Type -AssemblyName System.Drawing
    $origen = [System.Drawing.Bitmap]::FromFile($entrada)
    $tamanos = @(16, 24, 32, 48, 64, 128, 256)
    $bloques = @()

    foreach ($t in $tamanos) {
        $bmp = New-Object System.Drawing.Bitmap($t, $t, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
        $g = [System.Drawing.Graphics]::FromImage($bmp)
        $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
        $g.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
        $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
        $g.DrawImage($origen, 0, 0, $t, $t)
        $g.Dispose()

        $rect = New-Object System.Drawing.Rectangle(0, 0, $t, $t)
        $datos = $bmp.LockBits($rect, [System.Drawing.Imaging.ImageLockMode]::ReadOnly, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
        $stride = $datos.Stride
        $linea = New-Object byte[] ($stride * $t)
        [System.Runtime.InteropServices.Marshal]::Copy($datos.Scan0, $linea, 0, $linea.Length)
        $bmp.UnlockBits($datos)
        $bmp.Dispose()

        # el ICO guarda los pixeles de abajo hacia arriba en BGRA
        $fila = $t * 4
        $pixeles = New-Object byte[] ($fila * $t)
        for ($y = 0; $y -lt $t; $y++) {
            [Array]::Copy($linea, ($t - 1 - $y) * $stride, $pixeles, $y * $fila, $fila)
        }

        $ms = New-Object System.IO.MemoryStream
        $w = New-Object System.IO.BinaryWriter($ms)
        $w.Write([int]40)
        $w.Write([int]$t)
        $w.Write([int]($t * 2))
        $w.Write([int16]1)
        $w.Write([int16]32)
        $w.Write([int]0)
        $w.Write([int]($fila * $t))
        $w.Write([int]0)
        $w.Write([int]0)
        $w.Write([int]0)
        $w.Write([int]0)
        $w.Write($pixeles)

        $filaMascara = [int]([math]::Ceiling($t / 8.0))
        if ($filaMascara % 4 -ne 0) { $filaMascara += 4 - ($filaMascara % 4) }
        $w.Write((New-Object byte[] ($filaMascara * $t)))
        $w.Flush()

        $bloques += , @($t, $ms.ToArray())
        $w.Dispose()
        $ms.Dispose()
    }

    $salidaMs = New-Object System.IO.MemoryStream
    $bw = New-Object System.IO.BinaryWriter($salidaMs)
    $bw.Write([int16]0)
    $bw.Write([int16]1)
    $bw.Write([int16]$bloques.Count)

    $offset = 6 + 16 * $bloques.Count
    foreach ($b in $bloques) {
        $t = $b[0]
        $ancho = 0
        if ($t -lt 256) { $ancho = $t }
        $bw.Write([byte]$ancho)
        $bw.Write([byte]$ancho)
        $bw.Write([byte]0)
        $bw.Write([byte]0)
        $bw.Write([int16]1)
        $bw.Write([int16]32)
        $bw.Write([int]$b[1].Length)
        $bw.Write([int]$offset)
        $offset += $b[1].Length
    }
    foreach ($b in $bloques) {
        $bw.Write($b[1])
    }
    $bw.Flush()

    [System.IO.File]::WriteAllBytes($salida, $salidaMs.ToArray())
    $bw.Dispose()
    $salidaMs.Dispose()
    $origen.Dispose()
}

# 1. Ubicar jpackage (normalmente no esta en el PATH)
$jpackage = $null
if (Get-Command jpackage -ErrorAction SilentlyContinue) {
    $jpackage = "jpackage"
}
if (-not $jpackage -and $env:JAVA_HOME) {
    $candidato = Join-Path $env:JAVA_HOME "bin\jpackage.exe"
    if (Test-Path $candidato) { $jpackage = $candidato }
}
if (-not $jpackage) {
    $carpetas = @(
        "$env:ProgramFiles\Java",
        "$env:ProgramFiles\Eclipse Adoptium",
        "$env:ProgramFiles\Amazon Corretto",
        "$env:ProgramFiles\Microsoft",
        "$env:ProgramFiles\Zulu",
        "$env:LOCALAPPDATA\Programs\Eclipse Adoptium"
    )
    foreach ($carpeta in $carpetas) {
        if (-not (Test-Path $carpeta)) { continue }
        $encontrado = Get-ChildItem -Path $carpeta -Filter "jpackage.exe" -Recurse -ErrorAction SilentlyContinue |
            Where-Object { $_.FullName -match '\\bin\\jpackage\.exe$' } |
            Sort-Object FullName -Descending |
            Select-Object -First 1
        if ($encontrado) { $jpackage = $encontrado.FullName; break }
    }
}
if (-not $jpackage) {
    throw "No se encontro jpackage. Instala un JDK 17 o superior y agrega su carpeta bin al PATH."
}
Write-Host "jpackage: $jpackage"

# mvnw.cmd exige JAVA_HOME: lo deducimos del jpackage encontrado
if ($jpackage -eq "jpackage") {
    $jdk = Split-Path (Split-Path (Get-Command java).Source -Parent) -Parent
} else {
    $jdk = Split-Path (Split-Path $jpackage -Parent) -Parent
}
if ($jdk -and (Test-Path (Join-Path $jdk "bin\java.exe"))) {
    $env:JAVA_HOME = $jdk
}

# 2. Compilar y copiar las dependencias a target\libs
Write-Host "Compilando con Maven..."
& ".\mvnw.cmd" -B clean package
if ($LASTEXITCODE -ne 0) { throw "Fallo el build de Maven." }

# 3. Generar el .ico a partir del .png si no existe (jpackage no acepta .png)
if (-not (Test-Path $ico)) {
    New-Icono -entrada (Resolve-Path $png).Path -salida (Join-Path $raiz $ico)
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
    & $jpackage --type exe @parametros --win-shortcut --win-menu --win-per-user-install
    if ($LASTEXITCODE -ne 0) {
        throw "Fallo jpackage al crear el instalador. Verifica que WiX Toolset v3 este instalado."
    }
    Write-Host "  target\dist\$nombreApp-$Version.exe"
}

Write-Host "Listo." -ForegroundColor Green
