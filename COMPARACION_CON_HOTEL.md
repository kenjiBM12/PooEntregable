# Comparación del cine con el hotel

Se compararon `mi proyecto cine.zip` y `hotel.zip` recibidos el 21/10/2026.
Se conservan los paquetes y las capas del hotel; no se agregaron servicios,
repositorios genéricos ni frameworks adicionales.

| Parte | Resultado |
|---|---|
| Modelos | Propiedades JavaFX, constructores, getters, setters y métodos `Property()`, como en el hotel. |
| Relaciones | `Funcion` contiene `Pelicula` y `Sala`; `Venta` contiene `Funcion`. Se sigue el patrón de asociaciones de `Reserva`. |
| Interfaces | Se conservan `insert`, `update`, `searchById`, `delete`, `listAll`. Solo ventas agrega `listByDate(LocalDate)` para el reporte. |
| Implementaciones | JDBC, `PreparedStatement`, `ResultSet` y `try` con recursos. Se conservan los métodos privados de lectura para evitar repetir el mapeo. |
| Controladores | Tabla, `initialize`, `cargarDatos`, Nuevo, Editar, Eliminar y formulario modal, como en `ClienteController`. |
| Formularios | `setEntidad`, `getEntidad`, `isSaved`, `validate`, `handleSave`, `handleCancel`. Preparan el objeto; el controlador de tabla llama a insert/update. |
| Conexión | Se conserva el método `getConexion()` y la ruta `C:/poo/sqilte1U2/db/cine.db`. Cambiarlo a getConnection solo por el nombre rompería las llamadas actuales. |
| Pruebas | Se conservan las clases con `main` y los menús que permiten probar todas las operaciones. El hotel usa métodos desde main; el menú es la adaptación que pediste para probarlos. |
| Reporte | Es una función nueva del cine. Usa FXML, controlador y DAO dentro de los mismos paquetes existentes. |

## Errores encontrados y corregidos

1. `VentaDaoImpl.leerVenta()` leía `fecha_salida`. Esa columna pertenece al hotel y no existe en venta. Ahora lee `fecha_venta`.
2. `fecha_venta` contiene fecha y hora de SQLite; intentar leerla directamente con `LocalDate.parse()` falla. Ahora se interpreta el timestamp completo y se asignan fecha y hora por separado.
3. `VentaController` esperaba `colTotalPagar`, pero `venta-view.fxml` inyecta `colTotal`. Se unificó el nombre.
4. `TestPelicula` rechazaba los valores positivos mediante `duracion >= 0`. Se corrigió a `duracion <= 0`.
5. `Sala.getCapacidad()` devolvía `Integer` pese a representar un valor entero obligatorio. Ahora devuelve `int`.
6. El reporte tenía cifras fijas (450.00 y 30) y ninguna consulta. Ahora utiliza ventas reales y calcula el total, las entradas y la cantidad de ventas.
7. La base actual no declara claves foráneas. Los DAO ahora bloquean eliminar películas/salas con funciones y funciones con ventas. También comprueban que existan las relaciones al escribir funciones y ventas.

## Fechas del reporte

SQLite `CURRENT_TIMESTAMP` guarda UTC. La interfaz muestra la hora de Perú
(`America/Lima`). Los límites del día elegido se convierten a UTC antes de
consultar, para no mezclar ventas de dos días alrededor de medianoche.

Se mantiene `ObjectProperty<LocalDate> fechaVenta`, siguiendo el hotel.
Se agregó `ObjectProperty<LocalTime> horaVenta` para mostrar la hora sin
perderla. No se cambiaron columnas ni datos de la base original.

## Clientes

No es necesario crear una tabla Cliente para este reporte: cada venta ya
guarda el nombre del cliente. La tabla muestra ese nombre junto a la película,
las entradas, el total y la hora.

Solo haría falta un módulo Cliente separado si el alcance requiere DNI,
correo, edición de una ficha de cliente o historial por identificador.
No se copió Persona/Cliente del hotel artificialmente, porque no forma parte
del modelo actual del cine.

## Diferencias intencionales

El cine tiene películas, salas, funciones y ventas; el hotel tiene clientes,
habitaciones y reservas. El patrón se mantiene, pero no se copian campos
ajenos al cine ni métodos vacíos del hotel. Por ejemplo, el ZIP del hotel
todavía tiene operaciones de Reserva pendientes; los DAO del cine conservan
sus cinco operaciones implementadas.

## Verificación

- Maven con JDK 21: compilación correcta de los 32 archivos Java.
- CRUD de las cuatro entidades: inserción, listado, búsqueda, actualización y eliminación correctos.
- Los cuatro menús de consola se recorrieron con sus cinco operaciones, incluyendo búsqueda posterior a la eliminación.
- Borrados protegidos y relaciones inexistentes comprobados.
- Reporte: inicio y fin del día, conversión UTC/Perú, día vacío, sumas y preservación de fecha al editar comprobados.
- Los once FXML cargan y el reporte muestra las filas y totales esperados.
- Se comprobó que guardar en el formulario prepara el objeto sin escribir en la base antes de volver al controlador de tabla.
- Las pruebas con escritura utilizaron una copia aislada de cine.db.

## Alcance pendiente

No se agregó control de asientos disponibles ni detección de cruces de
horarios. La base existente sigue sin claves foráneas declaradas: las
comprobaciones agregadas protegen las operaciones del proyecto, no los
cambios que alguien haga directamente con SQL fuera de él.
