package pe.edu.upeu.gestioo_de_cine.utils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionDB {

    private static Connection connection;

    private static final String CARPETA = "SistemaCineUPeU";
    private static final String ARCHIVO_BD = "gestionCine.db";

    public static File archivoBase() {
        String userHome = System.getProperty("user.home");
        File directory = new File(userHome + File.separator + CARPETA);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return new File(directory, ARCHIVO_BD);
    }

    public static File carpetaSistema() {
        File carpeta = archivoBase().getParentFile();
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }
        return carpeta;
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {

                File archivo = archivoBase();

                String url = "jdbc:sqlite:" + archivo.getAbsolutePath();
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(url);
                connection.setAutoCommit(true);

                try (Statement stmt = connection.createStatement()) {
                    stmt.execute("PRAGMA foreign_keys = ON");
                    stmt.execute("PRAGMA busy_timeout = 8000");
                }

                initDatabase();
                System.out.println("Conexion success");
            }
            return connection;

        } catch (Exception e) {
            throw new RuntimeException("Error al conectar a la BD" + e.getMessage());
        }
    }

    private static void initDatabase() {

        String sqlPelicula = "CREATE TABLE IF NOT EXISTS pelicula (" +
                "idpelicula INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "numero INTEGER, " +
                "titulo TEXT, " +
                "duracion TEXT, " +
                "categoria TEXT, " +
                "clasificacion TEXT);";

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

            ejecutarSeguro(stmt, "CREATE UNIQUE INDEX IF NOT EXISTS ux_pelicula_numero "
                    + "ON pelicula(numero)");
            ejecutarSeguro(stmt, "CREATE UNIQUE INDEX IF NOT EXISTS ux_butaca_sala_fila_numero "
                    + "ON butaca (id_sala, fila, numero)");
            ejecutarSeguro(stmt, "CREATE INDEX IF NOT EXISTS ix_venta_funcion_butaca "
                    + "ON venta (id_funcion, id_butaca)");
            ejecutarSeguro(stmt, "CREATE INDEX IF NOT EXISTS ix_venta_codigo_compra "
                    + "ON venta (codigo_compra)");

            ejecutarSeguro(stmt, "CREATE UNIQUE INDEX IF NOT EXISTS ux_venta_funcion_butaca_activa "
                    + "ON venta (id_funcion, id_butaca) WHERE estado = 'activa'");

            ejecutarSeguro(stmt, "CREATE TRIGGER IF NOT EXISTS trg_venta_funcion_valida "
                    + "BEFORE INSERT ON venta FOR EACH ROW "
                    + "WHEN (SELECT COUNT(*) FROM funcion WHERE idfuncion = NEW.id_funcion) = 0 "
                    + "BEGIN SELECT RAISE(ABORT, 'La funcion de la venta no existe'); END");
            ejecutarSeguro(stmt, "CREATE TRIGGER IF NOT EXISTS trg_venta_butaca_valida "
                    + "BEFORE INSERT ON venta FOR EACH ROW "
                    + "WHEN NEW.id_butaca IS NOT NULL AND "
                    + "(SELECT COUNT(*) FROM butaca WHERE idbutaca = NEW.id_butaca) = 0 "
                    + "BEGIN SELECT RAISE(ABORT, 'La butaca de la venta no existe'); END");

            agregarColumna(stmt, "sala", "filas", "INTEGER NOT NULL DEFAULT 0");
            agregarColumna(stmt, "sala", "columnas", "INTEGER NOT NULL DEFAULT 0");

            agregarColumna(stmt, "venta", "codigo_compra", "TEXT");
            agregarColumna(stmt, "venta", "id_cliente", "INTEGER");
            agregarColumna(stmt, "venta", "id_usuario", "INTEGER");
            agregarColumna(stmt, "venta", "estado", "TEXT DEFAULT 'activa'");
            agregarColumna(stmt, "venta", "metodo_pago", "TEXT DEFAULT 'EFECTIVO'");
            agregarColumna(stmt, "venta", "anulada_por", "INTEGER");
            agregarColumna(stmt, "venta", "fecha_anulacion", "TEXT");
            agregarColumna(stmt, "venta", "motivo_anulacion", "TEXT");

            agregarColumna(stmt, "funcion", "estado", "TEXT DEFAULT 'activa'");

            agregarColumna(stmt, "usuario", "sal", "TEXT");
            agregarColumna(stmt, "usuario", "iteraciones", "INTEGER DEFAULT 120000");

            int totalUsuarios = 0;
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM usuario")) {
                if (rs.next()) {
                    totalUsuarios = rs.getInt(1);
                }
            }
            if (totalUsuarios == 0) {
                stmt.execute("INSERT INTO usuario (nombre, usuario, contrasena, rol, activo) "
                        + "VALUES ('Administrador', 'admin', 'admin123', 'admin', 1)");
                System.out.println("Usuario inicial 'admin' creado.");
            }

            convertirClavesAntiguas(stmt);

        } catch (SQLException e) {
            System.out.println("Error al inicializar tablas: " + e.getMessage());
        }
    }

    private static void convertirClavesAntiguas(Statement stmt) {
        String sql = "UPDATE usuario SET contrasena='admin123', sal=NULL "
                + "WHERE sal IS NOT NULL "
                + "OR (length(contrasena)=64 "
                + "    AND lower(contrasena) NOT GLOB '*[^0-9a-f]*')";
        try {
            int cambiados = stmt.executeUpdate(sql);
            if (cambiados > 0) {
                System.out.println("Claves antiguas cambiadas a 'admin123': " + cambiados);
            }
        } catch (SQLException e) {
            System.out.println("No se pudieron convertir las claves antiguas: " + e.getMessage());
        }
    }

    private static void ejecutarSeguro(Statement stmt, String sql) {
        try {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Aviso: no se pudo ejecutar [" + sql + "]: " + e.getMessage());
        }
    }

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
