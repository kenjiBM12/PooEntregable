// package organiza las clases; no es la ruta del archivo SQLite.
package com.example.proyecto_final.test;

// import permite utilizar clases de otros paquetes sin escribir su nombre completo.
import com.example.proyecto_final.utils.ConexionDB;

import java.sql.*;

/**
 * PRUEBA MANUAL DE CONSOLA: permite ejecutar operaciones sin la interfaz gráfica.
 * Utiliza los mismos modelos, interfaces y DAO que las ventanas.
 * Estas operaciones trabajan con la base real configurada en ConexionDB.
 */
public class TestConexion {

    /**
     * Comprueba que se obtuvo una conexión y que no está cerrada.
     * No comprueba por sí solo que las tablas y sus datos existan.
     */
    public static void testConexion() {
        try {
            Connection connection = ConexionDB.getConexion();

            if (connection != null && !connection.isClosed()) {
                System.out.println("conexion sucess a SQLite!!!");
            } else {
                System.out.println("error conexion");
            }

        } catch (Exception e) {
            System.out.println("error de conexion" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Punto de entrada que ejecuta Java al iniciar esta clase.
     * En los tests permite trabajar por consola; en HelloApplication inicia JavaFX.
     */
    public static void main(String[] args) {
        testConexion();
    }
}
