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

                //3. no se copia ninguna base antigua: si el archivo no existe,
                //   SQLite lo crea en blanco y initDatabase() le pone las tablas

                String url = "jdbc:sqlite:" + archivo.getAbsolutePath();
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(url);
                connection.setAutoCommit(true);

                //las llaves foraneas de SQLite vienen apagadas por defecto
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute("PRAGMA foreign_keys = ON");
                    //CORREGIDO: cuando otra ventana del sistema (o una copia vieja
                    //del programa) tiene la base abierta, SQLite responde enseguida
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

    //crea todas las tablas del sistema y el usuario admin inicial
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
                "codigo_compra TEXT);";

        String sqlUsuario = "CREATE TABLE IF NOT EXISTS usuario (" +
                "idusuario INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "usuario TEXT NOT NULL UNIQUE, " +
                "contrasena TEXT NOT NULL, " +
                "rol TEXT NOT NULL DEFAULT 'empleado', " +
                "activo INTEGER NOT NULL DEFAULT 1);";

        //indices para no repetir butacas, numeros de pelicula ni codigos de compra
        String sqlIndices =
                "CREATE UNIQUE INDEX IF NOT EXISTS ux_pelicula_numero ON pelicula(numero);" +
                "CREATE UNIQUE INDEX IF NOT EXISTS ux_butaca_sala_fila_numero " +
                "ON butaca (id_sala, fila, numero);" +
                "CREATE INDEX IF NOT EXISTS ix_venta_funcion_butaca ON venta (id_funcion, id_butaca);" +
                "CREATE INDEX IF NOT EXISTS ix_venta_codigo_compra ON venta (codigo_compra);";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sqlPelicula);
            stmt.execute(sqlSala);
            stmt.execute(sqlCliente);
            stmt.execute(sqlFuncion);
            stmt.execute(sqlButaca);
            stmt.execute(sqlVenta);
            stmt.execute(sqlUsuario);
            stmt.execute(sqlIndices);

            //4. bases creadas antes de la cuadricula: se agregan las columnas que falten
            agregarColumna(stmt, "sala", "filas", "INTEGER NOT NULL DEFAULT 0");
            agregarColumna(stmt, "sala", "columnas", "INTEGER NOT NULL DEFAULT 0");

            //5. INSERTAR USUARIO ADMIN POR DEFECTO (si la tabla esta vacia)
            //esto es vital para poder entrar al sistema la primera vez
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM usuario")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    stmt.execute("INSERT INTO usuario (nombre, usuario, contrasena, rol, activo) " +
                            "VALUES ('Administrador', 'admin', 'admin123', 'admin', 1)");
                    System.out.println("Usuario inicial 'admin' creado.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al inicializar tablas: " + e.getMessage());
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
