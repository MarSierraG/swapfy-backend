# Swapfy Backend

**Swapfy Backend** es la API REST desarrollada en Java con Spring Boot que da soporte a la plataforma de trueque digital [Swapfy](https://github.com/MarSierraG/swapfy-frontend).


## Tecnologías

- Java 17+
- Spring Boot
- Spring Security + JWT
- PostgreSQL
- Maven


## Funcionalidades

- Registro y login de usuarios
- Roles (`USER`, `ADMIN`) con control de permisos
- Gestión de artículos, etiquetas, valoraciones y mensajes
- Intercambio de artículos entre usuarios
- Configuración CORS para conexión con Angular
- Validaciones personalizadas y manejo de errores


## Estructura de carpetas

src/ ├── controllers/ ├── services/ ├── repositories/ ├── models/ ├── dto/ └── config/


## Pruebas

El backend ha sido testeado con una colección Postman automatizada que valida todas las rutas y funcionalidades con diferentes roles.


## Frontend

Puedes ver el cliente Angular aquí:  
-> [swapfy-frontend](https://github.com/MarSierraG/swapfy-frontend)
