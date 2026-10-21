param(
    [ValidateSet('TestConexion','TestPelicula','TestSala','TestFuncion','TestVenta','TestReporte','HelloApplication','Compilar')]
    [string]$Clase = 'TestConexion'
)
$ErrorActionPreference = 'Stop'
try {
    $cineRoot = $PSScriptRoot
    $cineJdk = 'C:\Program Files\Java\jdk-21'
    if (-not (Test-Path -LiteralPath "$cineJdk\bin\javac.exe")) {
        throw 'No se encontro JDK 21 en C:\Program Files\Java\jdk-21'
    }
    $cineOut = Join-Path $cineRoot 'target\classes'
    New-Item -ItemType Directory -Path $cineOut -Force | Out-Null
    $cineLib = Join-Path $cineRoot 'lib'
    $cineSources = Get-ChildItem -LiteralPath (Join-Path $cineRoot 'src\main\java') -Recurse -Filter '*.java'
    $cineArgs = Join-Path $cineRoot 'target\fuentes.txt'
    $cineLines = @($cineSources | ForEach-Object { '"' + $_.FullName.Replace('\','/') + '"' })
    [IO.File]::WriteAllLines($cineArgs, [string[]]$cineLines, [Text.UTF8Encoding]::new($false))
    & "$cineJdk\bin\javac.exe" -encoding UTF-8 --release 21 --module-path $cineLib -d $cineOut "@$cineArgs"
    if ($LASTEXITCODE -ne 0) { throw 'La compilacion fallo. Revise los mensajes anteriores.' }
    Copy-Item -Path (Join-Path $cineRoot 'src\main\resources\*') -Destination $cineOut -Recurse -Force
    Write-Host 'Compilacion correcta'
    if ($Clase -eq 'Compilar') { exit 0 }
    $cineMain = 'com.example.proyecto_final.'
    if ($Clase -ne 'HelloApplication') { $cineMain += 'test.' }
    $cineMain += $Clase
    & "$cineJdk\bin\java.exe" '-Dfile.encoding=UTF-8' --module-path "$cineOut;$cineLib" -m "com.example.proyecto_final/$cineMain"
    if ($LASTEXITCODE -ne 0) { throw 'La ejecucion fallo. Revise los mensajes anteriores.' }
} catch {
    Write-Host $_.Exception.Message -ForegroundColor Red
    exit 1
}
