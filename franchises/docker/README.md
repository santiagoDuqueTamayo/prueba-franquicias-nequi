# Deploy del Proyecto Franchises

Este directorio contiene la configuración de Docker para desplegar el proyecto.

## Requisitos Previos

- Docker Desktop instalado y corriendo
- Docker Compose instalado (viene con Docker Desktop)

## Opciones de Deploy

### Opción 1: Deploy Local con Docker Compose (Recomendado)

Esta opción despliega MongoDB y la aplicación Spring Boot en contenedores Docker.

#### Pasos:

1. **Navegar al directorio docker:**
   ```bash
   cd franchises/docker
   ```

2. **Configurar variables de entorno (opcional):**
   - Copia `.env.example` a `.env` si quieres personalizar la configuración
   - Por defecto, ya existe un `.env` con valores predeterminados

3. **Construir y levantar los servicios:**
   ```bash
   docker-compose up -d --build
   ```

4. **Verificar que los servicios están corriendo:**
   ```bash
   docker-compose ps
   ```

5. **Ver los logs de la aplicación:**
   ```bash
   docker-compose logs -f franchises-app
   ```

6. **Probar la aplicación:**
   - API: http://localhost:8080
   - Health Check: http://localhost:8080/actuator/health
   - Mongo Express: http://localhost:8081

#### Comandos útiles:

- **Detener los servicios:**
  ```bash
  docker-compose down
  ```

- **Detener y eliminar volúmenes (limpia la base de datos):**
  ```bash
  docker-compose down -v
  ```

- **Reconstruir solo la aplicación:**
  ```bash
  docker-compose up -d --build franchises-app
  ```

- **Ver logs de todos los servicios:**
  ```bash
  docker-compose logs -f
  ```

### Opción 2: Solo MongoDB en Docker, App Local

Si prefieres ejecutar la aplicación localmente pero usar MongoDB en Docker:

1. **Levantar solo MongoDB:**
   ```bash
   docker-compose up -d mongodb mongo-express
   ```

2. **Configurar la aplicación para usar MongoDB local:**
   - Usa el perfil `local` en `application-local.yml`
   - O configura `MONGODB_URI=mongodb://admin:admin123@localhost:27017/franchises_db?authSource=admin`

3. **Ejecutar la aplicación localmente:**
   ```bash
   cd franchises
   mvn spring-boot:run -Dspring-boot.run.profiles=local
   ```

### Opción 3: Usar MongoDB Atlas (Cloud)

Si quieres usar MongoDB Atlas en lugar de MongoDB local:

1. **Obtén tu connection string de MongoDB Atlas**

2. **Edita el archivo `.env` y actualiza:**
   ```env
   MONGODB_URI=mongodb+srv://username:password@cluster.mongodb.net/franchises_db
   SPRING_PROFILES_ACTIVE=dev
   ```

3. **Levanta solo la aplicación (sin MongoDB local):**
   ```bash
   docker-compose up -d --build franchises-app
   ```

## Estructura de Servicios

- **mongodb**: Base de datos MongoDB en el puerto 27017
- **mongo-express**: Interfaz web para MongoDB en el puerto 8081
- **franchises-app**: Aplicación Spring Boot en el puerto 8080

## Troubleshooting

### La aplicación no se conecta a MongoDB

1. Verifica que MongoDB esté corriendo:
   ```bash
   docker-compose ps mongodb
   ```

2. Revisa los logs:
   ```bash
   docker-compose logs mongodb
   docker-compose logs franchises-app
   ```

3. Verifica la URI de conexión en `.env`

### Puerto ya en uso

Si el puerto 8080, 27017 o 8081 ya están en uso:

1. Edita `docker-compose.yml` y cambia los puertos
2. O detén el servicio que está usando ese puerto

### Reconstruir desde cero

```bash
docker-compose down -v
docker-compose up -d --build
```

## Endpoints Disponibles

Una vez desplegado, puedes probar:

- **Health Check:** `GET http://localhost:8080/actuator/health`
- **API Base:** `http://localhost:8080/api/v1/franchises`


