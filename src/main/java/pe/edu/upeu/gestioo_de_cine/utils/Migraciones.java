package pe.edu.upeu.gestioo_de_cine.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * MEJORA 10: migraciones numeradas del esquema.
 *
 * Antes todo el crecimiento de la base estaba mezclado dentro de
 * ConexionDB.initDatabase(). Ahora cada cambio de estructura vive en un paso
 * numerado (aV3, aV4...) y se aplica solo si la base todavia no lo tiene
 * (PRAGMA user_version).
 *
 * Reglas:
 *  - Un paso NUNCA se modifica despues de publicado: si hay que cambiar algo,
 *    se agrega el paso siguiente.
 *  - Cada paso debe poder ejecutarse dos veces sin romper nada (IF NOT EXISTS
 *    o revisar la columna antes de agregarla).
 *  - Al final se escribe la version nueva en el archivo .db.
 *
 * Versiones:
 *  1: esquema original del sistema de cine.
 *  2: duraciones en minutos y tabla de auditoria.
 *  3: estado de la funcion, tarifas, cierre de caja y datos para PBKDF2.
 */
public class Migraciones {

    //version que espera este codigo (ConexionDB la usa al abrir la base)
    public static final int VERSION_ACTUAL = 3;

    /**
     * Aplica todos los pasos que le faltan a la base y guarda la version final.
     * Si la base es nueva, las tablas base ya las creo ConexionDB y los pasos
     * se encargan de lo agregado despues de la version 2.
     */
    public static void aplicar(Connection cn) {
        int version = leerVersion(cn);
        if (version < 3) {
            aV3(cn);
        }
        escribirVersion(cn, VERSION_ACTUAL);
        if (version < VERSION_ACTUAL) {
            System.out.println("Esquema migrado de la version " + version
                    + " a la " + VERSION_ACTUAL);
        }
    }

    public static int leerVersion(Connection cn) {
        try (Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery("PRAGMA user_version")) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            Log.error("No se pudo leer la version del esquema", e);
        }
        return 0;
    }

    private static void escribirVersion(Connection cn, int version) {
        try (Statement st = cn.createStatement()) {
            st.execute("PRAGMA user_version = " + version);
        } catch (SQLException e) {
            Log.error("No se pudo guardar la version del esquema", e);
        }
    }

    /**
     * Version 3:
     *  - funcion.estado: 'activa' o 'cancelada' (MEJORA 7: cancelar funcion).
     *  - usuario.sal y usuario.iteraciones: datos de PBKDF2 (MEJORA 5).
     *  - tabla tarifa: precio por tipo de sala y dia (MEJORA 4).
     *  - tabla cierre_caja: arqueo diario (MEJORA 1).
     */
    private static void aV3(Connection cn) {
        asegurarColumna(cn, "funcion", "estado", "TEXT DEFAULT 'activa'");
        asegurarColumna(cn, "usuario", "sal", "TEXT");
        asegurarColumna(cn, "usuario", "iteraciones", "INTEGER DEFAULT 120000");

        ejecutar(cn, "CREATE TABLE IF NOT EXISTS tarifa ("
                + "idtarifa INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "tipo_sala TEXT NOT NULL, "
                //0 = vale para todos los dias; 1..7 = lunes..domingo
                + "dia_semana INTEGER NOT NULL DEFAULT 0, "
                + "precio REAL NOT NULL, "
                + "UNIQUE (tipo_sala, dia_semana))");

        ejecutar(cn, "CREATE TABLE IF NOT EXISTS cierre_caja ("
                + "idcierre INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "fecha TEXT NOT NULL UNIQUE, "
                + "monto_inicial REAL NOT NULL DEFAULT 0, "
                + "efectivo REAL NOT NULL DEFAULT 0, "
                + "tarjeta REAL NOT NULL DEFAULT 0, "
                + "yape REAL NOT NULL DEFAULT 0, "
                + "otros REAL NOT NULL DEFAULT 0, "
                + "devuelto REAL NOT NULL DEFAULT 0, "
                + "total_esperado REAL NOT NULL DEFAULT 0, "
                + "total_contado REAL NOT NULL DEFAULT 0, "
                + "diferencia REAL NOT NULL DEFAULT 0, "
                + "id_usuario INTEGER, "
                + "usuario TEXT, "
                + "observacion TEXT, "
                + "fecha_registro TEXT)");
        ejecutar(cn, "CREATE INDEX IF NOT EXISTS ix_cierre_fecha ON cierre_caja(fecha)");

        sembrarTarifas(cn);
    }

    /**
     * Tarifas iniciales para que el formulario de funciones no quede sin
     * precio. Se guardan con dia_semana = 0 (todos los dias) y el
     * administrador puede cambiarlas despues.
     */
    private static void sembrarTarifas(Connection cn) {
        Object[][] iniciales = {
                {"2D", 10.0}, {"3D", 15.0}, {"XD", 18.0}, {"VIP", 25.0}
        };
        String sql = "INSERT OR IGNORE INTO tarifa (tipo_sala, dia_semana, precio) "
                + "VALUES (?, 0, ?)";
        for (Object[] fila : iniciales) {
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setString(1, (String) fila[0]);
                ps.setDouble(2, (Double) fila[1]);
                ps.executeUpdate();
            } catch (SQLException e) {
                Log.error("No se pudo sembrar la tarifa " + fila[0], e);
            }
        }
    }

    //SQLite no tiene ADD COLUMN IF NOT EXISTS: se mira la tabla antes
    private static void asegurarColumna(Connection cn, String tabla, String columna,
                                        String definicion) {
        boolean existe = false;
        try (Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery("PRAGMA table_info(" + tabla + ")")) {
            while (rs.next()) {
                if (columna.equalsIgnoreCase(rs.getString("name"))) {
                    existe = true;
                }
            }
        } catch (SQLException e) {
            Log.error("No se pudo revisar la tabla " + tabla, e);
            return;
        }
        if (!existe) {
            ejecutar(cn, "ALTER TABLE " + tabla + " ADD COLUMN " + columna + " " + definicion);
            System.out.println("Migracion: columna " + columna + " agregada a " + tabla);
        }
    }

    private static void ejecutar(Connection cn, String sql) {
        try (Statement st = cn.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            //no se corta la migracion completa por una sola sentencia
            System.out.println("Aviso de migracion: " + e.getMessage());
        }
    }
}
