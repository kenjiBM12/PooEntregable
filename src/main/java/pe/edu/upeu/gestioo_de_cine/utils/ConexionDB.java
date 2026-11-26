package pe.edu.upeu.gestioo_de_cine.utils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionDB {

    private static Connection connection;

    //carpeta y archivo donde se guarda la base de datos
    private static final String CARPETA = "SistemaCineUPeU";
    private static final String ARCHIVO_BD = "gestionCine.db";

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {

                //1. obtener la carpeta de documentos del usuario actual
                String userHome = System.getProperty("user.home");
                File directory = new File(userHome + File.separator + CARPETA);

                //2. si la carpeta no existe, la creamos
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                //la bd se guardara C:\\Users\\Nombre\\SistemaCineUPeU\\gestionCine.db
                File archivo = new File(directory, ARCHIVO_BD);

                //MEJORA 2: antes de abrir la conexion se guarda una copia de la
                //base. Se hace aqui porque el archivo esta cerrado y la copia
                //queda igual a como quedo la ultima vez que se uso el sistema.
                RespaldoDB.respaldar(archivo);

                String url = "jdbc:sqlite:" + archivo.getAbsolutePath();
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(url);
                connection.setAutoCommit(true);

                //las llaves foraneas de SQLite vienen apagadas por defecto
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute("PRAGMA foreign_keys = ON");
                    //cuando otra ventana del sistema (o una copia vieja del
                    //programa) tiene la base abierta, SQLite responde enseguida
                    //"database is locked" y no dejaba guardar ni editar NADA.
                    //Con busy_timeout la conexion espera unos segundos antes de
                    //rendirse, que es lo que pasa cuando dos ventanas escriben a la vez.
                    stmt.execute("PRAGMA busy_timeout = 8000");
                }

                //crear las tablas en la bd
                initDatabase();
                System.out.println("Conexion success");
            }
            return connection;

        } catch (Exception e) {
            throw new RuntimeException("Error al conectar a la BD" + e.getMessage());
        }
    }

    /**
     * Crea todas las tablas del sistema y el usuario admin inicial.
     *
     * MEJORA 1: este metodo y el archivo sql/esquema.sql ahora definen LO MISMO.
     * Antes el esquema usaba "nombre"/"tipo" para pelicula/sala, pero el codigo
     * usa "titulo" y "tipo_sala". Si alguien instalaba el sistema con
     * esquema.sql, la aplicacion fallaba. La version correcta es la del codigo,
     * porque es la que usa la base real, asi que se copio aqui.
     */
    private static void initDatabase() {
        //se usa CREATE TABLE IF NOT EXISTS para que no de error si ya existen

        String sqlPelicula = "CREATE TABLE IF NOT EXISTS pelicula (" +
                "idpelicula INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "numero INTEGER, " +
                "titulo TEXT, " +
                "duracion TEXT, " +
                "categoria TEXT, " +
                "clasificacion TEXT);";

        //filas y columnas son la cuadricula de la sala: de ahi salen sus butacas
        String sqlSala = "CREATE TABLE IF NOT EXISTS sala (" +
                "idsala INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "numero INTEGER NOT NULL UNIQUE, " +
                "tipo_sala TEXT NOT NULL, " +
                "capacidad INTEGER NOT NULL, " +
                "estado TEXT NOT NULL DEFAULT 'ACTIVA', " +
                "filas INTEGER NOT NULL DEFAULT 0, " +
                "columnas INTEGER NOT NULL DEFAULT 0);";

        String sqlCliente = "CREATE TABLE IF NOT EXISTS cliente (" +
                "idcliente INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "apellidos TEXT NOT NULL, " +
                "dni TEXT NOT NULL UNIQUE, " +
                "email TEXT, " +
                "genero TEXT, " +
                "telefono TEXT);";

        String sqlFuncion = "CREATE TABLE IF NOT EXISTS funcion (" +
                "idfuncion INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "numero INTEGER, " +
                "id_pelicula INTEGER NOT NULL, " +
                "id_sala INTEGER NOT NULL, " +
                "fecha TEXT NOT NULL, " +
                "horario TEXT NOT NULL, " +
                "precio REAL NOT NULL, " +
                "FOREIGN KEY (id_pelicula) REFERENCES pelicula(idpelicula), " +
                "FOREIGN KEY (id_sala) REFERENCES sala(idsala));";

        String sqlButaca = "CREATE TABLE IF NOT EXISTS butaca (" +
                "idbutaca INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_sala INTEGER NOT NULL, " +
                "fila INTEGER NOT NULL, " +
                "numero INTEGER NOT NULL, " +
                "FOREIGN KEY (id_sala) REFERENCES sala(idsala));";

        //cada fila de venta es UN boleto, por eso cantidad_asientos vale 1
        //MEJORA 6: metodo_pago y los tres datos de la anulacion (quien, cuando y por que)
        //MEJORA 8: llaves foraneas declaradas para las instalaciones nuevas
        String sqlVenta = "CREATE TABLE IF NOT EXISTS venta (" +
                "idventa INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_funcion INTEGER NOT NULL, " +
                "id_cliente INTEGER, " +
                "cliente TEXT NOT NULL, " +
                "cantidad_asientos INTEGER NOT NULL DEFAULT 1, " +
                "total_pagar REAL NOT NULL DEFAULT 0, " +
                "fecha_venta TEXT, " +
                "id_butaca INTEGER, " +
                "estado TEXT DEFAULT 'activa', " +
                "id_usuario INTEGER, " +
                "codigo_compra TEXT, " +
                "metodo_pago TEXT DEFAULT 'EFECTIVO', " +
                "anulada_por INTEGER, " +
                "fecha_anulacion TEXT, " +
                "motivo_anulacion TEXT, " +
                "FOREIGN KEY (id_funcion) REFERENCES funcion(idfuncion), " +
                "FOREIGN KEY (id_cliente) REFERENCES cliente(idcliente), " +
                "FOREIGN KEY (id_butaca) REFERENCES butaca(idbutaca), " +
                "FOREIGN KEY (id_usuario) REFERENCES usuario(idusuario));";

        String sqlUsuario = "CREATE TABLE IF NOT EXISTS usuario (" +
                "idusuario INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "usuario TEXT NOT NULL UNIQUE, " +
                "contrasena TEXT NOT NULL, " +
                "rol TEXT NOT NULL DEFAULT 'empleado', " +
                "activo INTEGER NOT NULL DEFAULT 1);";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sqlPelicula);
            stmt.execute(sqlSala);
            stmt.execute(sqlCliente);
            stmt.execute(sqlFuncion);
            stmt.execute(sqlButaca);
            stmt.execute(sqlVenta);
            stmt.execute(sqlUsuario);

            //MEJORA 1: antes TODOS los indices iban en un solo execute(), y
            //sqlite-jdbc solo ejecutaba el primero. Ahora cada sentencia va
            //aparte y con su propio try, asi una falla no impide crear las demas.
            ejecutarSeguro(stmt, "CREATE UNIQUE INDEX IF NOT EXISTS ux_pelicula_numero "
                    + "ON pelicula(numero)");
            ejecutarSeguro(stmt, "CREATE UNIQUE INDEX IF NOT EXISTS ux_butaca_sala_fila_numero "
                    + "ON butaca (id_sala, fila, numero)");
            ejecutarSeguro(stmt, "CREATE INDEX IF NOT EXISTS ix_venta_funcion_butaca "
                    + "ON venta (id_funcion, id_butaca)");
            ejecutarSeguro(stmt, "CREATE INDEX IF NOT EXISTS ix_venta_codigo_compra "
                    + "ON venta (codigo_compra)");

            //MEJORA 8: indice UNICO PARCIAL. Solo se aplica a las ventas
            //'activa', asi una butaca anulada se puede volver a vender en la
            //misma funcion. El "WHERE" es lo que lo hace parcial.
            ejecutarSeguro(stmt, "CREATE UNIQUE INDEX IF NOT EXISTS ux_venta_funcion_butaca_activa "
                    + "ON venta (id_funcion, id_butaca) WHERE estado = 'activa'");

            //MEJORA 8: SQLite no permite agregar una llave foranea con ALTER
            //TABLE, asi que en las bases ya creadas se controla con TRIGGERS.
            //Un trigger es codigo que la base ejecuta sola antes de guardar.
            ejecutarSeguro(stmt, "CREATE TRIGGER IF NOT EXISTS trg_venta_funcion_valida "
                    + "BEFORE INSERT ON venta FOR EACH ROW "
                    + "WHEN (SELECT COUNT(*) FROM funcion WHERE idfuncion = NEW.id_funcion) = 0 "
                    + "BEGIN SELECT RAISE(ABORT, 'La funcion de la venta no existe'); END");
            ejecutarSeguro(stmt, "CREATE TRIGGER IF NOT EXISTS trg_venta_butaca_valida "
                    + "BEFORE INSERT ON venta FOR EACH ROW "
                    + "WHEN NEW.id_butaca IS NOT NULL AND "
                    + "(SELECT COUNT(*) FROM butaca WHERE idbutaca = NEW.id_butaca) = 0 "
                    + "BEGIN SELECT RAISE(ABORT, 'La butaca de la venta no existe'); END");

            //4. bases creadas antes de la cuadricula: se agregan las columnas que falten
            agregarColumna(stmt, "sala", "filas", "INTEGER NOT NULL DEFAULT 0");
            agregarColumna(stmt, "sala", "columnas", "INTEGER NOT NULL DEFAULT 0");

            //MEJORA 1: estas columnas se agregaron en versiones anteriores del
            //sistema; en una base vieja faltan y la pantalla de ventas fallaba
            agregarColumna(stmt, "venta", "codigo_compra", "TEXT");
            agregarColumna(stmt, "venta", "id_cliente", "INTEGER");
            agregarColumna(stmt, "venta", "id_usuario", "INTEGER");
            agregarColumna(stmt, "venta", "estado", "TEXT DEFAULT 'activa'");

            //MEJORA 6: metodo de pago y auditoria de la anulacion
            agregarColumna(stmt, "venta", "metodo_pago", "TEXT DEFAULT 'EFECTIVO'");
            agregarColumna(stmt, "venta", "anulada_por", "INTEGER");
            agregarColumna(stmt, "venta", "fecha_anulacion", "TEXT");
            agregarColumna(stmt, "venta", "motivo_anulacion", "TEXT");

            //5. INSERTAR USUARIO ADMIN POR DEFECTO (si la tabla esta vacia)
            //esto es vital para poder entrar al sistema la primera vez
            //MEJORA 7: la contraseña se guarda como hash SHA-256, no en texto plano
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM usuario")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    try (java.sql.PreparedStatement ps = connection.prepareStatement(
                            "INSERT INTO usuario (nombre, usuario, contrasena, rol, activo) "
                                    + "VALUES ('Administrador', 'admin', ?, 'admin', 1)")) {
                        ps.setString(1, Seguridad.hash("admin123"));
                        ps.executeUpdate();
                    }
                    System.out.println("Usuario inicial 'admin' creado.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al inicializar tablas: " + e.getMessage());
        }
    }

    /**
     * Ejecuta una sentencia de estructura (indice o trigger) sin detener las
     * demas si falla. Por ejemplo: si una base vieja ya tiene butacas repetidas,
     * el indice unico no se puede crear; ese error se informa y se sigue con
     * el resto de sentencias.
     */
    private static void ejecutarSeguro(Statement stmt, String sql) {
        try {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Aviso: no se pudo ejecutar [" + sql + "]: " + e.getMessage());
        }
    }

    //SQLite no tiene ADD COLUMN IF NOT EXISTS: se revisa la tabla antes
    private static void agregarColumna(Statement stmt, String tabla, String columna, String definicion) {
        boolean existe = false;
        try (ResultSet rs = stmt.executeQuery("PRAGMA table_info(" + tabla + ")")) {
            while (rs.next()) {
                if (columna.equalsIgnoreCase(rs.getString("name"))) {
                    existe = true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al revisar la tabla " + tabla + ": " + e.getMessage());
            return;
        }
        if (!existe) {
            try {
                stmt.execute("ALTER TABLE " + tabla + " ADD COLUMN " + columna + " " + definicion);
                System.out.println("Columna " + columna + " agregada a " + tabla);
            } catch (SQLException e) {
                System.out.println("Error al agregar la columna " + columna + ": " + e.getMessage());
            }
        }
    }

    //cierra la conexion compartida cuando se sale del programa
    public static void cerrar() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception e) {
            System.out.println("Error al cerrar la conexion:" + e.getMessage());
        }
    }
}
