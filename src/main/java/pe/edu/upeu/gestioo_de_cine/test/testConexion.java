package pe.edu.upeu.gestioo_de_cine.test;

import pe.edu.upeu.gestioo_de_cine.utils.ConexionDB;

import java.sql.Connection;

public class testConexion {
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
