# Consideraciones Técnicas

Este documento recoge y explica las decisiones de diseño y consideraciones técnicas del proyecto.

---

## 1. Dominio (Domain)

### Internacionalización y validaciones de identidad

Actualmente, el dominio implementa un **Value Object `Dni`** y la función `esMayorDeEdad()` en `Cliente`.  

- La validación del DNI se limita a un formato simple (`8 números + letra`).
- La mayoría de edad se calcula usando 18 años como referencia.

**Consideraciones reales**:  

- En un sistema internacional sería necesario soportar **NIE, pasaportes u otros identificadores** según país.  
- La regla de mayoría de edad puede variar entre jurisdicciones (16, 18, 21 años).  
- La implementación actual se deja simple para ajustarse al ejemplo, pero se podría extender añadiendo:  
  - Estrategia de validación por tipo de documento (`Dni`, `NIE`, `Pasaporte`)  
  - Configuración de mayoría de edad por país en `Cliente` o mediante un servicio de reglas de negocio.

---

### Uso de Value Objects

Usar los tipos propios del lenguaje y literales de texto son dos code smells a evitar ("Primitive obsession" y "Magic Strings"), además de no permitir lógica interna como es la validación y normalización.
Para solucionarlo hay que crear Value Objects para los elementos que así lo requieran:

- `Dni`  
- `TipoCuenta`  

**Implicación**:

- Los Value Objects obligan a tener **mappers en la infraestructura**, para transformar entre entidades del dominio y entidades de persistencia, o entre JSON y dominio en los controladores REST.

---

### Representación del saldo de la cuenta (`total`)

Actualmente `CuentaBancaria.total` se implementa como `double`. En sistemas reales **lo recomendable es `BigDecimal`**, para evitar errores de precisión en operaciones financieras. Se mantiene `double` para simplificar la implementación y ajustarse a los requerimientos de la prueba técnica.

---

### Tests del dominio

- Los tests de dominio (`ClienteTest`, `CuentaBancariaTest`, VO tests) se realizan **aislados**, usando **JUnit 5** puro.  
- No requieren infraestructura ni acceso a bases de datos.  
- Esto permite validar la **lógica de negocio y reglas fundamentales** de forma rápida y confiable.

---

## 2. Infraestructura (Infrastructure)

### Problema N+1 y cómo lo solucionamos

Al cargar clientes junto con sus cuentas, si usamos **fetching perezoso (`LAZY`)** y accedemos a las cuentas en un bucle, se produce el conocido **problema N+1**:  

1. Se ejecuta 1 query para cargar todos los clientes.  
2. Por cada cliente, se ejecuta 1 query adicional para cargar sus cuentas.  
3. Esto genera **N+1 queries**, con un coste creciente según el número de clientes.

**Solución aplicada**:

- Uso de **`@EntityGraph(attributePaths = "cuentas")`** en los métodos del repositorio (`findByDni`, `findAll`, etc.).  
- Esto permite **cargar clientes y cuentas en una única consulta JOIN**, eliminando el N+1.  

**Alternativas**:

- **JPQL JOIN FETCH**: especificar explícitamente la relación en la consulta.  
- **Batch fetching de Hibernate**: agrupar select por lotes para reducir queries.  

---

### Técnicas de escalado y optimización de consultas

Para preparar el sistema a un mayor volumen de datos, se pueden añadir posteriormente técnicas que permitan una mayor escalabilidad:

- **Paginación**: usando `Pageable` en los repositorios para no cargar todos los clientes a la vez.
- **DTO projections**: devolver únicamente los campos necesarios, reduciendo transferencia de datos y memoria.
- **Query por criterios**: limitar las búsquedas y agregaciones según filtros específicos (`fechaNacimientoBefore`, `total > X`).

Estas técnicas ayudan a **evitar sobrecarga de memoria y consultas costosas** al trabajar con bases de datos grandes.

---

### Inicialización de datos con `data.sql`

Se decidió usar **`data.sql`** para poblar la base de datos inicial en lugar de hacerlo vía JPA:

- Permite **control total del contenido inicial**, incluyendo clientes y cuentas de prueba.  
- Se ejecuta automáticamente al iniciar la aplicación con **Spring Boot y H2/PostgreSQL**.  
- Facilita la **repetibilidad de tests y demos**, asegurando que todos los desarrolladores trabajen con los mismos datos iniciales.  

**Alternativa**: poblar datos con `CommandLineRunner` o `ApplicationRunner` usando repositorios JPA. Esto es más dinámico pero menos predecible para pruebas reproducibles y depende de la lógica de negocio para crear entidades.