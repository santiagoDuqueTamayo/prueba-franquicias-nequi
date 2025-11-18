# 🚀 Franchises Service — Prueba Técnica Backend

Servicio backend reactivo para la gestión de franquicias, sucursales y productos.
Desarrollado con **Spring Boot 3.5.7**, **Spring WebFlux**, **MongoDB Atlas**, **Docker**, **Railway** y **Terraform**.

---

## 📘 Tabla de Contenido

1. [Arquitectura y Tecnologías](#-arquitectura-y-tecnologías)
2. [Modelo de Dominio](#-modelo-de-dominio---franchises-como-agregado-raíz)
3. [Requisitos Previos](#-requisitos-previos)
4. [Configuración de Variables de Entorno](#-configuración-de-variables-de-entorno)
5. [Ejecución Local](#-ejecución-local)
6. [Ejecución con Docker](#-ejecución-con-docker)
7. [Despliegue en Railway](#-despliegue-en-railway)
8. [Probar la API desde Postman](#-probar-la-api-desde-postman)
9. [Colección Postman incluida](#-colección-postman-incluida)
10. [Notas para Evaluadores](#-notas-para-evaluadores)

---

# 🧱 Arquitectura y Tecnologías

### **Backend**

* **Java 17**
* **Spring Boot 3.5.7**
* **Spring WebFlux (programación reactiva)**
* **MongoDB Reactive Driver**
* **Maven**

### **Infraestructura & DevOps**

* **Docker**
* **Docker Compose**
* **Railway**
* **Terraform**
* **GitHub**

---

# 🧩 Modelo de Dominio — Franchises como Agregado Raíz

En esta solución, **Franchise** es la entidad agregadora. Toda operación del dominio se realiza *a través de ella*:
* Crear franquicias
* Crear sucursal
* Crear producto a través de la sucursal
* Eliminar productos
* Actualizar stock
* Actualizar nombre del producto
* Actualizar nombre de la franquicia
* Actualizar nombre de la sucursal
* Consultar productos top por sucursal
* Listar franquicias (con branches y products esto no es un caso de uso pero es para efectos de pruebas y visualizacion)

Esto garantiza consistencia transaccional dentro del agregado, siguiendo principios DDD.

### Estructura conceptual

```
Franchise
 └── Branch
       └── Product
```

MongoDB almacena este agregado como **documento único por franquicia**.

---

# 🔧 Requisitos Previos para desarrollo

| Herramienta             | Versión Recomendada |
| ----------------------- | ------------------- |
| Java                    | 17+                 |
| Maven                   | 3.9+                |
| Docker                  | 24+                 |
| Docker Compose          | 2+                  |
| Postman                 | Última versión      |
| Cuenta de MongoDB Atlas | Obligatoria         |
| Terraform               | Obligatoria         |
| Cuenta git              | Obligatoria         |
| Cuenta railway          | Obligatoria         |

---

# 🧪 Probar la API desde Postman

La api se puede probar en la nube railway, la colección esta diseñada para que al ejecutar el endpoint inicial crear franquicia, el id de respuesta de esa franquicia sea incluido en el pathvariable que existen en el resto de endpoints.

Incluyo:

*✔️ **Colección Postman con todos los endpoints**
*✔️ **Ambiente con variables**: `{{baseUrl}}`, `{{franchiseId}}`, etc
*✔️ Soporte para cambiar entre **Local** y **int**
*✔️ Endpoints para operaciones completas del dominio

### Cambiar entorno

En Postman:

`Environments → Select → Local`
`Environments → Select → int (aca se encuentra la nube)`

### Variables:

```
baseUrl = http://localhost:8080
baseUrl = https://docker-franchises-app-production.up.railway.app
```

---

# 📁 Colección Postman incluida

El repositorio contiene:

```
postman-colection/
 ├── franchises-collection.json
 └── int.postman_environment.json
 └── localhost.postman_environment.json
```

La colección incluye:

| Método | Endpoint                                     | Descripción             |
| ------ | ---------------------------------------------| ----------------------- |
| POST    | `/api/v1/franchises`                        | crear franquicias      |
| POST   | `/api/v1/franchises/{{franchiseId}}/branches`| crear sucursal        |
| POST | `/api/v1/franchises/{{franchiseId}}/products`| crear producto       |
| PUT    | `/api/v1/franchises/{{franchiseId}}/products/stock`  | Actualizar STOCK       |
| PUT    | `/api/v1/franchises/{{franchiseId}}/products/name` | Actualizar nombre producto        |
| PUT    | `/api/v1/franchises/{{franchiseId}}/name`   | Actualizar nombre franquicia |
| PUT    | `/api/v1/franchises/{{franchiseId}}/branches/name`   | Actualizar nombre SUCURSAL |
| GET    | `/api/v1/franchises/{{franchiseId}}/products/top`   | Obtener top productos |
| GET    | `/api/v1/franchises/list`   | Obtener top productos |


---

# 🔑 Configuración de Variables de Entorno (Solo si se van a hacer pruebas en local)

Crear archivo:

```
.env
```

Contenido:

```
MONGODB_URI=mongodb+srv://admin:%7Bm%3AMN%288F%5B0I%3DIH%28G@franchies-cluster.ttjhutb.mongodb.net/?retryWrites=true&w=majority&appName=franchies-cluster
```

---



# 🐳 Ejecución con Docker

### 1. Construir la imagen

```bash
docker build -t franchises-app:latest .
```

### 2. Ejecutar

```bash
docker run -p 8080:8080 \
  -e MONGODB_URI="mongodb+srv://..." \
  franchises-app:latest
```
# ▶️ Ejecución Local

```bash
mvn clean install
export MONGODB_URI="mongodb+srv://..."
mvn spring-boot:run
```

### Verificar

```
http://localhost:8080/api/v1/franchises/list
```

---

---

# 🚀 Despliegue en Railway

Railway permite desplegar aplicaciones por **imagen Docker cargada manualmente**.

### Pasos para desplegar una nueva versión:

1. Generar imagen local:

   ```bash
   docker build -t franchises-app:latest .
   ```
2. Crear un archivo `.tar`:

   ```bash
   docker save franchises-app:latest > franchises-app.tar
   ```
3. Subir el archivo desde Railway → Deploy → Deploy from Docker Image
4. Configurar variables de entorno:

   * `MONGODB_URI`
   * `SERVER_PORT` (Railway asigna uno)
5. Esperar build y verificar logs.

Una vez desplegado, Railway expone un dominio público. Ejemplo:

```
curl --location 'https://docker-franchises-app-production.up.railway.app/api/v1/franchises/list'
```

---


# 📝 Notas para Evaluadores

* También tengo una infraestructura en AWS con EC2 + ECS para el despliegue, pero hubo errores intentando conectar atlasmongoDB
* terrafom aproviciona la BD atlas mongoDB, pero si se ejecuta el terraform apply aparecerá error porque la BD ya existe en la nube.
* La arquitectura sigue **DDD ligero**.
* El servicio es completamente **reactivo**, sin usar API bloqueante.
* MongoDB se maneja con **ReactiveMongoRepository**.
* La entidad **Franchise es el agregado raíz** y garantiza consistencia del dominio.
* Todas las pruebas recomendadas están en la colección Postman.
* El deploy en Railway está basado en **Docker image **
