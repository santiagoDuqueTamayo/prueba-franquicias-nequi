#!/bin/bash

# Script para desplegar el proyecto Franchises

echo "🚀 Iniciando deploy del proyecto Franchises..."

# Verificar que Docker está corriendo
if ! docker info > /dev/null 2>&1; then
    echo "❌ Error: Docker no está corriendo. Por favor inicia Docker Desktop."
    exit 1
fi

# Navegar al directorio docker
cd "$(dirname "$0")"

# Verificar si existe .env
if [ ! -f .env ]; then
    echo "📝 Creando archivo .env desde .env.example..."
    if [ -f .env.example ]; then
        cp .env.example .env
        echo "✅ Archivo .env creado. Puedes editarlo si necesitas cambiar la configuración."
    else
        echo "⚠️  No se encontró .env.example. Usando valores por defecto."
    fi
fi

# Construir y levantar los servicios
echo "🔨 Construyendo y levantando los servicios..."
docker-compose up -d --build

# Esperar a que los servicios estén listos
echo "⏳ Esperando a que los servicios estén listos..."
sleep 10

# Verificar el estado de los servicios
echo "📊 Estado de los servicios:"
docker-compose ps

# Verificar health check de la aplicación
echo ""
echo "🏥 Verificando health check de la aplicación..."
sleep 5
if curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo "✅ La aplicación está corriendo correctamente!"
    echo ""
    echo "📍 Endpoints disponibles:"
    echo "   - API: http://localhost:8080"
    echo "   - Health Check: http://localhost:8080/actuator/health"
    echo "   - Mongo Express: http://localhost:8081"
else
    echo "⚠️  La aplicación aún no está lista. Revisa los logs con:"
    echo "   docker-compose logs -f franchises-app"
fi

echo ""
echo "📝 Comandos útiles:"
echo "   - Ver logs: docker-compose logs -f"
echo "   - Detener: docker-compose down"
echo "   - Detener y limpiar: docker-compose down -v"


