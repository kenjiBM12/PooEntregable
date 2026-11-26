package pe.edu.upeu.gestioo_de_cine.utils;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

/**
 * MEJORA 15: modo demostración.
 *
 * El sistema trabaja siempre sobre la carpeta del usuario
 * (System.getProperty("user.home")). Para demostrar sin riesgo, esta clase
 * cambia esa propiedad a una carpeta aparte (SistemaCineUPeU_demo), deja que
 * ConexionDB cree ahi el esquema completo y carga datos de ejemplo.
 *
 * La base real no se toca: cuando se cierra el programa, la propiedad vuelve
 * a su valor normal (el proceso termina).
 */
public class DatosDemo {

    public static final String CARPETA_DEMO = "SistemaCineUPeU_demo";

    /**
     * Prepara la carpeta demo y sus datos. Devuelve la carpeta usada.
     * Se puede llamar varias veces: si ya hay datos, no duplica nada.
     */
    public static File preparar() {
        File actual = new File(System.getProperty("user.home"));
        File demo = CARPETA_DEMO.equals(actual.getName())
                ? actual : new File(actual, CARPETA_DEMO);

        //se cierra la base que estuviera abierta antes de cambiar de carpeta
        ConexionDB.cerrar();
        System.setProperty("user.home", demo.getAbsolutePath());

        Connection cn = ConexionDB.getConnection();
        sembrar(cn);
        return demo;
    }

    /** Carga peliculas, salas con butacas, funciones y ventas de ejemplo. */
    private static void sembrar(Connection cn) {
        try {
            try (Statement st = cn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM pelicula")) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return;
                }
            }

            String hoy = LocalDate.now().toString();
            String manana = LocalDate.now().plusDays(1).toString();

            //1. dos salas con su cuadricula de butacas
            int sala1 = insertarSala(cn, 1, "2D", 5, 8);
            int sala2 = insertarSala(cn, 2, "3D", 4, 6);

            //2. dos peliculas (duracion en minutos, como pide el sistema)
            int peli1 = insertar(cn,
                    "INSERT INTO pelicula (numero, titulo, duracion, categoria, clasificacion) "
                            + "VALUES (1,'Interestelar','169','Ciencia ficción','+14')");
            int peli2 = insertar(cn,
                    "INSERT INTO pelicula (numero, titulo, duracion, categoria, clasificacion) "
                            + "VALUES (2,'Coco','105','Animación','APT')");

            //3. funciones: dos de hoy y una de mañana
            int funcion1 = insertarFuncion(cn, peli1, sala1, hoy, "15:00", 12.5);
            int funcion2 = insertarFuncion(cn, peli2, sala2, hoy, "18:30", 15.0);
            insertarFuncion(cn, peli1, sala2, manana, "20:00", 15.0);

            //4. dos clientes
            int cliente1 = insertar(cn,
                    "INSERT INTO cliente (nombre, apellidos, dni, email, genero, telefono) "
                            + "VALUES ('Ana','Quispe','70000001','ana@correo.com','Femenino','987111222')");
            int cliente2 = insertar(cn,
                    "INSERT INTO cliente (nombre, apellidos, dni, email, genero, telefono) "
                            + "VALUES ('Luis','Ramos','70000002','luis@correo.com','Masculino','987333444')");

            //5. dos compras de ejemplo (una por cada medio de pago)
            insertarVenta(cn, funcion1, cliente1, "Ana Quispe", 12.5, hoy, butacaId(cn, sala1, 1),
                    "C-9001", "EFECTIVO");
            insertarVenta(cn, funcion1, cliente1, "Ana Quispe", 12.5, hoy, butacaId(cn, sala1, 2),
                    "C-9001", "EFECTIVO");
            insertarVenta(cn, funcion2, cliente2, "Luis Ramos", 15.0, hoy, butacaId(cn, sala2, 1),
                    "C-9002", "TARJETA");

            System.out.println("Datos de demostracion cargados en " + ConexionDB.archivoBase());
        } catch (SQLException e) {
            throw new RuntimeException("No se pudieron cargar los datos de demostración: "
                    + e.getMessage(), e);
        }
    }

    private static int insertarSala(Connection cn, int numero, String tipo, int filas, int columnas)
            throws SQLException {
        int id = insertar(cn, "INSERT INTO sala (numero, tipo_sala, capacidad, estado, filas, columnas) "
                + "VALUES (?,?,?,'ACTIVA',?,?)", numero, tipo, filas * columnas, filas, columnas);
        for (int fila = 1; fila <= filas; fila++) {
            for (int columna = 1; columna <= columnas; columna++) {
                insertar(cn, "INSERT INTO butaca (id_sala, fila, numero) VALUES (?,?,?)",
                        id, fila, columna);
            }
        }
        return id;
    }

    private static int insertarFuncion(Connection cn, int idPelicula, int idSala, String fecha,
                                       String horario, double precio) throws SQLException {
        return insertar(cn, "INSERT INTO funcion (id_pelicula, id_sala, fecha, horario, precio, estado) "
                + "VALUES (?,?,?,?,?,'activa')", idPelicula, idSala, fecha, horario, precio);
    }

    private static void insertarVenta(Connection cn, int idFuncion, int idCliente, String cliente,
                                      double total, String fecha, int idButaca, String codigo,
                                      String metodo) throws SQLException {
        insertar(cn, "INSERT INTO venta (id_funcion, id_cliente, cliente, cantidad_asientos, "
                        + "total_pagar, fecha_venta, id_butaca, estado, id_usuario, codigo_compra, "
                        + "metodo_pago) VALUES (?,?,?,1,?,?,?,'activa',1,?,?)",
                idFuncion, idCliente, cliente, total, fecha, idButaca, codigo, metodo);
    }

    /** Id de la butaca numero 1..n de una sala (para las ventas de ejemplo). */
    private static int butacaId(Connection cn, int idSala, int orden) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(
                "SELECT idbutaca FROM butaca WHERE id_sala=? ORDER BY fila, numero LIMIT 1 OFFSET ?")) {
            ps.setInt(1, idSala);
            ps.setInt(2, orden - 1);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    private static int insertar(Connection cn, String sql, Object... datos) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < datos.length; i++) {
                ps.setObject(i + 1, datos[i]);
            }
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                return claves.next() ? claves.getInt(1) : 0;
            }
        }
    }
}
