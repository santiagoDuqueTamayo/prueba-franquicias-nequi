# 1. Levantar MongoDB con Docker
cd docker
docker-compose up -d

# 2. Verificar que MongoDB está corriendo
docker ps

# 3. Acceder a Mongo Express (opcional)
http://localhost:8081

# 4. Ejecutar la aplicación Spring Boot
mvn spring-boot:run

# 5. Probar el endpoint
curl -X POST http://localhost:8080/api/v1/franchises/{franchiseId}/products \
  -H "Content-Type: application/json" \
  -d '{
    "branchName": "Sucursal Centro",
    "productName": "Hamburguesa",
    "stock": 50
  }'