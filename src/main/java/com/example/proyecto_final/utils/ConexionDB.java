package com.example.proyecto_final.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    private static final String URL = "jdbc:sqlite:C:/poo/sqilte1U2/db/Cine.db";

    public static Connection getConexion() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
            System.out.println("¡Conexión exitosa a la base de datos SQLite!");
        } catch (SQLException e) {
            System.err.println("Error de conexión a la Base de Datos: " + e.getMessage());
        }
        return conn;
    }
}