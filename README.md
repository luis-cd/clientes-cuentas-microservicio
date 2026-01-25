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

## Arquitectura

El proyecto sigue una **arquitectura hexagonal**, separando claramente responsabilidades:

- **Dominio**  
  Entidades y lógica de negocio pura (sin dependencias de frameworks e implementaciones)

- **Aplicación**  
  Casos de uso que orquestan el dominio y los puertos

- **Infraestructura**  
  Adaptadores REST, persistencia con Spring Data JPA, configuración técnica

---

