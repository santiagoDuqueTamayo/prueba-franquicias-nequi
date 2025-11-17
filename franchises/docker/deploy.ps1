# Script PowerShell para desplegar el proyecto Franchises

Write-Host "🚀 Iniciando deploy del proyecto Franchises..." -ForegroundColor Cyan

# Verificar que Docker está corriendo
try {
    docker info | Out-Null
} catch {
    Write-Host "❌ Error: Docker no está corriendo. Por favor inicia Docker Desktop." -ForegroundColor Red
    exit 1
}

# Navegar al directorio docker
Set-Location $PSScriptRoot

# Verificar si existe .env
if (-not (Test-Path .env)) {
    Write-Host "📝 Creando archivo .env desde .env.example..." -ForegroundColor Yellow
    if (Test-Path .env.example) {
        Copy-Item .env.example .env
        Write-Host "✅ Archivo .env creado. Puedes editarlo si necesitas cambiar la configuración." -ForegroundColor Green
    } else {
        Write-Host "⚠️  No se encontró .env.example. Usando valores por defecto." -ForegroundColor Yellow
    }
}

# Construir y levantar los servicios
Write-Host "🔨 Construyendo y levantando los servicios..." -ForegroundColor Cyan
docker-compose up -d --build

# Esperar a que los servicios estén listos
Write-Host "⏳ Esperando a que los servicios estén listos..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

# Verificar el estado de los servicios
Write-Host "📊 Estado de los servicios:" -ForegroundColor Cyan
docker-compose ps

# Verificar health check de la aplicación
Write-Host ""
Write-Host "🏥 Verificando health check de la aplicación..." -ForegroundColor Cyan
Start-Sleep -Seconds 5

try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -UseBasicParsing -TimeoutSec 5
    if ($response.StatusCode -eq 200) {
        Write-Host "✅ La aplicación está corriendo correctamente!" -ForegroundColor Green
        Write-Host ""
        Write-Host "📍 Endpoints disponibles:" -ForegroundColor Cyan
        Write-Host "   - API: http://localhost:8080"
        Write-Host "   - Health Check: http://localhost:8080/actuator/health"
        Write-Host "   - Mongo Express: http://localhost:8081"
    }
} catch {
    Write-Host "⚠️  La aplicación aún no está lista. Revisa los logs con:" -ForegroundColor Yellow
    Write-Host "   docker-compose logs -f franchises-app"
}

Write-Host ""
Write-Host "📝 Comandos útiles:" -ForegroundColor Cyan
Write-Host "   - Ver logs: docker-compose logs -f"
Write-Host "   - Detener: docker-compose down"
Write-Host "   - Detener y limpiar: docker-compose down -v"


