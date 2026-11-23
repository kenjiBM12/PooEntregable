# NOTAS DE CAMBIOS — Sistema de gestión de cine (CINEMAX)

Curso: POO — UPeU, campus Tarapoto. Proyecto JavaFX + FXML + DAO + SQLite.
Trabajo hecho sobre la rama `main`, **sin commit, sin push, sin cambiar de rama**.

> **LEA ESTO PRIMERO**
> El sistema ahora guarda el **código de compra** en la tabla `venta`. Ese dato necesita
> una columna nueva en tu base de datos. **Antes de abrir la pantalla "Venta de entradas"**
> ejecuta una sola vez `sql/migracion.sql` (instrucciones en el punto 4).
> Si no lo ejecutas, la pantalla de ventas avisa "Error de base de datos: no such column:
> v.codigo_compra" y no muestra la lista. El resto del sistema funciona igual.
> El script **no fue ejecutado** sobre tu base real (regla de seguridad del trabajo).

---

## 0. Cómo está organizado el proyecto

```
src/main/java/pe/edu/upeu/gestioo_de_cine/
├── modelo/          Cliente, Pelicula, Sala, Funcion, Butaca, Venta, Usuario
├── interfaces/      IClienteDao, IPeliculaDao, ISalaDao, IFuncionDao,
│                    IButacaDao, IVentaDao, IUsuarioDao (+ IHistorial/IReporte sin uso)
├── implementacion/  *DaoImpl  (SQL con PreparedStatement)
├── controller/      un controlador por pantalla FXML
├── utils/           ConexionDB (conexión única compartida), Sesion, UtilAlerts
└── test/            clases con main() para probar DAOs a mano

src/main/resources/.../views/   login, main, home, 7 vistas de listado,
                                8 formularios modales y style.css (un solo CSS)
sql/                            esquema.sql (instalación nueva) y migracion.sql
```

Flujo general de cada acción:
`FXML (onAction)` → `controlador` → `interfaz DAO` → `*DaoImpl` → SQLite → el controlador
recarga la tabla.

### Lista de `fx:id` y `onAction` por pantalla (se usó al final para comprobar que nada se rompió)

| FXML | Controlador | fx:id | onAction |
|---|---|---|---|
| login-view | LoginController | txtUsuario, txtClave, lblMensaje | handleLogin |
| main-view | MainController | contentArea, lblUsuario, lblRol, btnInicio, btnClientes, btnPeliculas, btnSalas, btnFunciones, btnVentas, btnButacas, btnUsuarios | showHome, showCliente, showPeli, showSal, showFuncion, showVenta, showButacas, showUsuarios, handleLogout |
| home-view | (sin controlador) | mainContainer, imgCine | — |
| cliente-view | ClienteController | tableCliente, ColNumero, ColId, ColNombre, ColApellidos, ColDni, ColEmail, ColGenero, ColTelefono, txtBuscar | handleNew, handleEdit, handleDelete, handleBuscar |
| cliente-form | ClienteFormController | lblTitulo, txtNombre, txtApellidos, txtDni, txtEmail, cbGenero, txtTelefono | handleSave, handleCancel |
| pelicula-view | PeliculaController | tablePelicula, ColNumero, ColNumeroPelicula, ColId, ColNombre, ColDuracion, ColCategoria, ColClasificacion, txtBuscar | handleNew, handleEdit, handleDelete, handleBuscar |
| pelicula-form | PeliculaFormController | lblTitulo, txtNumero, txtNombre, txtDuracion, txtClasificacion, cbCategoria | handleSave, handleCancel |
| sala-view | SalaController | tableSala, ColNum, ColNumero, ColId, ColNombre, Coltipo, ColCapacidad, ColEstado, txtBuscar | handleNew, handleEdit, handleDelete, handleBuscar |
| sala-form | SalaFormController | lblTitulo, txtNumero, txtNombre (oculto), cbTipo, txtCapacidad, txtEstado | handleSave, handleCancel |
| funcion-view | FuncionController | tableFuncion, ColNumero, ColId, ColPelicula, ColSala, ColFecha, ColHorario, ColPrecio, txtBuscar | handleNew, handleEdit, handleDelete, handleBuscar |
| funcion-form | FuncionFormController | lblTitulo, cbPelicula, cbSala, txtHorario, txtPrecio | handleSave, handleCancel |
| butaca-view | ButacaController | cboFuncion, gridButacas, lblSala, lblMensaje | handleGenerar |
| butaca-form | ButacaFormController | lblTitulo, lblSala, txtFilas, txtColumnas | handleSave, handleCancel |
| usuario-view | UsuarioController | tableUsuario, ColNumero, ColNombre, ColUsuario, ColRol, ColEstado, txtBuscar | handleNew, handleEdit, handleCambiarEstado, handleBuscar |
| usuario-form | UsuarioFormController | lblTitulo, txtNombre, txtUsuario, txtClave, cbRol | handleSave, handleCancel |
| venta-view | VentaController | tableVenta, ColNumero, ColId, ColCodigo, ColCliente, ColDni, ColPelicula, ColSala, ColFecha, ColHorario, ColButaca, ColEmpleado, ColTotal, ColEstado, txtBuscar, cbEstado, dpFecha, lblResumen | handleNew, handleBuscar, handleLimpiar, handleDelete, handleAnularCompra |
| venta-form | VentaFormController | cboPelicula, cboFuncion, gridButacas, lblButaca, lblPrecio, lblMensaje, txtDni, txtNombre, txtApellidos, txtTelefono | handleVender, handleCancel |

Los `fx:id` y `onAction` que ya existían **no se renombraron ni se borraron**. Los nuevos
(`txtBuscar`, `cbEstado`, `dpFecha`, `lblResumen`, `ColCodigo`, `ColDni`, `ColSala`,
`ColEmpleado`, `ColFecha` de funciones, `txtApellidos`) son agregados.

---

## 1. Problemas principales encontrados

**De lógica / datos (corregidos)**

1. La venta de varias butacas no era atómica: cada fila se guardaba por separado. Si fallaba la
   tercera butaca, las dos primeras quedaban vendidas.
2. El cliente nuevo se insertaba **antes** de la venta; si la venta fallaba, quedaba un cliente
   registrado sin ninguna compra.
3. La venta guardaba solo el **nombre escrito** del cliente, no su `id_cliente`.
4. `ANULAR` borraba la fila de la venta (se perdía el historial).
5. Las butacas ocupadas se calculaban sin filtrar por estado, así que una venta anulada seguía
   bloqueando la butaca (si las ventas anuladas no se borraban).
6. Generar butacas insertaba sin comprobar duplicados: una segunda ejecución duplicaba filas y
   dejaba la sala bloqueada a medias.
7. `filas x columnas` se multiplicaba con `int` (podía desbordar) y no se comparaba con la
   capacidad de la sala.
8. Los duplicados de función se comparaban como texto (`18:30` ≠ `18:30:00`), y al editar una
   función se comparaba consigo misma y siempre salía "duplicada".
9. Los DAO usaban try-with-resources sobre la **conexión compartida**, así que la cerraban y
   provocaban "database connection closed" en pantallas siguientes.
10. Varios DAO **ocultaban errores de base de datos** devolviendo `false` o una lista vacía; la
    interfaz mostraba "no hay registros" cuando en realidad había un error de SQL.
11. `FuncionDaoImpl.insert` no enviaba `fecha`, que es `NOT NULL` en la tabla (fallaba el alta).
12. No se recordaba quién inició sesión; el empleado no tenía forma de vender con su `id_usuario`.
13. El empleado veía "Inicio", pero no "Butacas", al revés de lo pedido.
14. La restricción por rol era solo visual: los métodos del controlador se podían ejecutar igual.
15. No existía búsqueda en ninguna lista, ni filtro por estado/fecha en ventas, ni forma de
    agrupar los boletos de una misma compra.
16. La tabla de ventas no mostraba código, DNI, sala ni empleado, y ordenaba por id.
17. `Sala.nombre` se guardaba como "sala"/"SALA" (sin información) y se mostraba en la tabla.
18. Los ComboBox y tablas mostraban el nombre guardado de la sala en lugar de "Sala 2 — 3D".
19. Las vistas mezclaban estilos: fondo claro `#F7F7F7` con texto azul oscuro `#014074` dentro de
    un tema oscuro, y no había forma de saber qué pantalla estaba activa en el menú.

**Encontrados y NO corregidos (documentados, no rompen nada)**

- `interfaces/IHistorial.java`, `IHistorialDao.java`, `IReporteDao.java`, `modelo/Historial.java`
  y `modelo/Reporte.java` no se usan. **No se borraron** (regla del trabajo).
- No hay llave foránea declarada entre `venta` y las demás tablas (SQLite no permite agregarla con
  `ALTER TABLE`); la relación se controla desde los DAO.
- La contraseña se guarda en texto plano. No se cambió porque no se pidió y cambiaría el login
  (`admin/admin123`, `empleado/empleado123`).
- Las clases de `test/` se compilan con el proyecto y tienen `main()`; se dejaron como estaban.

---

## 2. Cambios por bloque y por archivo

### Bloque 0 — Diagnóstico
Sin cambios de código. Se recorrieron modelos, FXML, controladores, interfaces, DAO, conexión,
esquema y CSS, y se trazaron los flujos de login, navegación, altas/ediciones, generación de
butacas, venta, consulta y anulación. La lista de `fx:id`/`onAction` está arriba.

### Bloque 1 — Atributos y relaciones
- `modelo/Venta.java`: se agregaron `codigoCompra`, `dniCliente` y `empleado` (StringProperty).
  `idCliente` ya existía. `cliente` guarda el nombre completo que viene del JOIN.
- `modelo/Funcion.java`: se agregó `fecha` (texto `yyyy-MM-dd`) para mostrarla en ventas y buscarla.
- `modelo/Sala.java`: se agregaron `getEtiqueta()` → `"Sala 2 — 3D"` y `toString()` → lo mismo.
  Así cualquier ComboBox de salas muestra la etiqueta sin tocar los controladores.
- `Sala.nombre`: **se conserva** (ver punto 3).
- `Venta.cantidadAsientos`: se mantiene la columna, pero **cada fila es un boleto** y siempre vale
  1. La cantidad real de boletos de una compra se obtiene contando filas con el mismo
  `codigo_compra`.
- `Butaca`: no tiene estado fijo; "ocupada" depende de la función y sus ventas activas.

### Bloque 2 — Lógica, datos y conexión
- `implementacion/VentaDaoImpl.java`
  - `registrarVentas(...)` ahora devuelve el **código de compra** (o `null` si una butaca ya estaba
    vendida) y hace todo en una transacción:
    `setAutoCommit(false)` → buscar/insertar cliente por DNI → revalidar butacas → generar código
    → una fila por butaca → `commit()`; ante error `rollback()`; en `finally`
    `setAutoCommit(true)`.
  - `generarCodigoCompra()`: `MAX(SUBSTR(codigo_compra,3))` + 1 → `C-0001`, dentro de la misma
    transacción.
  - `anular()`: `UPDATE venta SET estado='anulada'` (no borra).
  - `anularCompra(codigo)`: anula todos los boletos activos de ese código.
  - `listAll()`: ordena por `COALESCE(codigo_compra,'V-'||idventa)` para agrupar boletos y usa
    JOIN con cliente, usuario, función, película, sala y butaca.
  - Las ventas antiguas sin código se muestran como `V-<id>`.
  - Errores de SQL se lanzan como `RuntimeException` (no se devuelven listas vacías).
- `implementacion/ButacaDaoImpl.java`: `generarButacas()` es transaccional, usa `long` para
  filas×columnas, valida contra la capacidad y no duplica butacas existentes
  (`existeButaca`). `listarOcupadas()` filtra `estado='activa'`. `contarPorSala()` para completar
  salas incompletas.
- `implementacion/ClienteDaoImpl.java`: inserta y devuelve el id generado; ya no cierra la conexión
  compartida; los errores se lanzan.
- `implementacion/FuncionDaoImpl.java`: incluye `fecha` en el SELECT y en el INSERT; errores
  lanzados; conexión compartida no se cierra.
- `implementacion/{Pelicula,Sala,Usuario}DaoImpl.java`: mismo criterio (conexión compartida, sin
  tragarse errores).
- `interfaces/IVentaDao.java`: `registrarVentas` devuelve `String`; se agregó `anularCompra`.
- `interfaces/IButacaDao.java`: `contarPorSala`, `generarButacas`.
- `controller/VentaFormController.java`: límite de **10 boletos** por compra; pide DNI, nombres,
  apellidos y teléfono; reutiliza el cliente por DNI; muestra el comprobante con código, cliente,
  DNI, lista de boletos, total y empleado.
- `controller/VentaController.java`: ANULAR BOLETO y ANULAR COMPRA con confirmación; resumen de
  boletos activos y total (el dinero solo lo ve el admin); recarga la tabla.
- `controller/ButacaController.java` / `ButacaFormController.java`: generación/completado de
  butacas con mensajes claros; "generar" solo para admin.
- `controller/FuncionFormController.java`: duplicados comparados con `LocalTime`, se salta la
  propia función y muestra las salas como "Sala 2 — 3D".
- `controller/SalaFormController.java`: ya no pide el nombre; guarda `"Sala " + numero`.
- `utils/ConexionDB.java`: **no se tocó** (ruta `jdbc:sqlite:C:/sqlite/db/gestionCine.db`,
  usuario/contraseña/base iguales). La estrategia elegida fue: los DAO **no cierran** la conexión
  compartida, solo cierran `PreparedStatement` y `ResultSet`.

### Bloque 3 — FXML → controlador → DAO y permisos
- Cada `onAction` tiene su método y cada `@FXML` su `fx:id` (comprobado automáticamente, 0 errores).
- Después de guardar/editar/eliminar/anular se recarga la tabla (`cargarDatos()`), para que el
  filtro y el resumen sigan coherentes.
- `utils/Sesion.java`: guarda el `Usuario` conectado mientras se usan las demás pantallas.
- `controller/MainController.java`: el admin ve todo y arranca en Inicio; el empleado ve solo
  **Venta de entradas** y **Butacas** y arranca en Ventas. Se resalta la pantalla activa con
  `nav-button-activo`.
- La restricción por rol también se valida **dentro** de los controladores: Clientes, Películas,
  Salas, Funciones, Usuarios y "Generar butacas" avisan "Sin permiso" si el rol no es admin.
- `loadView()` captura `RuntimeException` y muestra un mensaje claro (sin traza técnica) si falla
  la base de datos.

### Bloque 4 — Búsquedas y ventas por boleto
- `TextField txtBuscar` + botón BUSCAR (y Enter) en Clientes, Películas, Salas, Funciones, Ventas y
  Usuarios. Filtro con `for` + `toLowerCase().contains()` sobre un `ArrayList` completo; si el
  texto está vacío se muestra todo.
- Campos buscados: clientes → nombres/apellidos/DNI; películas → título/género; salas →
  número/tipo; funciones → película/sala/fecha/hora; ventas → código/cliente/DNI/película/sala/
  fecha/empleado/estado; usuarios → nombre/usuario/rol.
- Ventas: `ComboBox cbEstado` (Todas/Activa/Anulada) y `DatePicker dpFecha`, más botón LIMPIAR.
- Tabla de ventas: código, cliente, DNI, película, sala, fecha, hora, butaca (`A5`), empleado,
  precio y estado. Los IDs se guardan pero no se muestran.
- Estados con color y texto: `.estado-activa` verde, `.estado-anulada` rojo.
- Resumen debajo de la tabla: boletos activos y total en dinero (las anuladas no suman).
- Código de compra único por compra, generado al confirmar, dentro de la transacción.

### Bloque 5 — Esquema SQLite
- `sql/migracion.sql`: script único y comentado con todos los cambios
  (`codigo_compra`, índices, estado, butacas sin duplicados y explicación de las columnas que ya
  existen). Probado sobre una copia (11 comprobaciones OK).
- `sql/esquema.sql`: actualizado para instalaciones nuevas (incluye `codigo_compra`).

### Bloque 6 — Vistas con identidad de cine
- Un solo `style.css` para todas las pantallas (paleta: fondo `#12151C`, panel `#1B1F2B`, acento
  dorado `#E4B04A`, peligro `#B23A3A`).
- Se eliminaron los estilos en línea que peleaban con el tema (fondo claro + texto azul).
- Cabecera con marca CINEMAX, menú lateral con título "MENÚ" y pantalla activa resaltada, pie de
  página.
- Cada listado: título + subtítulo, barra de búsqueda en panel propio, tabla con filas alternadas y
  mensaje cuando no hay datos (`placeholder`).
- Formularios: `form-dialogo` oscuro, etiquetas visibles, `promptText` con ejemplos, botón
  secundario neutro y principal dorado.
- Butacas: libre = número, elegida = dorada, ocupada = `✕` (símbolo además del color) y "PANTALLA".
- Login con panel de marca CINE/UPeU y formulario oscuro (ya existía, se mantuvo).
- No se rompió ningún `fx:id`, `onAction` ni `fx:controller`; solo se agregaron controles.

### Bloque 7 — Lo que faltaba
- Login leyendo `usuario` con `rol` y `activo`: **ya existía**, se mantuvo.
- Registro de cuentas solo para admin, sin repetir nombre de acceso y con lista de usuarios:
  **ya existía**, se le agregó búsqueda y validación de rol en cada acción.
- Datos del cliente en la venta (DNI, nombres, apellidos, teléfono): se agregó el campo
  **apellidos** (antes no se pedía) y se reutiliza el cliente por DNI dentro de la transacción.
- Historial de ventas: la pantalla de Ventas muestra el detalle del bloque 4; el **total en dinero
  solo lo ve el admin** (el empleado ve la cantidad de boletos activos).

### Bloque 8 — Verificación
Ver el punto 6.

---

## 3. Atributos renombrados, quitados o conservados

| Atributo | Decisión | Razón |
|---|---|---|
| `Sala.nombre` | **Se conserva** en modelo, DAO y base; **deja de mostrarse** | En la base real sus únicos valores son `sala` y `SALA` (no aportan información). No se puede quitar sin reconstruir la tabla y es `NOT NULL`; el formulario ya no lo pide y guarda `"Sala " + numero`. Se muestra `"Sala 2 — 3D"` con `getEtiqueta()` |
| `Sala.numero` | Conservado, obligatorio y único | Ya era `UNIQUE` en la base |
| `Sala.tipo` | Conservado (`2D`, `3D`, `XD`, `VIP`) | Se usa en la etiqueta |
| `Venta.cantidadAsientos` | Conservado, siempre `1` por fila | Cada fila es un boleto; el total se cuenta por `codigo_compra` |
| `Venta.cliente` (texto) | Conservado | Es `NOT NULL` en la base; se sigue guardando el nombre completo, pero la relación real es `id_cliente` |
| `Venta.idCliente` | Agregado antes, se mantiene | Referencia estable al cliente |
| `Venta.codigoCompra` | **Nuevo** | Agrupa los boletos de una compra |
| `Venta.idUsuario` | Conservado y usado | Empleado que vendió (se muestra con JOIN) |
| `Pelicula.numero` | Conservado | Código interno; se muestra como "Código" |
| `Pelicula.duracion` | Conservado como texto | No se cambió el tipo para no romper datos existentes |
| `Cliente.email`, `genero` | Conservados | No se piden en la venta, sí en el alta de cliente |
| `Cliente.telefono` | Conservado y ahora usado en la venta | Lo pide el bloque 7 |
| `Funcion.fecha` | **Nuevo en el modelo** | La columna ya existía en la base y es `NOT NULL` |
| `Butaca.ocupada` | Conservado, solo en memoria | No se guarda: se calcula por función |

Ningún atributo se renombró, para no romper FXML, controladores ni consultas.

---

## 4. Cambios de base de datos necesarios y cómo aplicarlos

Archivo: **`sql/migracion.sql`** (SQLite, comentado paso a paso).

Cambios:
1. `ALTER TABLE venta ADD COLUMN codigo_compra TEXT;` (nuevo)
2. `CREATE INDEX ix_venta_codigo_compra` (nuevo)
3. `UPDATE venta SET estado='activa' WHERE estado IS NULL OR estado=''` (limpieza)
4. `CREATE UNIQUE INDEX ux_butaca_sala_fila_numero` (evita butacas duplicadas)
5. `CREATE INDEX ix_venta_funcion_butaca` (butacas ocupadas más rápido)
6. Bloque **comentado** con `id_cliente`, `id_usuario` y `estado`: **ya existen** en tu base.
7. Bloque **comentado** con el método para quitar `sala.nombre` (no aplicado).

Cómo ejecutarlo (una sola vez, con el programa cerrado):
1. Copia `C:/sqlite/db/gestionCine.db` a otro sitio (respaldo).
2. Abre **DB Browser for SQLite** → *Open Database* → `gestionCine.db`.
3. Pestaña **Execute SQL** → pega el contenido de `sql/migracion.sql` → botón **Play** (▶).
4. Pestaña **Write Changes** (💾) para guardar.
5. Abre el sistema y entra a *Venta de entradas*.

Para una instalación nueva (sin base) se usa `sql/esquema.sql`.

**No se ejecutó sobre la base real.** Sí se ejecutó sobre una **copia** (`target/tmp/test.db`) con
resultado 11/11 comprobaciones OK.

---

## 5. Decisiones tomadas ante dudas

1. **Conexión compartida**: se eligió que los DAO **no la cierren** (solo cierran statements), en
   lugar de abrir una conexión por consulta. Es el cambio más pequeño y conserva el
   comportamiento y los datos. `ConexionDB` quedó intacto.
2. **Transacciones en el DAO** con `setAutoCommit(false)` / `commit` / `rollback` /
   `setAutoCommit(true)`, sin clases nuevas ni patrones: se explica en segundo ciclo y no cambia la
   arquitectura.
3. **Los errores de BD se lanzan** como `RuntimeException` con mensaje en español y el detalle
   técnico va a consola. La interfaz muestra un mensaje entendible (nunca una traza).
4. **`Sala.nombre`** se conserva y se oculta (ver punto 3), porque no se puede quitar sin
   reconstruir la tabla y la base la exige `NOT NULL`.
5. **Ventas antiguas sin código**: se muestran como `V-<id>`, sin inventar códigos nuevos, tal
   como pide el bloque 4.
6. **Historial y dinero**: el empleado necesita la pantalla de Ventas para vender, así que la lista
   se le muestra; el **total en dinero** se oculta para el empleado (queda solo para el admin).
7. **Generar butacas**: el empleado puede ver el mapa de butacas, pero **generar** (cambia la
   estructura de la sala) es solo del admin.
8. **Migración sin ejecutar**: no se tocó la base real. Si no se aplica, solo falla la pantalla de
   ventas (se avisa en pantalla); todo lo demás sigue funcionando.
9. **`Funcion.fecha`**: el formulario no pide fecha; se usa la fecha del día, que es lo que ya
   hacía el DAO, y ahora se guarda también en el objeto para mostrarla.
10. **`cantidad_asientos`**: se deja la columna con valor 1 por boleto en lugar de eliminarla,
    porque eliminarla obligaría a reconstruir la tabla de ventas sin ganar nada.

---

## 6. Verificaciones

| # | Verificación | Resultado |
|---|---|---|
| 1 | Compilación Maven (`mvnw -B -DskipTests compile`) | **Ejecutada y exitosa** — BUILD SUCCESS, EXIT=0 |
| 2 | `fx:id` de cada FXML contra los campos `@FXML` de su controlador | **Ejecutada y exitosa** — 16 FXML, 0 faltantes |
| 3 | `onAction` de cada FXML contra los métodos del controlador | **Ejecutada y exitosa** — 0 faltantes |
| 4 | `fx:controller` apunta a una clase existente | **Ejecutada y exitosa** — 16/16 |
| 5 | Carga real de cada FXML con `FXMLLoader` (JavaFX) | **16 de 17 OK**; `venta-view` falla **solo** porque la base real aún no tiene `codigo_compra` (no es un error del FXML: el archivo se analiza, el controlador se crea y todos los `fx:id` se inyectan; la excepción viene de la consulta SQL) |
| 6 | `sql/migracion.sql` sobre una **copia** de la base: ejecuta el script y luego las consultas del DAO | **Ejecutada y exitosa — 11 OK / 0 fallos**: columna creada, 12 ventas conservadas, JOIN de la lista, `V-<id>` para las viejas, código `C-0001` para 2 boletos, butaca ocupada solo con venta activa, anular compra toca los 2 boletos, butaca liberada, índice único rechaza duplicados |
| 7 | Venta de varias butacas / compra de una sola / fallo a mitad | **Ejecutada a nivel de SQL** (punto 6). **No se pudo ejecutar por interfaz**: el agente no puede hacer clic en las ventanas JavaFX |
| 8 | Login admin/empleado, permisos, guardar, editar, buscar, generar butacas, anular por botones | **No se pudo ejecutar** (requiere clics en la interfaz). El código de permisos y los métodos fueron revisados y compilan |
| 9 | Revisión visual de alineación, texto cortado, contraste | **No se pudo ejecutar** como revisión visual real. Se revisó el CSS/FXML a mano; el estilo se aplica (todas las vistas cargan `style.css`) |
| 10 | Que la app abra la ventana de login | **Ejecutada** en la sesión anterior: `javafx:run` quedó abierto sin excepciones (se cerró por tiempo) |

**Limitación principal:** todo lo que exige clics dentro de las ventanas (login, elegir butacas,
anular) no se puede probar desde aquí. Está verificado a nivel de DAO/SQL y de compilación.

---

## 7. Explicación simple (para defender)

### a) Transacciones: commit y rollback
Una transacción es "todo o nada". Se apaga el guardado automático y los cambios quedan
*pendientes*:

```java
cn.setAutoCommit(false);   // desde aquí nada se guarda de verdad
// ... insertar cliente, revalidar butacas, insertar 3 boletos ...
cn.commit();               // recién aquí se guarda TODO
// si algo falla:
cn.rollback();             // se descarta TODO, como si nunca hubiera pasado
cn.setAutoCommit(true);    // se restaura el modo normal (siempre en finally)
```

**Ejemplo:** Kenji vende 3 butacas. Se guarda el boleto 1, luego el 2, y en el 3 se corta la luz.
Con transacción, la base vuelve atrás: no queda ningún boleto a medias ni el cliente suelto.

### b) Permisos por rol
Al iniciar sesión se guarda el usuario en `Sesion`. En el menú, `aplicarPermisos()` oculta los
botones que el empleado no debe ver y `sinPermiso()` lo regresa a Ventas si intenta otra pantalla.
Además cada acción del controlador vuelve a preguntar:

```java
if (!Sesion.esAdmin()) {
    utilAlert.mostrarAlerts("Sin permiso", "Solo el administrador...", "WARNING").showAndWait();
    return;
}
```

Así no basta con "esconder el botón": aunque alguien llame al método, no se ejecuta.

**Ejemplo:** el empleado entra y solo ve "Venta de entradas" y "Butacas"; si intenta generar
butacas, recibe "Sin permiso".

### c) Cómo se decide qué butacas están ocupadas
No hay un estado fijo en la butaca. Se consulta la tabla `venta`: una butaca está ocupada si
existe una venta **activa** de esa butaca en esa función.

```sql
SELECT b.* FROM butaca b
INNER JOIN venta v ON v.id_butaca = b.idbutaca
WHERE v.id_funcion = ? AND v.estado = 'activa';
```

**Ejemplo:** si se anula la venta, `estado` pasa a `'anulada'` y la butaca deja de aparecer en la
lista: vuelve a estar libre sin borrar nada.

### d) Código de compra
Es un texto igual para todos los boletos de una misma compra. Se genera al confirmar, dentro de la
transacción: se toma el mayor número usado y se le suma uno.

```java
SELECT MAX(CAST(SUBSTR(codigo_compra,3) AS INTEGER)) FROM venta WHERE codigo_compra LIKE 'C-%'
// si el mayor es 7 -> String.format("C-%04d", 8) -> "C-0008"
```

**Ejemplo:** Kenji compra 2 boletos → dos filas en `venta`, ambas con `C-0008`. "Anular compra"
actualiza las dos filas a `'anulada'`. Las ventas viejas no tienen código y se muestran `V-12`
(usando su `idventa`).

### e) Búsquedas
Cada listado guarda dos listas: `todasLas...` (lo que vino de la base) y la que se muestra en la
tabla. Al escribir y pulsar BUSCAR (o Enter) se recorre la lista completa con un `for`:

```java
for (Cliente c : todosLosClientes) {
    if (texto.isEmpty() || c.getNombre().toLowerCase().contains(texto)) {
        listaClientes.add(c);
    }
}
```

Si el texto está vacío, se agregan todos: se ve todo otra vez.

**Ejemplo:** escribir "ken" en Clientes muestra solo a "Kenji"; borrar el texto y pulsar BUSCAR
vuelve a mostrar la lista completa. En Ventas hay además combo de estado y fecha.

---

## 8. Cómo volver atrás

Copia de seguridad completa del proyecto tomada **antes** de tocar nada:

```
C:\poo\U2_POO\respaldo_antes_de_cambios
```

Para volver al estado anterior:
1. Cierra el programa.
2. Copia el contenido de `respaldo_antes_de_cambios` sobre `C:\poo\U2_POO\PooEntregable`
   (o renombra la carpeta actual y usa la copia).
3. La base de datos **no se modificó**, así que no hay nada que restaurar en
   `C:/sqlite/db/gestionCine.db`. Si algún día aplicas `sql/migracion.sql`, guarda antes una copia
   del `.db` (el script solo **agrega** una columna: no borra datos).
4. No se hizo commit ni push: `git status` sigue mostrando los archivos modificados en `main`.
