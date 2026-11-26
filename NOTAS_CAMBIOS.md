# NOTAS DE CAMBIOS — Sistema de gestión de cine (CINEMAX)

Curso: POO — UPeU, campus Tarapoto. Proyecto JavaFX + FXML + DAO + SQLite.
Trabajo hecho sobre la rama `main`, **sin commit, sin push, sin cambiar de rama**.

> **ACTUALIZACIÓN 25/11/2026**: las correcciones de esta sesión están al final, en el
> punto 9. Lo más importante: la sala ahora se guarda con sus butacas en una sola
> transacción, la cuadrícula se sincroniza al editarla y una película con ventas ya
> **no** se puede eliminar (antes el DAO borraba esas ventas).

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

---

## 9. Actualización 25/11/2026 — salas/butacas, borrado seguro y edición

### 9.1 Por qué "no se podía editar nada"

Al probar un `BEGIN IMMEDIATE` sobre una **copia** de la base, SQLite respondió
`SQLITE_BUSY: database is locked`. En la máquina siguen abiertas **7 instancias de
`SistemaCineUPeU-1.0.0.exe`** (del 23/11) y existe `gestionCine.db-journal` (del 24/11,
una escritura que no terminó). Esas ventanas viejas mantienen la base tomada. Se agregó
`PRAGMA busy_timeout = 8000` en `ConexionDB` para esperar unos segundos en vez de fallar
enseguida, pero la solución definitiva es cerrar esos procesos:

```
taskkill /IM SistemaCineUPeU-1.0.0.exe /F
```

No se cerraron desde aquí porque son ventanas visibles del usuario. Tampoco se borró el
`-journal`: SQLite lo usa para recuperar la última escritura.

> **Corrección (ver sección 10):** cerrar esas 7 ventanas **no** liberó la base. El bloqueo
> real lo tenía **DB Browser for SQLite** con la base abierta; al cerrarlo todo volvió a
> funcionar. El `busy_timeout` sí sirve para los choques normales entre ventanas del sistema.

### 9.2 Archivos y métodos corregidos

| Archivo | Método | Qué se corrigió |
|---|---|---|
| `implementacion/SalaDaoImpl.java` | `insert` | La sala, su capacidad (filas x columnas) y **todas sus butacas** se guardan en una sola transacción, con `getGeneratedKeys` y `rollback` si algo falla. |
| `implementacion/SalaDaoImpl.java` | `update` | Al cambiar filas/columnas actualiza la capacidad, quita las butacas que sobran y agrega las que faltan, en una transacción. Si una butaca que se quitaría tiene ventas, rechaza el cambio. |
| `implementacion/SalaDaoImpl.java` | `insert`/`update` | Valida filas y columnas > 0, limita a 100 x 100 (10 000 butacas) y da un mensaje claro si el número de sala ya existe. |
| `implementacion/PeliculaDaoImpl.java` | `delete` | **Ya no borra ventas.** Si la película tiene boletos vendidos, lanza el aviso y no borra nada. Si solo tiene funciones sin ventas, las elimina junto con la película en una transacción. |
| `implementacion/FuncionDaoImpl.java` | `listAll` | Ya no cierra la conexión compartida (solo `PreparedStatement` y `ResultSet`) y lanza `RuntimeException` en vez de devolver una lista vacía al ocultar el error. |
| `implementacion/FuncionDaoImpl.java` | `delete` | El mensaje ya no dice "anule las ventas" (anular no borra la fila); ahora explica que se conserva el historial. |
| `controller/ButacaFormController.java` | `validate`/`handleSave` | Permite **editar filas y columnas** desde la pantalla de butacas, actualiza la capacidad y la cuadrícula de la sala, y muestra el motivo si el cambio afecta butacas vendidas. |
| `controller/ButacaController.java` | `handleGenerar`/`cargarButacas` | El botón ya no bloquea el formulario cuando la sala está completa; se puede corregir la cuadrícula. El mapa relee la sala para usar las columnas actuales. |
| `controller/SalaController.java` | `handleNew`/`handleEdit`/`handleDelete` | Se agregó la validación de rol ("Sin permiso") y el mensaje de guardado indica que se generaron/ajustaron las butacas. |
| `controller/PeliculaController.java` | `handleDelete` | Antes avisaba que borraría los boletos; ahora bloquea el borrado y explica que se conserva el historial. |
| `views/sala-form.fxml`, `views/butaca-form.fxml`, `views/butaca-view.fxml` | textos | Se explicó que las butacas se generan/ajustan automáticamente y el botón pasó a "ADMINISTRAR BUTACAS". |

No se renombró ningún `fx:id`, `onAction` ni método existente. `ConexionDB` mantiene la ruta
`%USERPROFILE%\SistemaCineUPeU\gestionCine.db`, que en esta máquina es
`C:\Users\ajpev\SistemaCineUPeU\gestionCine.db` (Windows no distingue mayúsculas en la ruta).

### 9.3 Verificaciones de esta sesión

| Verificación | Resultado |
|---|---|
| Esquema real leído sobre una copia (7 tablas) | **Ejecutada y exitosa**: `sala` sin `nombre`, con `filas`/`columnas`; `funcion.fecha NOT NULL`; `venta.codigo_compra` ya existe; 0 ventas. |
| `mvnw compile` | **Ejecutada y exitosa** (`EXIT=0`). |
| Los 17 FXML son XML válido | **Ejecutada y exitosa** (17/17). |
| `onAction` y `fx:id` contra los controladores | **Ejecutada**: 0 métodos faltantes; `home-view` no tiene controlador (es solo diseño) y `btnCancelar1` es un `fx:id` sin campo, sin uso. |
| Pruebas de DAO sobre una **copia** de la base | **Ejecutada y exitosa — 14/14**: alta de sala 2x3 con 6 butacas, número repetido rechazado, edición a 2x2, cambio rechazado por ventas, película con ventas no se borra, película sin ventas borra sus funciones, `listAll`/`searchById` de funciones siguen funcionando. |
| Interfaz JavaFX (abrir, hacer clic, editar/eliminar de verdad) | **No se pudo ejecutar**: requiere clics y, además, la base real sigue tomada por los 7 procesos del `.exe`. |

### 9.4 Pendientes que se mantienen

1. Cerrar las 7 instancias del `.exe` y ejecutar el sistema desde IntelliJ o `mvnw javafx:run` (el `.exe` del 23/11 no tiene estas correcciones).
2. Al vender a un cliente que ya existe solo se reutiliza su `id`; sus datos (teléfono, correo) no se actualizan.
3. La tabla `venta` no tiene llave foránea (SQLite no permite agregarla con `ALTER TABLE`); la relación se controla en los DAO.
4. La contraseña sigue en texto plano (`admin/admin123`). No se cambió porque no se pidió.
5. `ButacaDaoImpl.generarButacas` quedó disponible pero la pantalla ya no la usa: ahora la cuadrícula se sincroniza con `SalaDaoImpl.update`.
6. No se ejecutó ningún cambio de esquema sobre la base real.

---

## 10. Aviso "El cliente no se actualizó" (25/11/2026)

### 10.1 Qué pasaba realmente

El aviso de la pantalla de Clientes no era un problema del `UPDATE`. Se comprobó así:

| Prueba | Resultado |
|---|---|
| `UPDATE cliente SET ... WHERE idcliente=?` con los mismos datos, sobre una **copia** de la base | **Ejecutada y exitosa**: las 2 filas se actualizan (`filas=1`). |
| `BEGIN IMMEDIATE` + `ROLLBACK` sobre la base **real** (no cambia datos) | **Ejecutada y falló**: `[SQLITE_BUSY] The database file is locked (database is locked)`. |
| `SELECT COUNT(*) FROM cliente` sobre la base real | **Ejecutada y exitosa**: lectura sí, escritura no. |

Es decir: la base se puede **leer** pero no **escribir**. El `busy_timeout = 8000` hace que
el sistema espere 8 segundos antes de rendirse; al agotarse, el `catch` mostraba el texto
fijo "Revise si el DNI ya existe", que no era la causa.

Al principio se culpó a las 7 instancias de `SistemaCineUPeU-1.0.0.exe` del 23/11, pero al
cerrarlas todas el bloqueo **continuó**. Se consultó la API `Restart Manager` de Windows
(«qué proceso tiene abierto este archivo») y el dueño real era:

```
PID 2312 | DB Browser for SQLite | DB Browser for SQLite   (gestionCine.db y gestionCine.db-journal)
```

DB Browser tenía la base abierta con una edición sin confirmar; eso mantenía el
`gestionCine.db-journal` del 24/11 17:44 y un bloqueo de escritura (RESERVED). Al cerrarlo,
SQLite deshizo ese `-journal`, el archivo se liberó y las escrituras volvieron a funcionar.
Lección: si vuelve a salir "database is locked", revisar si DB Browser (o cualquier otro
programa) tiene abierta la base.

### 10.2 Cambio hecho

`controller/ClienteController.java`:

| Método | Qué se cambió |
|---|---|
| `showForm` (`catch` de `insert` y de `update`) | Ahora llama a `mensajeError(...)` en lugar de usar el texto fijo. |
| `mensajeError` (nuevo) | Si el error de SQLite dice `locked`/`busy`, avisa "Otra ventana del sistema tiene la base de datos abierta. Cierre todas las ventanas del programa y vuelva a intentarlo". En cualquier otro caso muestra el mensaje de siempre **más** el detalle real de SQLite (por ejemplo el DNI repetido). |

No se tocaron DAO, FXML, ni la base real. Con las ventanas viejas cerradas el guardado
funciona como antes; el cambio solo evita que un bloqueo se confunda con un DNI repetido.

### 10.3 Verificación

| Verificación | Resultado |
|---|---|
| `mvnw compile` | **Ejecutada y exitosa** (`EXIT=0`). |
| `UPDATE` de cliente contra una copia de la base | **Ejecutada y exitosa**: las 2 filas, `filas=1`. |
| `BEGIN IMMEDIATE` sobre la base real antes de liberarla | **Ejecutada y falló**: `SQLITE_BUSY`. |
| Cerrar las 7 instancias del `.exe` | **Ejecutada y exitosa** (PIDs 7260, 3744, 17064, 16648, 2824, 12600, 18312), pero el bloqueo siguió. |
| Identificar el dueño del archivo (Restart Manager) | **Ejecutada y exitosa**: DB Browser for SQLite, PID 2312. |
| `BEGIN IMMEDIATE` sobre la base real después de cerrar DB Browser | **Ejecutada y exitosa**: ya no hay bloqueo y el `-journal` desapareció. |
| `ClienteDaoImpl.update` contra la base **real** (los 2 clientes, reescribiendo sus mismos datos) | **Ejecutada y exitosa**: `update=true` en ambos, sin excepción y sin dejar `-journal`. |
| Guardar desde la interfaz (hacer clic en el formulario) | **No se pudo ejecutar desde aquí**: falta el clic; conviene repetir la prueba en la ventana del sistema. |

---

## 11. Actualización 26/11/2026 — las 10 mejoras implementadas

Se implementaron las 10 mejoras propuestas, con el mismo estilo del curso
(segundo ciclo de POO): clases y `ArrayList`, DAO con `PreparedStatement`,
transacciones, JavaFX/FXML y **comentarios en español** explicando cada cambio.
No se agregaron librerías nuevas (solo `java.security`, `java.nio.file` que ya
vienen con Java) y no se tocó la base real: cuando se abra el sistema por primera
vez, `ConexionDB` agrega las columnas nuevas **después de crear un respaldo**.

### 11.1 Qué se hizo en cada mejora

| # | Mejora | Archivos principales | Cómo se prueba en la interfaz |
|---|---|---|---|
| 1 | Esquema único | `utils/ConexionDB.java`, `sql/esquema.sql` | Instalar de cero con `esquema.sql` y abrir el sistema: ya no falta ninguna columna |
| 2 | Respaldo automático | `utils/RespaldoDB.java`, `ConexionDB` | Abrir el sistema y mirar `%USERPROFILE%\SistemaCineUPeU\respaldos` |
| 3 | Reportes / cierre de caja | `modelo/Reporte`, `IReporteDao`, `ReporteDaoImpl`, `ReporteController`, `reporte-view.fxml`, botón en `main-view` | Menú **Reportes** → elegir fechas → GENERAR |
| 4 | Integridad de clientes | `ClienteDaoImpl.delete`, `VentaDaoImpl.actualizarCliente`, `VentaFormController` | Vender a un DNI existente cambiando el teléfono; intentar borrar un cliente con compras |
| 5 | Funciones sin choques / sin vender funciones pasadas | `FuncionFormController`, `VentaFormController` | Programar 18:00 (120 min) y luego 19:00 en la misma sala: debe rechazar |
| 6 | Forma de pago + auditoría de anulación | `modelo/Venta`, `VentaDaoImpl`, `IVentaDao`, `VentaFormController`, `VentaController`, FXML de venta | Vender eligiendo Efectivo/Tarjeta/Yape; anular pidiendo motivo; ver columna Anulación |
| 7 | Contraseñas SHA-256 | `utils/Seguridad.java`, `UsuarioDaoImpl` | Entrar con `admin/admin123`: la clave se cambia sola por su hash |
| 8 | Índice único parcial + llaves foráneas | `ConexionDB`, `sql/esquema.sql`, `sql/migracion.sql` | Vender dos veces la misma butaca en la misma función: la segunda venta no se guarda |
| 9 | Comprobante guardable + rango de fechas | `VentaFormController.guardarComprobante`, `VentaController`, `venta-view.fxml` | Al vender se avisa la ruta del `.txt`; en Ventas hay fecha **Desde** y **Hasta** |
| 10 | `.exe` con `jpackage` + código muerto justificado | `empaquetar_exe.bat`, comentarios en `Historial`/`IHistorial`/`IHistorialDao` | Ejecutar `empaquetar_exe.bat` y abrir `target\exe\SistemaCineUPeU\SistemaCineUPeU.exe` |

**Explicación corta de cada una:**

1. **Esquema único.** El código usaba `pelicula.titulo` y `sala.tipo_sala`, pero
   `sql/esquema.sql` definía `nombre` y `tipo`. Se alineó el `.sql` con el código
   (que es lo que usa la base real) y se separaron los `CREATE INDEX`, porque
   antes `sqlite-jdbc` solo ejecutaba el primero del bloque.
2. **Respaldo.** Antes de abrir la conexión se copia el `.db` a `respaldos` con
   fecha y hora. Se conservan las 10 copias más recientes. Un respaldo que falla
   no impide usar el sistema.
3. **Reportes.** `ReporteDaoImpl` usa `SUM(CASE WHEN ...)` para contar boletos
   activos/anulados y sumar el dinero en un rango de fechas. La pantalla muestra
   5 totales y el detalle de los boletos. Es de solo administrador porque muestra
   dinero.
4. **Clientes.** `delete` cuenta las ventas del cliente y, si tiene al menos una,
   lanza un aviso y no borra. Al vender a un DNI existente, `registrarVentas`
   actualiza sus datos dentro de la misma transacción (si la venta falla, el
   `rollback` también deshace la actualización).
5. **Funciones.** Se calcula la hora de fin con la duración de la película
   (`18:00` + 120 min = `20:00`) y se rechaza si los rangos se cruzan. Además no
   se puede programar una función nueva en una fecha pasada y la venta ya no
   ofrece funciones que empezaron.
6. **Pago y auditoría.** Se agregaron `metodo_pago`, `anulada_por`,
   `fecha_anulacion` y `motivo_anulacion` a `venta`. Al anular se pide el motivo
   con `TextInputDialog` y se guarda junto con el usuario de la sesión.
7. **SHA-256.** `Seguridad.hash()` usa `MessageDigest`. El login compara el hash;
   si la base todavía tiene la clave en texto plano, la acepta una vez y la migra
   en ese momento (no hay que cambiar contraseñas a mano).
8. **Índice parcial y FK.** `CREATE UNIQUE INDEX ... WHERE estado='activa'` impide
   vender dos veces la misma butaca en una función, pero permite revenderla si la
   venta se anuló. Como SQLite no permite `ALTER TABLE ... ADD CONSTRAINT`, en las
   bases ya creadas la relación se controla con **triggers**.
9. **Comprobante y fechas.** Cada venta deja un `comprobante_C-0001.txt` en
   `%USERPROFILE%\SistemaCineUPeU\comprobantes`. La lista de ventas se filtra con
   dos `DatePicker` (desde / hasta), no con una sola fecha.
10. **Empaquetado y código muerto.** `empaquetar_exe.bat` compila y llama a
    `jpackage --type app-image`, que genera un `.exe` con su propio Java. Las clases
    vacías `Historial`, `IHistorial` e `IHistorialDao` se dejaron (regla de no
    borrar archivos) pero ahora tienen un comentario que explica que no se usan.

### 11.2 Cambios en la base de datos

Todo esto lo aplica el sistema **solo** al iniciar (`initDatabase`), y antes crea
un respaldo. También está escrito a mano en `sql/migracion.sql` (secciones 7 a 10):

- `venta.metodo_pago TEXT DEFAULT 'EFECTIVO'`
- `venta.anulada_por INTEGER`
- `venta.fecha_anulacion TEXT`
- `venta.motivo_anulacion TEXT`
- índice `ux_venta_funcion_butaca_activa` (parcial, solo `estado='activa'`)
- triggers `trg_venta_funcion_valida` y `trg_venta_butaca_valida`
- para instalaciones nuevas, `venta` declara las 4 llaves foráneas

Ninguna columna se borra y ninguna venta se modifica: solo se **agregan** columnas
(con `ALTER TABLE ... ADD COLUMN`, que conserva los datos).

### 11.3 Verificaciones de esta sesión

| Verificación | Resultado |
|---|---|
| `mvnw compile` | **Ejecutada y exitosa** (`COMPILE_EXIT=0`) |
| `mvnw -DskipTests package` | **Ejecutada y exitosa** (`PACKAGE_EXIT=0`) |
| XML de los 18 FXML | **Ejecutada y exitosa**: 18 válidos, 0 con error |
| `fx:id` / `onAction` contra los controladores | **Ejecutada**: 0 acciones faltantes; único `fx:id` sin campo: `btnCancelar1` (ya existía y no se usa) |
| Prueba funcional sobre base **nueva** en carpeta temporal | **Ejecutada y exitosa — 25/25**: columnas e índice creados, venta con Tarjeta, cliente actualizado, butaca repetida rechazada, cliente con compras no se borra, anulación con motivo y usuario, butaca revendida, reporte con totales, hash y migración de contraseña, admin hasheado, respaldo creado |
| Migración sobre una **copia** de la base real | **Ejecutada y exitosa — 10/10**: columnas, índice y triggers creados; 3 ventas y 2 clientes conservados |
| `jpackage --type app-image` | **Ejecutada y exitosa** (`JPACKAGE_EXIT=0`): `target\exe\SistemaCineUPeU\SistemaCineUPeU.exe` |
| Abrir el `.exe` / hacer clic en la interfaz | **No se pudo ejecutar** desde aquí: abre una ventana en la PC del usuario y además migraría la base real. El `.exe` se generó y se revisó su estructura, pero no se lanzó |

### 11.4 Limitaciones que conviene saber (para la defensa)

- SHA-256 **sin sal** es suficiente para el curso, pero un sistema real usa sal o
  `bcrypt`/`argon2` para que dos claves iguales no den el mismo hash.
- `target\dist` quedó bloqueado por un proceso abierto: por eso
  `empaquetar_exe.bat` usa `package` y no `clean`. Cerrar el `.exe` anterior y
  borrar `target\dist` a mano si alguna vez molesta.
- Las ventas viejas sin código siguen mostrándose como `V-<id>`.
- Los botones y formularios se revisaron por código, no con clics reales.

### 11.5 Conceptos de POO / BD que se pueden explicar con estos cambios

- **Encapsulamiento**: los campos de `Venta` y `Reporte` son privados con
  `get`/`set`; las propiedades de JavaFX avisan a la tabla cuando cambian.
- **Interfaz + implementación**: `IReporteDao` / `ReporteDaoImpl`, igual que los
  demás DAO (`IClienteDao` / `ClienteDaoImpl`).
false)`, `commit()`, `rollback()` y
  `setAutoCommit(true)` en `finally` (venta + actualización de cliente juntos).
- **PreparedStatement**: todos los valores van con `?`, nunca concatenados.
- **Manejo de errores**: los DAO lanzan `RuntimeException` con mensaje en español;
  la pantalla lo muestra, no lo esconde.
- **Seguridad**: `MessageDigest` (SHA-256) y migración de datos viejos sin perder
  usuarios.
- **SQL**: `SUM(CASE WHEN ...)` para totales, índice parcial `WHERE`, triggers
  `BEFORE INSERT` con `RAISE(ABORT, ...)`.
---

## 12. Corrección del cierre de caja y de la anulación (26/11/2026)

### 12.1 El cierre de caja usa la fecha de VENTA

Antes el reporte filtraba por `funcion.fecha`, es decir, por el día en que se
proyecta la película. Eso hacía que una entrada vendida hoy para una función del
mes siguiente no apareciera en el cierre de hoy (el dinero ya había entrado).

Ahora se usa `venta.fecha_venta`:

- `implementacion/ReporteDaoImpl.java`: el `WHERE` compara
  `substr(COALESCE(NULLIF(v.fecha_venta,''), f.fecha),1,10) BETWEEN ? AND ?`.
  Se usa `COALESCE` con la fecha de la función solo como respaldo para ventas muy
  viejas que no tengan `fecha_venta`, así no desaparecen del reporte.
- `controller/ReporteController.java`: el detalle de la tabla y la columna
  **Fecha** también usan la fecha de venta (método nuevo `fechaCierre(Venta)`).

### 12.2 Un boleto anulado no se puede anular otra vez

- `implementacion/VentaDaoImpl.java`: `anular(...)` ahora actualiza con
  `WHERE idventa=? AND estado='activa'`. Si el boleto ya estaba anulado, el
  `UPDATE` no toca ninguna fila y el método devuelve `false`, sin sobrescribir el
  motivo ni el usuario de la primera anulación.
- `controller/VentaController.java`: cuando el DAO devuelve `false`, la pantalla
  avisa "Ese boleto ya estaba anulado" y recarga la tabla.
- `anularCompra(...)` ya tenía esa misma condición desde antes.

### 12.3 Verificación

| Prueba | Resultado |
|---|---|
| `mvnw compile` | **exit 0** |
| Prueba sobre base nueva en carpeta temporal (`PruebaCorreccion`) | **8/8 OK**: la venta hecha hoy para una función del 2020-01-01 aparece en el cierre de HOY y **no** en el de la fecha de la función; la primera anulación funciona, la segunda se rechaza y el motivo original no se sobrescribe |
| Interfaz JavaFX (clic en Reportes / ANULAR BOLETO) | **No ejecutada**: requiere clics del usuario |

---

## 13. Mejoras 1 a 10 (26/11/2026)

Se implementaron las diez mejoras propuestas, manteniendo el estilo del curso:
clases con `get`/`set`, `ArrayList`, DAO con interfaz, JDBC con
`PreparedStatement`, transacciones y JavaFX/FXML. No se agregaron librerías
nuevas (JUnit ya estaba declarado en el `pom`).

### 13.1 Qué hace cada mejora y en qué archivo

| # | Mejora | Archivos principales |
|---|---|---|
| 1 | **Duración normalizada a minutos**: `"1:50"` ahora se lee como 110, igual que `"95 min"` o `"120"`. El formulario guarda siempre minutos y las películas viejas se convierten al abrir la base. | `utils/Duracion.java`, `PeliculaFormController`, `FuncionFormController`, `ConexionDB.normalizarDuraciones()` |
| 2 | **Cierre por forma de pago**: el reporte muestra una tabla con boletos y dinero por EFECTIVO / TARJETA / YAPE, y la tabla de Ventas tiene la columna **Pago**. | `ReporteDaoImpl.desglosePorMetodo()`, `modelo/ResumenPago`, `ReporteController`, `reporte-view.fxml`, `venta-view.fxml`, `VentaController` |
| 3 | **Historial de compras por cliente**: botón HISTORIAL en Clientes que abre una ventana con todas sus compras y el total gastado. | `ClienteController.handleHistorial()`, `ClienteHistorialController`, `cliente-historial.fxml`, `VentaDaoImpl.listarPorCliente()` |
| 4 | **Pruebas JUnit** en `src/test/java`: 8 pruebas de `Duracion` y `Seguridad`. | `src/test/java/.../DuracionTest.java`, `SeguridadTest.java`, `pom.xml` |
| 5 | **`PRAGMA user_version`**: la base guarda su versión de esquema (2) y el sistema la actualiza al migrar. | `ConexionDB.actualizarVersionEsquema()`, `sql/esquema.sql`, `sql/migracion.sql` |
| 6 | **Estado de la sala en uso**: las salas INACTIVAS no se ofrecen al programar funciones ni al vender. | `FuncionFormController` (`salaActiva`, `cbSala`), `VentaFormController.cargarFunciones()` |
| 7 | **Exportar CSV**: botón EXPORTAR CSV en Reportes. Deja `cierre_<desde>_<hasta>.csv` en `SistemaCineUPeU\reportes` con BOM para Excel. | `ReporteController.handleExportarCsv()` |
| 8 | **Restaurar respaldo**: botones RESPALDAR AHORA y RESTAURAR RESPALDO en Reportes. Antes de restaurar cierra la conexión y guarda una copia del estado actual. | `RespaldoDB.listar()/restaurar()`, `ConexionDB.archivoBase()`, `ReporteController` |
| 9 | **Auditoría**: tabla `auditoria` + pantalla nueva en el menú. Registra login, login fallido, ventas, anulaciones, exportaciones y restauraciones. | `utils/Bitacora`, `AuditoriaDaoImpl`, `modelo/Auditoria`, `AuditoriaController`, `auditoria-view.fxml`, `main-view.fxml` |
| 10 | **Inicio con métricas**: la pantalla de Inicio muestra películas, salas activas, clientes, funciones de hoy, boletos de hoy e ingresos de hoy (dinero solo para el admin). | `HomeController`, `MetricaDaoImpl`, `modelo/Metrica`, `home-view.fxml` |

Detalles de implementación que conviene poder explicar:

- **Duración**: `Duracion.aMinutos()` separa por `:` y por `h`, quita palabras
  como "min"/"minutos" y deja solo los dígitos. `normalizar()` devuelve el texto
  en minutos para guardarlo. El cruce de horarios de `FuncionFormController` ya
  no tiene su propio parser: usa esta clase.
- **Desglose de caja**: una consulta con `GROUP BY` y `WHERE v.estado='activa'`.
  Se usa `UPPER(COALESCE(NULLIF(TRIM(metodo_pago),''),'EFECTIVO'))` para que una
  venta vieja sin forma de pago cuente como efectivo y no se pierda el dinero.
- **Historial**: reutiliza la consulta base de ventas (`QUERY_BASE`) y solo agrega
  `WHERE v.id_cliente=?`, ordenado por fecha de venta descendente.
- **Pruebas**: el proyecto es modular (`module-info.java`), por eso el `pom` usa
  `--add-reads` al compilar las pruebas y `useModulePath=false` en Surefire.
- **Versión de esquema**: `PRAGMA user_version` se guarda dentro del archivo .db,
  sin tabla extra. Una base nueva queda en 2; una base vieja pasa de 0/1 a 2.
- **Salas inactivas**: se filtran en el controlador (no en el DAO) para que el
  administrador siga viendo todas las salas en su pantalla.
- **CSV**: se encierran los textos entre comillas y se duplican las comillas
  internas; el separador es `;` porque así lo abre Excel en español.
- **Restaurar**: `RespaldoDB.restaurar()` llama a `ConexionDB.cerrar()` antes de
  copiar, porque Windows no permite sobrescribir un `.db` abierto.
- **Auditoría**: `Bitacora.registrar()` nunca interrumpe la operación principal;
  si la tabla no existe, solo imprime el error.
- **Métricas**: una sola consulta con subconsultas `SELECT COUNT(*)`, sin abrir
  varias conexiones al entrar.

### 13.2 Verificaciones de esta sesión

| Verificación | Resultado |
|---|---|
| `mvnw compile` | **Ejecutada y exitosa** (`COMPILE_EXIT=0`) |
| `mvnw test` | **Ejecutada y exitosa** (`TEST_EXIT=0`): `DuracionTest` 5 pruebas, `SeguridadTest` 3 pruebas, **0 fallos, 0 errores** |
| `mvnw -DskipTests package` | **Ejecutada y exitosa** (`PACKAGE_EXIT=0`) |
| XML + `fx:id` + `onAction` de los 20 FXML | **Ejecutada y exitosa**: 20 FXML válidos, 0 acciones faltantes. Único `fx:id` sin campo: `btnCancelar1` (ya existía, sin uso) |
| Prueba funcional sobre base **nueva** en carpeta temporal (`target/tmp/hogar_mejoras`) | **Ejecutada y exitosa — 19/19**: `user_version=2`, tabla `auditoria`, `"1:50"` guardada como `110`, desglose EFECTIVO 10.00 / TARJETA 12.50 sin la anulada, historial de 3 boletos, métricas (2 salas / 1 activa, 2 boletos hoy, S/ 22.50), auditoría registrando y listando |
| Base real `gestionCine.db` | **No se tocó**: la prueba usó `-Duser.home` apuntando a `target/tmp/hogar_mejoras` |
| Interfaz JavaFX (clics reales) | **No ejecutada**: requiere abrir la ventana en la PC y hacer clic; los FXML y sus acciones se revisaron por código |

### 13.3 Limitaciones

- `PRAGMA user_version` informa la versión, pero las migraciones siguen siendo
  idempotentes (se pueden correr siempre) en vez de saltarse por número.
- La auditoría crece sin límite: la pantalla muestra los últimos 300 movimientos.
- Restaurar un respaldo reemplaza **toda** la base; por eso pide confirmación y
  guarda antes una copia del estado actual.
- El CSV se genera para el rango que está en pantalla; si se cambian las fechas
  hay que volver a GENERAR antes de exportar.
- Las pruebas JUnit cubren `Duracion` y `Seguridad` (lógica pura). Los DAO se
  probaron con la prueba funcional, no con JUnit.

---

## 14. Ideas nuevas más pequeñas (en el radar, **no implementadas**)

Quedan anotadas para otra etapa; ninguna se agregó en esta entrega:

1. **Tarifas por tipo de sala o día** (por ejemplo 2D S/ 10 y 3D S/ 15):
   el precio dejaría de escribirse a mano en cada función.
2. **Reporte de películas más vendidas** y **% de ocupación por función**:
   dos consultas de agregación sobre `venta`.
3. **Cancelar una función** y avisar a los compradores (marcar sus boletos).
4. **Límite de intentos de login** y **caducidad de la sesión** por inactividad.
5. **Comprobante en PDF** o impresión directa, en vez del `.txt` actual.
6. **Atajos de teclado** (F5 actualizar, Ctrl+B buscar, Enter vender).
7. **Butacas preferenciales / para discapacidad** y pasillo en el mapa.
8. **Filtro por empleado** en la pantalla de ventas y en el cierre de caja.
9. **Respaldo automático al cerrar** el programa (hoy se hace al abrir).
10. **Modo demostración** con datos de ejemplo para probar sin la base real.

---

## 15. Corrección de auditoría del login y del combo de funciones (26/11/2026)

### 15.1 La auditoría ya guarda el usuario real del login

- `controller/LoginController.java`: la sesión se inicia con
  `Sesion.iniciar(usuario)` **antes** de llamar a `Bitacora.registrar("LOGIN", ...)`.
  Antes se escribía primero en la bitácora y `Sesion` todavía estaba vacía, así
  que `Bitacora` guardaba el usuario `"sistema"` en un ingreso correcto.
- `abrirPrincipal(...)` ya no vuelve a iniciar la sesión: solo se comenta que
  quedó iniciada en `handleLogin`.
- El intento fallido sigue registrándose como `"sistema"` (todavía no hay
  usuario válido) y en el detalle queda el nombre escrito, que es lo útil.

### 15.2 En Ventas aparecen todas las funciones de la película

Antes `VentaFormController.cargarFunciones()` **escondía** las funciones que ya
habían empezado y las de salas inactivas. Si eran las únicas, el combo de
funciones quedaba vacío y parecía que la película no tenía funciones.

Ahora:

- El combo lista **todas** las funciones de la película elegida.
- El texto de cada función avisa cuando corresponde: `(ya empezó)` o
  `(sala inactiva)`, con un `StringConverter` en `cboFuncion`.
- Al elegir una función no vendible se muestra el motivo en el mensaje del
  formulario, el botón SIGUIENTE queda deshabilitado y `validate()`/`handleVender`
  la rechazan con un aviso claro.
- El aviso del combo es **condicional**, según lo que realmente tenga la
  película:
  - si tiene funciones pero **todas son de fechas pasadas**: "Esa película no
    tiene funciones de hoy en adelante. Cree una nueva en el menú Funciones.";
  - si no tiene ninguna función: "Esa película todavía no tiene funciones.
    Cree una en el menú Funciones."
  La diferencia se decide en `cargarFunciones()` con la bandera `tieneFunciones`,
  que cuenta todas las funciones de la película en el mismo recorrido, antes de
  aplicar el filtro de fecha. Antes el mensaje decía siempre "no tiene
  funciones", aunque en realidad existieran solo funciones viejas.
- El aviso (`lblMensaje`) se movió fuera de `panelPaso3` —que está oculto hasta
  avanzar— a la cabecera de `venta-form.fxml`, debajo del separador. Así el
  mensaje **se ve desde el paso 1**: antes el texto se asignaba pero quedaba
  dentro del panel invisible, así que al elegir película o función no aparecía
  nada en pantalla.
- `SalaFormController`: una sala nueva se crea con estado **Activa** por defecto
  (antes el combo quedaba sin elegir y una sala podía quedar Inactiva sin querer).

### 15.3 Verificación

| Prueba | Resultado |
|---|---|
| `mvnw compile` | **Ejecutada y exitosa** (`COMPILE_EXIT=0`) |
| `mvnw test` | **Ejecutada y exitosa** (`TEST_EXIT=0`): `DuracionTest` 5 y `SeguridadTest` 3, 0 fallos |
| Registrar login con sesión iniciada | Revisado por código: `Sesion.iniciar` va antes de `Bitacora.registrar`, así que el `usuario` guardado es el real |
| Interfaz (abrir Venta y desplegar funciones) | **No ejecutada** con clics reales: requiere abrir la ventana en la PC |

### 15.4 Ajuste del combo de funciones (26/11/2026)

- El combo de Ventas ahora lista solo funciones **de hoy en adelante**
  (`esDeHoyOEnAdelante`): las de fechas pasadas ya no aparecen. Las de hoy que
  ya empezaron siguen visibles, marcadas con `(ya empezó)`, para que se entienda
  por qué no se pueden vender.
- `lblPrecio` vuelve a `Precio: -` en cada cambio de función
  (`cargarButacas`), así no queda el precio de la función anterior cuando la
  elegida no se puede vender.
- Verificación: `mvnw compile` exit 0 y `mvnw test` exit 0. La interfaz no se
  abrió con clics reales.

---

## 16. Segunda etapa: cierre de caja, seguridad y calidad (26/11/2026)

Se implementaron las 15 mejoras de la propuesta «Próximas mejoras para el
sistema de cine (priorizadas)», en el orden en que estaban planteadas.

### 16.1 Nivel 1 — dinero y datos

1. **Cierre de caja con arqueo** (`modelo/CierreCaja`, `ICierreCajaDao`,
   `CierreCajaDaoImpl`, `controller/CierreCajaController`,
   `views/cierre-caja.fxml`). Tabla `cierre_caja` (`UNIQUE(fecha)`): monto
   inicial, cobrado/devuelto por forma de pago, total esperado, contado,
   diferencia, usuario y observación. `calcularDia` separa boletos activos de
   anulados; el esperado de efectivo es `inicial + ventas - devoluciones`.
   Se abre desde Reportes con el botón **CERRAR CAJA** y se puede corregir el
   mismo día volviendo a guardar.
2. **Pruebas de integración de DAOs** (`IntegracionDaoTest`, 8 pruebas) sobre
   una base temporal con `@TempDir` y `user.home` cambiado: venta en
   transacción, butaca repetida (el índice único parcial la rechaza), anulación
   por función, cobrado/devuelto del arqueo, guardado del cierre, tarifas y
   sal del admin. Nunca tocan la base real.
3. **Reportes con gráficos** (`modelo/Estadistica`, `IEstadisticaDao`,
   `EstadisticaDaoImpl`, `reporte-view.fxml`): ventas por día del rango y top 5
   de películas con `BarChart` nativo, más `.chart` en `style.css` para que
   sigan el tema oscuro. `ocupacionPorFuncion` queda disponible en el DAO.
4. **Tarifas por tipo de sala y día** (`modelo/Tarifa`, `ITarifaDao`,
   `TarifaDaoImpl`): tabla `tarifa` (`UNIQUE(tipo_sala, dia_semana)`, 0 = base,
   1..7 = lunes..domingo) sembrada con 2D 10, 3D 15, XD 18 y VIP 25.
   `FuncionFormController` propone el precio al elegir sala o fecha y respeta
   el precio guardado al editar.

### 16.2 Nivel 2 — seguridad y operación

5. **PBKDF2 con sal** (`Seguridad.pbkdf2/nuevaSal/verificar/esClaveGuardada`,
   `UsuarioDaoImpl`, `ConexionDB`): columnas `usuario.sal` e
   `usuario.iteraciones`; 120 000 iteraciones, 256 bits, Base64. El login
   acepta el SHA-256 viejo y lo migra en el mismo ingreso. El admin inicial ya
   se crea con sal.
6. **Sesión**: el botón «Cerrar sesión» ya existía; se agregaron **límite de 5
   intentos fallidos** con bloqueo de 60 s (`LoginController`, auditado como
   `LOGIN BLOQUEADO`) y **cierre por inactividad de 15 minutos**
   (`MainController`, aviso + `Bitacora`).
7. **Cancelar función** (`Funcion.estado`, `IFuncionDao.cancelar`,
   `IVentaDao.anularPorFuncion`, `FuncionController`): estado `cancelada`,
   anulación transaccional de sus boletos activos y aviso con la cantidad;
   las funciones canceladas no se ofrecen en Ventas y no aparecen en la
   cartelera.
8. **Ventas y cierre por empleado** (`VentaController`, `venta-view.fxml`):
   combo de empleados alimentado con `venta.id_usuario` + JOIN a `usuario`
   (campo `empleado` del modelo ya existente).
9. **Comprobante reimprimible** (`utils/Comprobante`,
   `IVentaDao.listarPorCodigo`, botón **REIMPRIMIR**): reconstruye la compra
   desde la base y la guarda como `.txt` en
   `SistemaCineUPeU/comprobantes`, listo para abrir con el Bloc de notas e
   imprimir. La venta nueva también deja el archivo. Solo usa `File`/`Files`,
   como se vio en el curso (sin HTML).

### 16.3 Nivel 3 — calidad y experiencia

10. **Migraciones numeradas** (`utils/Migraciones`): pasos `aV3`… con
    `PRAGMA user_version`; `ConexionDB` ya no mezcla migraciones en
    `initDatabase`. La versión 3 agrega `funcion.estado`, `usuario.sal`,
    `usuario.iteraciones`, `tarifa` y `cierre_caja`.
11. **Log de errores** (`utils/Log`): `logs/error.log`, manejador global
    instalado en `HelloApplication` con alerta genérica; usado en login, menú,
    vista, comprobante y migraciones.
12. **Atajos de teclado** (`MainController`): `Ctrl+F1` Inicio, `Ctrl+F2` Ventas,
    `Ctrl+F3` Funciones, `Ctrl+F4` Reportes (los permisos se siguen validando).
13. **Hora de fin y cartelera** (`Funcion.getHoraFin`, columna «Termina» en
    Funciones, botón **CARTELERA** en Ventas): suma la duración normalizada al
    horario y lista las funciones activas del día con su hora de fin.
14. **Validadores compartidos** (`utils/Validaciones` + `ValidacionesTest`):
    DNI, email, teléfono, precio, duración y hora; usados en el formulario de
    venta.
15. **Modo demostración** (`utils/DatosDemo`, botón en el login): cambia
    `user.home` a `SistemaCineUPeU_demo`, crea ahí el esquema y carga 2 salas
    con 64 butacas, 2 películas, 3 funciones, 2 clientes y 3 boletos; entra
    como `admin/admin123`. La base real no se toca.

### 16.4 Verificación

| Prueba | Resultado |
|---|---|
| `mvnw compile` | **exit 0** |
| `mvnw test` | **exit 0**: 32 pruebas (`IntegracionDaoTest` 9, `ValidacionesTest` 6, `SeguridadPbkdf2Test` 4, `SeguridadTest` 3, `DuracionTest` 5, `FuncionTest` 3, `ComprobanteTest` 2), 0 fallos |
| Migración sobre copia de la base real | `Esquema migrado de la version 2 a la 3` en `target/tmp/hogar_ui` |
| Carga de las 20 vistas FXML sobre la copia | **20 de 20 OK** |
| Modo demostración en carpeta temporal | 2 películas, 2 salas, 64 butacas, 3 funciones, 3 ventas, 2 clientes, `user_version = 3`, login `admin` OK |
| Capturas de Cierre de caja y Reportes | Revisadas visualmente (tema oscuro y gráficos con datos) |
| Interacción con clics reales (botones, formularios) | **No ejecutada**: la revisión fue por carga de vistas y pruebas de DAO |

### 16.5 Notas y limitaciones

- `sql/esquema.sql` y `sql/migracion.sql` **no se modificaron** (no se pidió):
  una base creada con esos archivos queda en versión 2 y el sistema la migra
  sola a la 3 al abrir.
- La duración y los permisos existentes se respetan: el empleado sigue viendo
  solo Ventas/Butacas y la cancelación de funciones es solo del admin.
- El bloqueo por intentos y la inactividad se guardan en memoria del proceso;
  al reiniciar el programa se limpian (suficiente para una PC de boletería).
- **Todos los modelos usan propiedades de JavaFX** (`StringProperty`,
  `IntegerProperty`, `DoubleProperty`) con `getX/setX` y `xProperty()`, tal como
  `Cliente`, `Pelicula` o `Venta`. Se completaron `CierreCaja`, `Tarifa`,
  `Estadistica`, `ResumenPago`, `Metrica` y `Reporte`; el neto de
  `ResumenPago` es una propiedad calculada (`Bindings`). `Historial` sigue
  vacío porque es código muerto justificado (no tiene campos).
