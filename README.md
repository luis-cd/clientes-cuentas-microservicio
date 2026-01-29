# Bank Account Service

Microservicio REST desarrollado con **Spring Boot 3** que permite gestionar clientes bancarios y sus cuentas asociadas.  
El proyecto forma parte de una prueba técnica y está diseñado siguiendo principios de **arquitectura hexagonal (Ports & Adapters)**, **API-First** y buenas prácticas de desarrollo backend.

---

## Descripción funcional

El servicio permite:

- Consultar clientes y sus cuentas bancarias
- Filtrar clientes por criterios de edad y saldo total
- Dar de alta nuevas cuentas bancarias
- Actualizar el saldo de una cuenta existente

Los datos se almacenan en una base de datos **H2 en memoria** y se exponen mediante una API REST documentada con **OpenAPI / Swagger**.

---

## Requisitos

Para ejecutar el proyecto en local es necesario disponer de:

- **Java 17** o superior  
  (Spring Boot 3 requiere Java 17 como mínimo)

- **Maven 3.8+**  
  (el proyecto incluye Maven Wrapper, por lo que no es obligatorio tener Maven instalado globalmente)

- Un sistema operativo compatible con JVM  
  (Linux, macOS, Windows)

No es necesario instalar ni configurar una base de datos externa, ya que el proyecto utiliza **H2 en memoria**.

---

## Ejecución del proyecto

### Ejecución en local

Desde la raíz del proyecto, ejecutar:

```bash
./mvnw spring-boot:run
```
O para sistemas Windows:

```
mvnw.cmd spring-boot:run
```
---

### Cómo ejecutar los tests

El proyecto incluye **tests unitarios y de integración** para cada capa. Se pueden ejecutar de varias formas:

```bash
./mvnw clean test
```
A futuro, se le puede añadir Postman para testear el funcionamiento de la API

---

## URLs importantes

| Servicio       | URL                                         |
|----------------|---------------------------------------------|
| **Swagger UI** | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) |
| **OpenAPI JSON** | [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs) |
| **H2 Console** | [http://localhost:8080/h2-console](http://localhost:8080/h2-console) |

> ⚠ Nota: para la H2 Console, la URL de conexión es `jdbc:h2:mem:testdb` que ya lo pone de forma predeterminada, usuario `user` y contraseña `pass`. Estos datos están expuestos en la configuración del proyecto en el `.properties`. No debería ser así en código real en producción. No he realizado configuración de .env para hacer el proyecto más directo y accesible

## Consideraciones técnicas

Para entender las decisiones de diseño, trade-offs y algunas discusiones sobre seguridad, escalabilidad y arquitectura, consulta el archivo:

[Consideraciones técnicas](ConsideracionesTecnicas.md)

[Grafo de directorios](Tree.md)

## Arquitectura y organización del proyecto

El proyecto sigue una **arquitectura hexagonal (Ports & Adapters)**, donde cada capa tiene responsabilidades claras y aisladas. A continuación se explica cómo se refleja en la estructura de carpetas:

### Dominio (`domain`)

Contiene la **lógica de negocio pura**, sin dependencias de frameworks externos. Aquí se definen:

- **Modelos / Entidades** (`model`):  
  - `Cliente`, `CuentaBancaria`  
  - Value Objects: `Dni`, `TipoCuenta`  
  - Excepciones del dominio: `CuentaNoEncontradaException`, `ClienteNoEncontradoException`  

- **Puertos de entrada (in)**: Interfaces que representan los casos de uso desde la perspectiva del dominio, por ejemplo:  
  - `CrearCuentaUseCase`, `ActualizarSaldoUseCase`  
  - `ObtenerClientesUseCase`, `ObtenerClientePorDniUseCase`, etc.

- **Puertos de salida (out)**: Interfaces que abstraen la persistencia u otros servicios externos, por ejemplo:  
  - `ClienteRepositoryPort`, `CuentaRepositoryPort`

**Ruta en el proyecto:**  
`src/main/java/com/example/banca/domain`

---

### Aplicación (`application`)

Orquesta la lógica del dominio y implementa los **casos de uso**, utilizando los puertos definidos en el dominio.  

- **Servicios de aplicación** (`services`):  
  - `CuentaService`: Implementa `CrearCuentaUseCase` y `ActualizarSaldoUseCase`  
  - `ClienteService`: Implementa los casos de uso de consulta de clientes (`ObtenerClientesUseCase`, etc.)  

**Responsabilidad:**  
- Coordinar operaciones entre entidades de dominio y puertos de salida  
- Validar reglas de negocio que involucran múltiples entidades  
- Preparar la información para ser devuelta a la infraestructura (controladores REST, mappers, etc.)

**Ruta en el proyecto:**  
`src/main/java/com/example/banca/application/services`

---

### Infraestructura (`infrastructure`)

Contiene **adaptadores concretos** que permiten interactuar con el mundo exterior: base de datos, REST API, mappers, etc.  

- **Persistencia (`persistence`)**:  
  - **Entidades JPA** (`jpaentities`): `ClienteEntity`, `CuentaBancariaEntity`  
  - **Repositorios Spring Data** (`repositories`): `ClienteRepository`, `CuentaBancariaRepository`  
  - **Adapters** que implementan los puertos de salida (`ClienteRepositoryAdapter`, `CuentaRepositoryAdapter`)  
  - **Mappers** (`mappers`): Transforman entre entidades JPA y objetos de dominio  

- **REST (`rest`)**:  
  - **Controladores** (`Controladores`): `ClienteController`, `CuentaController`  
  - **DTOs** (`DTOs`): `ClienteDTO`, `CuentaDTO`, `CrearCuentaRequest`, `ActualizarCuentaRequest`  
  - **Mappers** (`mappers`): Transforman entre DTOs y objetos de dominio  

**Responsabilidad:**  
- Exponer los casos de uso a través de APIs REST  
- Persistir y recuperar datos de la base de datos mediante JPA  
- Convertir entre modelos internos de dominio y representaciones externas (JSON, DTOs)  

**Ruta en el proyecto:**  
`src/main/java/com/example/banca/infrastructure`

---

### Tests

Se mantienen tests separados por tipo y capa:

- **Dominio:**  
  - Lógica de negocio pura (`ClienteTest`, `CuentaBancariaTest`, `DniTest`, `TipoCuentaTest`)  

- **Aplicación:**  
  - Servicios y casos de uso (`ClienteServiceTest`, `CuentaServiceTest`)  
  - Tests de integración con base de datos en memoria (`ClienteServiceIntegrationTest`, `CuentaServiceIntegrationTest`)  

- **Infraestructura REST:**  
  - Tests de integración de endpoints con `MockMvc` (`ClienteControllerIntegrationTest`)  

**Ruta en el proyecto:**  
`src/test/java/com/example/banca`
