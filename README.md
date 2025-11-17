# prueba-franquicias-nequi
El objetivo de este repositorio es desarrollar una prueba técnica de ingreso para la empresa nequi en la que se propone generar un sistema básico para franquicias

## 🚀 Deploy Rápido

### Opción 1: Deploy con Docker Compose (Recomendado)

La forma más fácil de desplegar el proyecto es usando Docker Compose:

**Windows (PowerShell):**
```powershell
cd franchises/docker
.\deploy.ps1
```

**Linux/Mac:**
```bash
cd franchises/docker
chmod +x deploy.sh
./deploy.sh
```

**O manualmente:**
```bash
cd franchises/docker
docker-compose up -d --build
```

### Opción 2: Ejecutar Localmente

Si prefieres ejecutar la aplicación localmente:

1. **Levantar MongoDB con Docker:**
   ```bash
   cd franchises/docker
   docker-compose up -d mongodb mongo-express
   ```

2. **Ejecutar la aplicación:**
   ```bash
   cd franchises
   mvn spring-boot:run -Dspring-boot.run.profiles=local
   ```

### Verificar el Deploy

Una vez desplegado, puedes verificar que todo funciona:

- **API:** http://localhost:8080
- **Health Check:** http://localhost:8080/actuator/health
- **Mongo Express:** http://localhost:8081 (usuario: admin, contraseña: admin)

### Documentación Completa

Para más detalles sobre el deploy, configuración y troubleshooting, consulta:
- [Documentación de Docker](franchises/docker/README.md)

## 📋 Requisitos

- Java 17
- Maven 3.9+
- Docker Desktop (para deploy con Docker)
- MongoDB (local o Atlas)

## 🛠️ Tecnologías

- Spring Boot 3.5.7
- Spring WebFlux (Reactivo)
- MongoDB Reactive
- Docker & Docker Compose
- Maven