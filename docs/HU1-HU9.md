# Matriz de alcance HU1–HU9

| HU | Validaciones de negocio principales | Prueba/resultado observable |
|---|---|---|
| 1 | Solo administrador; correo/documento únicos; celular con máximo 13 caracteres; documento numérico; fecha de nacimiento válida y 18+ años; clave BCrypt | Crea usuario con rol `PROPIETARIO`; rechaza menor de edad |
| 2 | Solo administrador; NIT numérico y único; nombre no formado solo por números; propietario existente con rol `PROPIETARIO` | Crea restaurante y conserva `propietarioId` |
| 3 | Solo propietario del restaurante; campos requeridos; precio entero mayor que cero | Crea plato con `activo=true` |
| 4 | Solo propietario del restaurante; el cuerpo permite cambiar únicamente precio y descripción | No modifica nombre, categoría, URL, restaurante ni activo |
| 5 | Login por correo y clave; BCrypt; JWT compartido por ambos servicios; autorización por rol | JWT permite acceder solo a las operaciones del rol correspondiente |
| 6 | Solo propietario del restaurante indicado; documento/correo únicos; clave BCrypt | Crea usuario con rol `EMPLEADO` y `restauranteId` asociado |
| 7 | Solo propietario del restaurante indicado; verifica que el plato pertenezca al restaurante | Cambia `activo` sin permitir tocar platos ajenos |
| 8 | Alta pública; datos obligatorios; documento/correo únicos; rol fijo `CLIENTE`; clave BCrypt | Crea cuenta de cliente |
| 9 | Solo cliente; página no negativa; tamaño 1–100; orden ascendente por nombre | Devuelve `nombre` y `urlLogo` por restaurante y metadatos de paginación |

## Flujo entre microservicios

- Plazoleta consulta `GET /internal/usuarios/{id}` en Usuarios al crear un restaurante para verificar el rol del propietario.
- Plazoleta envía `POST /internal/usuarios/empleados` al crear un empleado. Incluye el `restauranteId` y la llamada se protege con `X-Internal-Token`.
- Los endpoints internos requieren ese token aunque sus rutas estén excluidas del filtro JWT.
