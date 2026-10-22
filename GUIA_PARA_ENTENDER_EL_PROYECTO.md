# Guía del proyecto con LocalDate

## Orden para estudiar

Lee primero Pelicula, IPeliculaDao, ConexionDB, PeliculaDaoImpl y TestPelicula.
Después estudia PeliculaController, pelicula-view.fxml, PeliculaFormController
y pelicula-form.fxml. Repite el recorrido con Sala, Funcion y Venta.

## Capas

| Parte | Qué hace |
|---|---|
| modelo | Guarda datos en objetos Java. |
| interfaces | Declara qué operaciones ofrece el DAO. |
| implementacion | Ejecuta SQL y convierte filas en modelos. |
| utils | Reutiliza conexión y alertas. |
| controller | Atiende controles y acciones de las vistas. |
| test | Prueba las operaciones desde consola. |
| resources/views | Define las ventanas mediante FXML y sus estilos CSS. |

## Los dos campos de fecha

Funcion.horario y Venta.fechaVenta son ambos LocalDate.
Por eso pueden contener 2026-10-22, pero no una hora como 18:30.

```java
funcion.setHorario(LocalDate.parse("2026-10-22"));
venta.setFechaVenta(LocalDate.now());
```

La propiedad JavaFX de cada uno es ObjectProperty<LocalDate>.
El getter obtiene la fecha; el setter la asigna; el método Property permite
mostrarla en una columna de la tabla. Es el patrón de Reserva del hotel.

```java
// Escribir en SQLite:
st.setString(3, funcion.getHorario().toString());

// Recuperar desde el resultado SQL:
funcion.setHorario(LocalDate.parse(rs.getString("fecha_funcion")));
```

toString convierte la fecha a texto yyyy-MM-dd. parse realiza la conversión
inversa. trim solo elimina espacios al principio y al final de un texto.

En los formularios, DatePicker.getValue devuelve LocalDate directamente:

```java
registro.setHorario(dpHorario.getValue());
registro.setFechaVenta(dpFechaVenta.getValue());
```

## Del formulario a la base, como en el hotel

```text
Nuevo → showForm(null) → formulario → validate → handleSave
      → saved=true → cerrar → controlador de tabla
      → dao.insert(objeto) → cargarDatos
```

Editar sigue el mismo recorrido con un objeto que ya tiene ID y termina
en update. Cancelar no marca saved y no ejecuta insert/update.
El formulario prepara el objeto; no guarda por su cuenta en SQLite.

## Propiedades y tablas

StringProperty representa texto; IntegerProperty un entero; DoubleProperty
un decimal; ObjectProperty guarda un objeto, incluida una fecha LocalDate.
final impide reemplazar la referencia de la propiedad, pero permite cambiar
su valor con set. Los getters leen, los setters asignan y Property devuelve
la propiedad observable.

FXMLLoader crea los controles del FXML e inyecta los campos @FXML. El fx:id
del FXML debe coincidir con el nombre del campo Java. Después se ejecuta
initialize, que configura las columnas y carga datos.

```java
colHorario.setCellValueFactory(
        cellData -> cellData.getValue().horarioProperty());
```

La lambda recibe información de una fila y devuelve la propiedad a mostrar.
ObservableList permite a la tabla observar su colección. asObject adapta
las propiedades numéricas a Integer o Double cuando la columna lo necesita.

## DAO y JDBC

Connection abre la comunicación con SQLite. PreparedStatement prepara SQL
con parámetros ?, que se completan desde la posición 1. ResultSet contiene
las filas devueltas; next avanza a la siguiente fila.

executeUpdate ejecuta INSERT, UPDATE y DELETE y devuelve filas afectadas.
executeQuery ejecuta SELECT y devuelve ResultSet.
El try con recursos cierra la conexión y las sentencias al finalizar.

Los modelos usan nombres como idpelicula; la base enviada usa id. El DAO
hace la correspondencia con alias: SELECT id AS idpelicula.
No se renombraron columnas ni se ejecutaron migraciones automáticamente.

En cada búsqueda y listado se crean los modelos con new y se asignan sus
campos mediante setters. En Funcion se crean también Pelicula y Sala; en
Venta se crea su Funcion. No se usa un método leerEntidad separado.

## Enteros y decimales

TextField.getText devuelve String. Para duración y capacidad se usa
Integer.parseInt; para el precio se usa Double.parseDouble.
Los campos numéricos deben validarse después de convertirlos.
DatePicker ya devuelve LocalDate, por lo que no necesita parseInt ni parseDouble.

## Reporte

ReporteController pide al DAO listByDate(fecha), llena la tabla y suma el
total de ventas, las entradas y la cantidad de operaciones. La columna
final muestra la fecha de la función. Las ventas de una fecha se seleccionan
por fechaVenta; no por la fecha de la función.

Los registros antiguos con fecha y hora se leen tomando su fecha. No se
modifican ni se convierten de zona horaria. Las nuevas escrituras guardan
solo la fecha. Un día sin ventas debe mostrar una lista vacía y totales cero.

## Tests

Cada test de entidad tiene un menú con insertar, listar, buscar, actualizar
y eliminar. Escribe las fechas como yyyy-MM-dd. Los menús trabajan sobre tu
base real. Para registrar, crea primero películas y salas, después funciones
y finalmente ventas. Para eliminar registros relacionados sigue el orden inverso.

TestConexion solo verifica que puede abrir la base; no confirma por sí solo
que sus tablas existan. TestReporte muestra las ventas de hoy y acepta otra
fecha como argumento. Revisa los errores de consola antes de interpretar
una lista vacía como ausencia de datos.
