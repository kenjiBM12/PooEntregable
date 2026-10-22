# Comparación con el hotel

Esta versión se corrigió usando los ZIP del cine y del hotel que enviaste,
y tu última indicación: **horario y fechaVenta deben ser LocalDate**.

## Qué conserva del hotel

- Los paquetes `modelo`, `interfaces`, `implementacion`, `utils`, `controller` y `test`.
- Modelos con propiedades JavaFX, constructores, getters, setters y métodos Property.
- Interfaces con insert, update, searchById, delete y listAll.
- DAO con boolean flag, PreparedStatement y objetos creados dentro de cada consulta.
- SQL y mapeo dentro de cada método; no hay métodos privados leerEntidad ni un SELECT compartido.
- Controlador de tabla separado de su FormController, formulario modal y bandera isSaved.
- Fechas mediante LocalDate, toString al escribir y parse al recuperar.

## Qué se corrigió en el ZIP recibido

1. PeliculaDaoImpl.searchById usaba un objeto null sin crear new Pelicula.
2. Había nombres inexistentes como clasisficacion e idpelic.
3. Algunas consultas mezclaban id, idpelicula e idsala sin coincidir con la base.
4. PeliculaDaoImpl.delete devolvía false aunque hubiera ejecutado la eliminación.
5. Funcion.horario todavía era String; ahora todos sus usos son LocalDate.
6. ReporteController y algunos tests llamaban a horaVenta, que no existía en el modelo recibido.
7. Se sustituyó el campo de texto de horario por un DatePicker y se agregó otro para fechaVenta.
8. El reporte se adaptó a fechas sin hora y muestra la fecha de la función.

## Base de datos y nombres

El archivo Cine.db recibido tiene la clave primaria id en las cuatro tablas.
No se cambió el archivo original. El SQL usa esos nombres físicos y los
adapta a los modelos con alias: por ejemplo id AS idpelicula.
El modelo sigue usando getIdpelicula, getIdsala, getIdfuncion y getIdventa.

Los datos antiguos con fecha y hora permanecen intactos. Al leerlos, substr
obtiene sus diez primeros caracteres para construir LocalDate. Las nuevas
operaciones escriben solo yyyy-MM-dd. En esta versión no se hace conversión
de zona horaria: se trabaja con la fecha guardada, como pediste.

## Diferencias necesarias del dominio

El hotel usa Cliente, Habitacion y Reserva; el cine usa Pelicula, Sala,
Funcion y Venta. No se copian campos ajenos al cine ni operaciones vacías
del ejemplo. El reporte agrega listByDate a IVentaDao dentro de las mismas capas.

Se conservan comprobaciones para impedir borrar registros relacionados.
Como la base no declara claves foráneas, los DAO comprueban las asociaciones
al escribir funciones y ventas. Estas comprobaciones no protegen SQL que
se ejecute directamente con otra herramienta fuera del proyecto.

## Qué se probó

- Compilación de los 32 archivos Java.
- Insertar, listar, buscar, actualizar y eliminar en las cuatro entidades.
- Las cinco operaciones desde cada menú de consola y búsqueda tras eliminar.
- Rechazo de borrados que dejarían registros relacionados sin padre.
- Tipo LocalDate de ambos campos y escritura yyyy-MM-dd sin hora.
- Lectura de valores antiguos que todavía contienen hora.
- Reporte por fecha, día vacío, totales y entradas.
- Carga de los once FXML utilizados y controles DatePicker.
- Guardar en los formularios prepara los objetos sin escribir en la base antes del controlador de tabla.

La verificación con escritura utilizó una copia de cine.db; no se alteraron
tus registros ni el proyecto original. No se agregó control de asientos
disponibles ni detección de cruces de fechas.
