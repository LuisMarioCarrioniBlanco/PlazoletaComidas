# Backend Plazoleta de Comidas — HU1 a HU9

Backend sin frontend. El ZIP contiene dos microservicios independientes, cada uno con su propio `pom.xml` y `Dockerfile`; se agrupan en este directorio solo para facilitar la descarga y las pruebas iniciales:

- **usuarios-service** (`8081`): cuentas de propietario, cliente y empleado; login, BCrypt y emisión de JWT.
- **plazoleta-service** (`8082`): restaurantes, platos, control de propiedad, empleado asociado a un restaurante y listado paginado de restaurantes.
- **MySQL**: cada microservicio tiene su propia base de datos.
- **OpenAPI / Swagger**: documentación automática en cada servicio.

Se mantiene la arquitectura hexagonal solicitada: `domain`, `application/port/input`, `application/port/output`, `application/service` e `infrastructure/input/rest`, `infrastructure/output/persistence` y `infrastructure/configuration`. Las entidades de negocio no dependen de JPA ni de Spring.

## Alcance implementado

| HU | Funcionalidad | Endpoint principal |
|---|---|---|
| HU1 | Crear propietario (solo administrador), validar mayoría de edad y datos | `POST /api/v1/usuarios/propietarios` |
| HU2 | Crear restaurante y validar que el propietario exista y tenga el rol correcto | `POST /api/v1/restaurantes` |
| HU3 | Crear plato en un restaurante propio; empieza activo | `POST /api/v1/restaurantes/{restauranteId}/platos` |
| HU4 | Modificar exclusivamente precio y descripción | `PATCH /api/v1/restaurantes/{restauranteId}/platos/{platoId}` |
| HU5 | Login por correo y clave, JWT y permisos por rol | `POST /api/v1/auth/login` |
| HU6 | Crear empleado asociado a un restaurante propio | `POST /api/v1/restaurantes/{restauranteId}/empleados` |
| HU7 | Activar o desactivar un plato del restaurante propio | `PATCH /api/v1/restaurantes/{restauranteId}/platos/{platoId}/estado` |
| HU8 | Crear cuenta de cliente con rol fijo CLIENTE | `POST /api/v1/usuarios/clientes` |
| HU9 | Listar restaurantes en orden alfabético y paginados, mostrando nombre y logo | `GET /api/v1/restaurantes?page=0&size=10` |

Los pedidos, el listado de platos para clientes (HU10), Twilio/SMS y trazabilidad no se implementan porque están después de HU9. No se incorpora frontend ni flujo de pagos.

## Requisitos

- Java 21
- Maven 3.9 o superior, o Docker con Docker Compose
- Conexión a internet la primera vez que Maven descargue dependencias, si ejecutas el proyecto sin Docker

## Ejecutar con Docker

1. Copia `.env.example` como `.env` (`copy .env.example .env` en CMD/PowerShell, o `cp .env.example .env` en Linux/macOS).
2. Revisa las variables de entorno. Las credenciales que aparecen por defecto son **solo para desarrollo local**.
3. Desde esta carpeta ejecuta:

   ```bash
   docker compose --env-file .env up --build
   ```

   Si tu versión de Docker Compose ya carga automáticamente `.env`, también puede usarse `docker compose up --build`.

Servicios disponibles:

- Usuarios: `http://localhost:8081`
- Plazoleta: `http://localhost:8082`
- Swagger Usuarios: `http://localhost:8081/swagger-ui.html`
- Swagger Plazoleta: `http://localhost:8082/swagger-ui.html`

Las bases de datos se publican en los puertos locales `3307` (usuarios) y `3308` (plazoleta). Hibernate crea/actualiza las tablas al arrancar.

### Administrador inicial

En desarrollo se crea un administrador inicial usando estas variables del archivo `.env`:

- Correo: `INITIAL_ADMIN_EMAIL`
- Clave: `INITIAL_ADMIN_PASSWORD`

Con los valores que trae `.env.example`, el acceso inicial es `admin@plazoleta.local` / `Admin123!`. Cambia estas credenciales antes de exponer el backend en una red real. La clave almacenada en la base de datos se codifica con BCrypt.

## Flujo básico para probarlo

### 1. Iniciar sesión como administrador

```http
POST http://localhost:8081/api/v1/auth/login
Content-Type: application/json

{
  "correo": "admin@plazoleta.local",
  "clave": "Admin123!"
}
```

Copia el valor `token` de la respuesta. En las siguientes peticiones protegidas usa la cabecera `Authorization: Bearer <token>`.

### 2. Crear un propietario

```http
POST http://localhost:8081/api/v1/usuarios/propietarios
Authorization: Bearer <TOKEN_ADMIN>
Content-Type: application/json

{
  "nombre": "Ana",
  "apellido": "Pérez",
  "documentoIdentidad": "100200300",
  "celular": "+573001234567",
  "fechaNacimiento": "1998-04-10",
  "correo": "ana.propietaria@ejemplo.com",
  "clave": "ClaveAna123!"
}
```

### 3. Crear un restaurante

Primero inicia sesión con la cuenta del propietario para obtener su token; la cuenta de propietario la crea el paso anterior.

```http
POST http://localhost:8082/api/v1/restaurantes
Authorization: Bearer <TOKEN_ADMIN>
Content-Type: application/json

{
  "nombre": "Restaurante Caribe",
  "nit": "900123456",
  "direccion": "Calle 10 # 20-30",
  "telefono": "+573001234567",
  "urlLogo": "https://ejemplo.com/logo.png",
  "propietarioId": 2
}
```

Sustituye `propietarioId` por el `id` real devuelto al crear el propietario. El servicio Plazoleta consulta Usuarios por una API interna para comprobar el rol. Para obtener el token del propietario, inicia sesión con su correo y clave:

```http
POST http://localhost:8081/api/v1/auth/login
Content-Type: application/json

{
  "correo": "ana.propietaria@ejemplo.com",
  "clave": "ClaveAna123!"
}
```

### 4. Crear cliente y consultar restaurantes

```http
POST http://localhost:8081/api/v1/usuarios/clientes
Content-Type: application/json

{
  "nombre": "Carlos",
  "apellido": "Díaz",
  "documentoIdentidad": "100500700",
  "celular": "3011234567",
  "correo": "carlos.cliente@ejemplo.com",
  "clave": "ClaveCarlos123!"
}
```

Inicia sesión como cliente y utiliza el JWT:

```http
GET http://localhost:8082/api/v1/restaurantes?page=0&size=10
Authorization: Bearer <TOKEN_CLIENTE>
```

La respuesta contiene `nombre` y `urlLogo` para cada restaurante, junto con `page`, `size`, `totalElements` y `totalPages`. El tamaño debe estar entre 1 y 100.

### 5. Crear/actualizar platos

Inicia sesión como propietario. Usa su token en estas peticiones:

```http
POST http://localhost:8082/api/v1/restaurantes/1/platos
Authorization: Bearer <TOKEN_PROPIETARIO>
Content-Type: application/json

{
  "nombre": "Arroz con coco",
  "precio": 18000,
  "descripcion": "Arroz con coco y pescado",
  "urlImagen": "https://ejemplo.com/arroz.png",
  "categoria": "Platos fuertes"
}
```

Actualizar solo precio o descripción:

```http
PATCH http://localhost:8082/api/v1/restaurantes/1/platos/1
Authorization: Bearer <TOKEN_PROPIETARIO>
Content-Type: application/json

{
  "precio": 20000,
  "descripcion": "Arroz con coco, pescado y ensalada"
}
```

Activar o desactivar:

```http
PATCH http://localhost:8082/api/v1/restaurantes/1/platos/1/estado
Authorization: Bearer <TOKEN_PROPIETARIO>
Content-Type: application/json

{
  "activo": false
}
```

Los ID de ejemplo (`1`, `2`) deben reemplazarse por los que entregue la API.

## Pruebas

Hay pruebas unitarias de reglas centrales de las HU1 a HU9 (edad y rol de usuario, login, propiedad del restaurante, actualización restringida de platos, estado, empleado y paginación). Para ejecutarlas localmente:

```bash
mvn test
```

También puedes compilar ambos microservicios con:

```bash
mvn package
```

## Notas de implementación

- Los roles permitidos son `ADMINISTRADOR`, `PROPIETARIO`, `EMPLEADO` y `CLIENTE`.
- La creación de clientes es pública porque la persona todavía no tiene cuenta; el resto de operaciones queda sujeto a JWT y rol.
- Las llamadas entre microservicios usan `X-Internal-Token`. Configura un valor largo y distinto para cada entorno antes de desplegar.
- Los campos de contraseñas nunca se devuelven en respuestas REST.
- Para mantenerlo sencillo, las tablas se gestionan con `spring.jpa.hibernate.ddl-auto=update`. Para despliegues formales sería mejor añadir migraciones controladas.
