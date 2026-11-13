package pe.edu.upeu.gestioo_de_cine.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

    public class ConexionDB {
        private static Connection connection;

        public static Connection getConnection(){
            try{
                if (connection == null || connection.isClosed()){
                    Class.forName("org.sqlite.JDBC");
                    String url ="jdbc:sqlite:C:/sqlite/db/gestionCine1.db";
                    connection = DriverManager.getConnection(url);
                    connection.setAutoCommit(true);
                    try(Statement stmt = connection.createStatement()){
                        stmt.execute("PRAGMA foreign_keys = ON");
                    }
                    System.out.println("Conexion success");
                }
                return connection;

            } catch (Exception e) {
                throw new RuntimeException("Error al conectar a la BD" +e.getMessage());
            }

        }

    }

