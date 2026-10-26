package com.example.proyecto_final.utils;

import java.sql.*;

public class ConexionDB {
    private static Connection connection;

    public static Connection getConnection(){
        try{
            if (connection == null || connection.isClosed()){
                Class.forName("org.sqlite.JDBC");
                String url ="jdbc:sqlite:C:/poo/sqilte1U2/db/Cine.db";
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
