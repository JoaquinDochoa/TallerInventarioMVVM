# Inventario Taller

**Aplicación móvil de gestión de inventario de productos automotrices**

**Asignatura:** DSY1105 – Desarrollo de Aplicaciones Móviles  
**Institución:** Duoc UC  
**Lenguaje:** Kotlin  
**Entorno de desarrollo:** Android Studio  
**Arquitectura:** MVVM (Model-View-ViewModel)  
**Estado:** Primera entrega académica

---

## 1. Descripción del proyecto

Inventario Taller es una aplicación móvil desarrollada en Kotlin para dispositivos Android, orientada a mejorar la administración de productos, repuestos e insumos de un taller mecánico automotriz.

El proyecto surge de la necesidad de contar con una herramienta que permita registrar productos, consultar existencias, identificar artículos con stock bajo y mantener información actualizada sobre el inventario.

La aplicación utiliza arquitectura MVVM, Jetpack Compose para la interfaz gráfica y Room para el almacenamiento local de la información.

El desarrollo corresponde a un proyecto académico basado en un caso de estudio de la asignatura Desarrollo de Aplicaciones Móviles de Duoc UC.

## 2. Objetivo general

Desarrollar una aplicación móvil Android que permita gestionar el inventario de productos automotrices mediante una interfaz intuitiva, almacenamiento local, validaciones de datos y utilización de recursos nativos del dispositivo.

## 3. Tecnologías utilizadas

| Tecnología | Función |
|---|---|
| Kotlin | Lenguaje de programación |
| Android Studio | Entorno de desarrollo |
| Jetpack Compose | Desarrollo de interfaces gráficas |
| Material Design 3 | Componentes visuales |
| MVVM | Separación de responsabilidades |
| ViewModel | Administración de estados y lógica de presentación |
| StateFlow | Actualización reactiva de la interfaz |
| Navigation Compose | Navegación entre pantallas |
| Room | Persistencia local de datos |
| DAO | Consultas y operaciones de base de datos |
| Repository | Comunicación entre la lógica y los datos |
| Google Code Scanner | Lectura de códigos QR |
| NotificationManager | Generación de notificaciones locales |
| Git y GitHub | Control de versiones |

## 4. Funcionalidades implementadas

### Inicio de sesión

- Acceso mediante una cuenta ficticia.
- Validación de usuario y PIN.
- Navegación hacia el menú principal.
- Cierre de sesión.
- Pantalla personalizada con el logo de Inventario Taller y Duoc UC.

### Gestión de productos

- Registro de productos automotrices.
- Visualización del listado de productos.
- Búsqueda por nombre o código.
- Edición de productos registrados.
- Eliminación de productos con confirmación.
- Registro de categoría, marca y ubicación.
- Control de stock disponible y stock mínimo.
- Identificación visual de productos agotados o bajo el mínimo.

### Validaciones de formularios

- Verificación de campos obligatorios.
- Validación de cantidades numéricas.
- Prevención de existencias negativas.
- Comprobación de códigos duplicados.
- Mensajes de error individuales por campo.
- Gestión de estado mediante UiState y StateFlow.

### Escáner de códigos QR

La aplicación integra Google Code Scanner para identificar productos utilizando códigos QR ficticios.

El módulo permite:

- Escanear códigos QR mediante la cámara.
- Buscar productos registrados utilizando su código interno.
- Mostrar nombre, código, categoría, marca, existencias y ubicación.
- Informar cuando un producto no está registrado.
- Detectar códigos con formatos no válidos.
- Acceder a la edición del producto encontrado.

### Movimientos de inventario

- Registro de ingresos de productos.
- Registro de ventas.
- Registro de consumos asociados a servicios mecánicos.
- Actualización automática de existencias.
- Validación de stock disponible.
- Prevención de salidas superiores a las existencias.
- Registro del stock anterior y resultante.
- Historial de movimientos con fecha y hora.

### Notificaciones locales

- Alertas cuando un producto alcanza o queda bajo su existencia mínima.
- Avisos de productos agotados.
- Integración con el sistema de notificaciones de Android.
- Solicitud de permisos de notificación cuando corresponde.

### Animaciones

- Transiciones suaves en los mensajes de confirmación.
- Animaciones de alertas de existencias bajas.
- Cambios visuales de color en las tarjetas de productos.
- Ajustes animados del contenido de la interfaz.

## 5. Arquitectura MVVM

El proyecto utiliza el patrón Model-View-ViewModel para separar las responsabilidades de la aplicación.

**Model:** representa la información de productos y movimientos, así como el acceso a la base de datos mediante Room, DAO y Repository.

**View:** contiene las interfaces desarrolladas con Jetpack Compose y presenta la información al usuario.

**ViewModel:** administra los estados de la interfaz, coordina las validaciones y se comunica con los repositorios.

Flujo general:

```text
View (Jetpack Compose)
        |
        v
ViewModel (StateFlow)
        |
        v
Repository
        |
        v
DAO (Room)
        |
        v
Base de datos SQLite
```

Esta organización facilita la mantenibilidad del código y permite separar la presentación visual del acceso a los datos.

## 6. Estructura del proyecto

```text
cl.duoc.tallerinventario/
|
|-- MainActivity.kt
|
|-- data/
|   |-- local/
|   |   |-- AppDatabase.kt
|   |   |-- Producto.kt
|   |   |-- ProductoDao.kt
|   |   |-- Movimiento.kt
|   |   `-- MovimientoDao.kt
|   |
|   `-- repository/
|       |-- ProductoRepository.kt
|       `-- MovimientoRepository.kt
|
|-- ui/
|   |-- LoginScreen.kt
|   |-- HomeScreen.kt
|   |-- ProductosScreen.kt
|   |-- ProductoFormScreen.kt
|   |-- ProductoUiState.kt
|   |-- ProductoViewModel.kt
|   |-- MovimientoScreen.kt
|   `-- MovimientoViewModel.kt
|
`-- notifications/
    `-- NotificacionHelper.kt
```

## 7. Persistencia local

La aplicación utiliza Room para almacenar productos y movimientos de inventario.

Los datos se conservan localmente, permitiendo recuperar la información después de cerrar y volver a abrir la aplicación.

Esta característica permite consultar el inventario sin depender de una conexión permanente a internet.

## 8. Requisitos para ejecutar

- Android Studio compatible con el proyecto.
- JDK 17.
- Android SDK configurado.
- Emulador o dispositivo Android con API 26 o superior.
- Google Play Services para utilizar Google Code Scanner.
- Acceso a internet durante la configuración inicial y descarga de dependencias.

## 9. Instrucciones de ejecución

1. Clonar el repositorio desde GitHub.
2. Abrir Android Studio.
3. Seleccionar **File → Open**.
4. Elegir la carpeta del proyecto.
5. Esperar la sincronización de Gradle.
6. Seleccionar un emulador o dispositivo Android.
7. Ejecutar la configuración `app`.
8. Ingresar con las credenciales ficticias de demostración.

### Credenciales de prueba

**Usuario:** `admin`  
**PIN:** `1234`

Estas credenciales se utilizan exclusivamente para fines académicos. El inicio de sesión es simulado y no corresponde a un sistema de autenticación de producción.

## 10. Pruebas funcionales

Durante el desarrollo se realizaron pruebas de las siguientes funcionalidades:

- Registro de productos con datos válidos.
- Validación de formularios incompletos.
- Prevención de códigos duplicados.
- Edición de productos existentes.
- Registro de ingresos, ventas y consumos.
- Rechazo de ventas superiores al stock disponible.
- Lectura de códigos QR registrados.
- Detección de códigos QR inválidos o desconocidos.
- Generación de notificaciones de stock bajo.
- Visualización de mensajes y alertas animadas.

## 11. Alcance académico

Inventario Taller es un prototipo funcional desarrollado con fines educativos.

No contempla facturación electrónica, pagos reales ni integración con sistemas comerciales productivos.

Para etapas posteriores del proyecto se considera ampliar el sistema incorporando ajustes, devoluciones, reposición de productos, gestión de proveedores y preparación de solicitudes por correo electrónico.

## 12. Gestión del proyecto

El código fuente se administra mediante Git y GitHub, permitiendo registrar los cambios realizados durante el desarrollo.

La organización y seguimiento de actividades se documentará mediante Trello, incluyendo tareas implementadas, pruebas y elaboración del informe técnico.

El proyecto se ha continuado desarrollando individualmente debido a la desvinculación del otro integrante del equipo.

## 13. Autoría

**Proyecto:** Inventario Taller  
**Asignatura:** Desarrollo de Aplicaciones Móviles – DSY1105  
**Institución:** Duoc UC  
**Modalidad de desarrollo:** Individual, tras la desvinculación del compañero de equipo.

## 14. Conclusión

El desarrollo de Inventario Taller permitió aplicar conocimientos de programación móvil con Kotlin, arquitectura MVVM, interfaces con Jetpack Compose, gestión de estado mediante StateFlow y persistencia local utilizando Room.

La aplicación permite administrar productos automotrices, controlar existencias, registrar movimientos y utilizar recursos nativos como la lectura de códigos QR y las notificaciones locales.

Las funcionalidades implementadas constituyen una base para continuar ampliando la aplicación en futuras etapas del proyecto académico.
