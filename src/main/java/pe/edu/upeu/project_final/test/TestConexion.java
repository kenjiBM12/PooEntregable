package pe.edu.upeu.project_final.test;

import pe.edu.upeu.project_final.utils.ConexionDB;

import java.sql.*;

public class TestConexion {

    public static void testConexion(){
        try{
            Connection connection = ConexionDB.getConnection();
            if (connection !=null && !connection.isClosed()){
                System.out.println("conexion sucess a SQLite!!!");
            }else{
                System.out.println("error conexion");
            }

        } catch (Exception e) {
            System.out.println("error de conexion"+e.getMessage());
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        testConexion();
    }
}
