// package organiza las clases; no es la ruta del archivo SQLite.
package com.example.proyecto_final.utils;

// import permite utilizar clases de otros paquetes sin escribir su nombre completo.
import java.sql.*;

/**
 * CONEXIÓN JDBC: abre o reutiliza el acceso a tu archivo SQLite.
 * Mantiene el mismo patrón de conexión que el hotel.
 * Conectar no crea tablas ni registra películas, salas, funciones o ventas.
 */
public class ConexionDB {

    private static Connection connection;

    /**
     * Devuelve una conexión abierta al archivo cine.db.
     * Si un DAO ya cerró la conexión anterior, abre otra antes de devolverla.
     */
    public static Connection getConexion() {
        try {
            if (connection == null || connection.isClosed()) {
                // Carga el controlador JDBC que proporciona la dependencia sqlite-jdbc.
                Class.forName("org.sqlite.JDBC");

                // URL JDBC: jdbc:sqlite: indica el motor; el resto identifica tu archivo de base de datos.
                String url = "jdbc:sqlite:C:/poo/sqilte1U2/db/cine.db";

                connection = DriverManager.getConnection(url);
                // Cada sentencia se confirma automáticamente; no agrupa varias sentencias en una transacción.
                connection.setAutoCommit(true);

                try (Statement stmt = connection.createStatement()) {
                    // Activa las claves foráneas que estén declaradas en SQLite; no crea relaciones por sí solo.
                    stmt.execute("PRAGMA foreign_keys = ON");
                }

                System.out.println("Conexion success");
            }

            return connection;

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error al conectar a la BD: " + e.getMessage(), e
            );
        }
    }
}
