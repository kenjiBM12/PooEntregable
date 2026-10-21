# Cómo entender tu proyecto de cine

Esta copia contiene el mismo programa corregido, con comentarios explicativos.
Los comentarios de Java (`//` y `/** ... */`), FXML (`<!-- ... -->`) y CSS
(`/* ... */`) son explicaciones: no se ejecutan ni cambian los resultados.

## 1. Qué representa cada paquete

| Paquete | Responsabilidad | Ejemplo |
|---|---|---|
| `modelo` | Representa los datos como objetos Java. | Una `Pelicula` con título y duración. |
| `interfaces` | Declara las operaciones que debe ofrecer un DAO. | `IPeliculaDao.insert(p)`. |
| `implementacion` | Ejecuta SQL y convierte filas en objetos. | `PeliculaDaoImpl`. |
| `utils` | Contiene utilidades compartidas. | Conexión y alertas. |
| `controller` | Atiende las vistas JavaFX y sus acciones. | Botones Nuevo, Editar, Guardar. |
| `test` | Permite probar el programa desde consola. | `TestPelicula`. |

Los FXML y las imágenes están en `src/main/resources`. No son paquetes de
clases Java: son recursos que `FXMLLoader` localiza al ejecutar.

`pom.xml` configura Maven y sus dependencias. `module-info.java` declara
los módulos que Java puede usar y los paquetes que JavaFX puede inspeccionar.

## 2. Por dónde leer el código

Estudia una entidad completa antes de saltar a otra:

1. `modelo/Pelicula.java`.
2. `interfaces/IPeliculaDao.java`.
3. `utils/ConexionDB.java`.
4. `implementacion/PeliculaDaoImpl.java`.
5. `test/TestPelicula.java`.
6. `controller/PeliculaController.java` y `views/pelicula-view.fxml`.
7. `controller/PeliculaFormController.java` y `views/pelicula-form.fxml`.

Después repite con Sala, Funcion y Venta. Finalmente estudia el reporte.

## 3. Los modelos: objetos en memoria

| Modelo | Datos y relaciones |
|---|---|
| `Pelicula` | ID, título, duración en minutos, género y clasificación. |
| `Sala` | ID, nombre, tipo y capacidad. |
| `Funcion` | ID, película, sala, horario y precio por entrada. |
| `Venta` | ID, función, nombre del cliente, cantidad, total, fecha y hora. |

Este código solo crea y modifica un objeto en memoria:

```java
Pelicula p = new Pelicula();
p.setTitulo("Avatar");
p.setDuracion(162);
```

Hasta que se llama a `dao.insert(p)`, esos datos no están guardados en SQLite.

### Propiedades JavaFX

```java
private final IntegerProperty duracion = new SimpleIntegerProperty();
```

- `private`: el atributo se usa directamente dentro de la clase.
- `final`: la referencia a esa propiedad no puede reemplazarse por otra.
  Su valor sí puede cambiar mediante `set()`.
- `IntegerProperty`: propiedad observable que guarda un entero.
- `new SimpleIntegerProperty()`: crea el objeto que almacena ese valor.

| Tipo | Valor que representa |
|---|---|
| `StringProperty` | Texto, por ejemplo un título. |
| `IntegerProperty` | Entero, por ejemplo capacidad o duración. |
| `DoubleProperty` | Decimal, por ejemplo un precio. |
| `ObjectProperty<Pelicula>` | Un objeto Pelicula relacionado. |
| `ObjectProperty<LocalDate>` | Fecha sin hora. |
| `ObjectProperty<LocalTime>` | Hora sin fecha. |

Por cada atributo hay tres métodos:

```java
p.getDuracion();         // Lee el int actual.
p.setDuracion(100);      // Cambia el valor.
p.duracionProperty();    // Devuelve la propiedad que una tabla puede observar.
```

Un constructor prepara un objeto al crearlo. El constructor vacío permite
asignar los datos después; el constructor con parámetros los recibe al inicio.
`this` se refiere al objeto actual. `void` indica que un método no devuelve
un resultado. `return` devuelve un resultado o termina el método.

Las asociaciones se navegan de objeto en objeto:

```java
venta.getFuncion().getPelicula().getTitulo();
```

Lee: de la venta obtengo su función; de esa función obtengo su película;
de esa película obtengo el título. Si una relación no fue asignada y es
`null`, no se puede continuar esa cadena.

## 4. Interfaces e implementaciones

Una interfaz declara qué operaciones existen, sin escribir sus consultas SQL:

```java
boolean insert(Pelicula p);
Pelicula searchById(int id);
List<Pelicula> listAll();
```

`implements IPeliculaDao` significa que `PeliculaDaoImpl` se compromete a
implementar ese contrato. `@Override` indica que el método implementa o
redefine uno declarado por una interfaz o una clase padre.

```java
IPeliculaDao dao = new PeliculaDaoImpl();
```

El tipo de la variable es la interfaz; el objeto concreto ejecuta el SQL.
DAO significa *Data Access Object*: objeto de acceso a datos.

| Método | Operación |
|---|---|
| `insert` | Registrar una fila nueva. |
| `update` | Modificar una fila que ya tiene ID. |
| `searchById` | Recuperar una fila por su identificador. |
| `delete` | Eliminar la fila indicada si sus relaciones lo permiten. |
| `listAll` | Recuperar una lista de objetos. |
| `listByDate` | Recuperar las ventas del día elegido para el reporte. |

`List<Pelicula>` significa lista cuyos elementos son Pelicula. `ArrayList`
es una implementación concreta de esa lista. `null` significa que no hay
un objeto en esa referencia; no es igual a una lista vacía.

En los DAO actuales, algunas búsquedas también devuelven null si hubo un
error SQL, y algunos listados devuelven una lista vacía después de imprimir
el error. Por eso revisa la consola antes de interpretar el resultado como
"no existe" o "no hay registros". La consulta del reporte comunica el error
al controlador para no confundirlo con un total real de cero.

## 5. Cómo se ejecuta SQL con JDBC

JDBC es la API de Java para comunicarse con bases de datos. SQLite es el
motor que guarda los datos en el archivo `C:/poo/sqilte1U2/db/cine.db`.

```java
String query = "INSERT INTO pelicula (...) VALUES (?, ?, ?, ?)";
```

Los `?` son lugares reservados para los valores. No llevan el nombre del
atributo; se completan según su posición, empezando en 1:

```java
st.setString(1, p.getTitulo());
st.setInt(3, p.getDuracion());
```

`Connection` es la conexión abierta. `PreparedStatement` prepara la sentencia
y recibe sus parámetros. `ResultSet` contiene las filas de un SELECT.

- `executeUpdate()` se usa para INSERT, UPDATE y DELETE y devuelve cuántas
  filas se afectaron. Compararlo con `1` confirma que se afectó una fila.
- `executeQuery()` se usa para SELECT y devuelve un ResultSet.
- `rs.next()` avanza de fila; devuelve false cuando no quedan más.
- `rs.getString("titulo")` lee una columna como texto.
- `rs.getInt("duracion")` lee una columna como entero.
- `leerPelicula(rs)` agrupa el trabajo de crear el objeto desde la fila actual.

Un JOIN reúne información relacionada: una venta con su función, película
y sala. Un alias como `v.id AS venta_id` da un nombre claro a una columna
cuando varias tablas tienen una columna llamada `id`.

El nombre del campo Java no tiene que ser idéntico al de la columna SQL:
el DAO hace la correspondencia. Por ejemplo, `idpelicula` en el modelo se
lee desde la columna `id` de la tabla pelicula.

### Cierre y errores

```java
try (Connection cn = ConexionDB.getConexion();
     PreparedStatement st = cn.prepareStatement(query)) {
    // Operación.
} catch (SQLException e) {
    // Se informa el error.
}
```

El `try` con recursos cierra los recursos declarados al terminar, incluso si
hay error. Como la conexión queda cerrada por el DAO, `getConexion()` comprueba
`isClosed()` y abre otra la próxima vez. Es el patrón utilizado en el hotel.

`catch` atiende una excepción. `throw` genera o propaga una excepción;
`throws SQLException` indica en la firma que un método puede propagarla.

`setAutoCommit(true)` confirma cada sentencia automáticamente. No significa
que todas las operaciones de toda la aplicación formen una sola transacción.

## 6. Vista y controlador de tabla

FXML describe los controles. El controlador atiende sus acciones:

```xml
<Button text="Nuevo" onAction="#handleNew"/>
<TableView fx:id="tabla"/>
```

`@FXML private TableView<Pelicula> tabla;` recibe el control cuyo fx:id es
`tabla`. El nombre debe coincidir. `fx:controller` señala la clase Java que
atiende ese FXML. `FXMLLoader` crea los controles, los inyecta y después
ejecuta `initialize()`.

```java
colTitulo.setCellValueFactory(
        cellData -> cellData.getValue().tituloProperty());
```

`cellData -> ...` es una expresión lambda: recibe la información de una fila,
obtiene su objeto Pelicula y devuelve la propiedad que debe mostrar esa columna.

`IntegerProperty.asObject()` y `DoubleProperty.asObject()` adaptan esas
propiedades para columnas que esperan `Integer` o `Double`.

`setCellValueFactory` decide qué dato mostrar. `setCellFactory` personaliza
cómo se dibuja una celda, por ejemplo la numeración o el formato de un total.

`ObservableList` es una colección que la tabla puede observar. `setItems(lista)`
le entrega a TableView sus filas. `getSelectionModel().getSelectedItem()`
recupera el objeto seleccionado o null si no hay selección.

## 7. El formulario separado, como en el hotel

El recorrido de Nuevo es:

```text
Botón Nuevo
  → handleNew()
  → showForm(null)
  → FXMLLoader carga pelicula-form.fxml
  → setPelicula(null)
  → el usuario completa los campos
  → handleSave() valida y prepara Pelicula
  → saved = true y cerrar()
  → el controlador de tabla consulta isSaved()
  → dao.insert(controller.getPelicula())
  → cargarDatos() actualiza la tabla
```

Para Editar, `setPelicula()` recibe el objeto encontrado por ID y llena los
campos. Al regresar, el controlador de tabla utiliza `update()`.

`Stage` representa la ventana y `Scene` su contenido. Una ventana modal
bloquea la interacción con otras ventanas de la aplicación mientras se usa.
`showAndWait()` espera a su cierre antes de continuar ese flujo de código.

`isSaved()` no significa que SQLite ya guardó: significa que el formulario
aceptó los datos. El controlador de tabla realiza la operación SQL después.

Si el usuario cancela, `saved` sigue false y no se llama a insert/update.
Para eliminar, `Optional<ButtonType>` permite comprobar si hubo una respuesta
y si se pulsó OK antes de ejecutar delete.

## 8. Texto, enteros y decimales

Un TextField siempre entrega String. Aunque se escriba `80`, hay que convertirlo:

```java
int capacidad = Integer.parseInt(txtCapacidad.getText().trim());
double precio = Double.parseDouble(txtPrecio.getText().trim());
```

`trim()` elimina espacios del principio y del final. `isEmpty()` comprueba
si no hay caracteres; `isBlank()` también acepta como vacío un texto hecho
solo de espacios. `parseInt()` no acepta `3.5`; para ese valor se utiliza
`parseDouble()`. Estos formularios esperan punto decimal, por ejemplo `15.50`.

Si el texto no se puede convertir, se genera `NumberFormatException`.
Después de convertir, hay que validar el valor:

```java
if (capacidad <= 0) {
    // El cero y los negativos no son capacidades válidas.
}
```

`Double.isFinite(precio)` rechaza valores especiales como infinito o NaN.
`||` significa O: la condición falla si cualquiera de sus partes es inválida.
`&&` significa Y: ambas partes deben ser verdaderas. `==` compara valores
primitivos; para el contenido de un String se usa `equals()`.

## 9. El reporte del día

No es una tabla nueva de la base: es una consulta de las ventas existentes.

```text
reporte-view.fxml
  → ReporteController.initialize()
  → fecha de hoy en America/Lima
  → ventaDao.listByDate(fecha)
  → consulta en VentaDaoImpl
  → List<Venta>
  → tabla y resúmenes
```

SQLite genera `CURRENT_TIMESTAMP` en UTC. El reporte convierte el inicio
del día peruano y el inicio del siguiente día a UTC antes de consultar.
Por ejemplo, el 19/10/2026 en Perú va desde 19/10 a las 05:00 UTC hasta
20/10 a las 05:00 UTC, sin incluir ese último instante.

Al leer una venta se convierte el timestamp UTC a Perú y se separa en:

- `fechaVenta`: LocalDate, como las fechas de Reserva del hotel.
- `horaVenta`: LocalTime, para mostrar la hora en la tabla del reporte.

El reporte suma `totalPagar`, suma `cantidadAsientos` y cuenta las ventas.
Cantidad de ventas y cantidad de entradas no son lo mismo: una venta puede
contener varias entradas. Los importes del resumen se acumulan con BigDecimal
a partir de los valores guardados y se muestran con dos decimales.

No hace falta una tabla Cliente para esta consulta. El nombre ya está
guardado en cada venta. Crear fichas de clientes con DNI e historial sería
otra función, que no se agregó artificialmente.

## 10. Los tests y las relaciones

`main` es el punto desde el que Java empieza a ejecutar una clase. `static`
permite llamarlo sin crear un objeto de la clase de prueba. `Scanner` lee
la consola y `switch` selecciona qué operación ejecutar.

Los tests son pruebas manuales, no pruebas JUnit. Cada menú contiene las
cinco operaciones DAO. El test de reporte muestra hoy o recibe una fecha
como argumento. El test de conexión solo verifica que puede abrir SQLite.

Los datos escritos en estos menús se guardan en la base real. Para crear
datos relacionados, sigue este orden:

```text
Película y Sala → Función → Venta → Reporte
```

Para eliminar datos relacionados, recorre el camino inverso. No se permite
borrar una película o sala usada por una función ni una función con ventas.
Estas comprobaciones están en los DAO, porque la base existente no declara
claves foráneas. No impiden que alguien cambie las tablas directamente con
otra herramienta SQL fuera del proyecto.

El control de asientos disponibles y la detección de cruces de horarios
siguen fuera de lo implementado. Consulta `COMPARACION_CON_HOTEL.md` para
ver las correcciones verificadas y las diferencias intencionales.
