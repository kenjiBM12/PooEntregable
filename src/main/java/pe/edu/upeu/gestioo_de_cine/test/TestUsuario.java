package pe.edu.upeu.gestioo_de_cine.test;

import pe.edu.upeu.gestioo_de_cine.implementacion.UsuarioDaoImpl;
import pe.edu.upeu.gestioo_de_cine.interfaces.IUsuarioDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Usuario;

public class TestUsuario {
    private static IUsuarioDao dao = new UsuarioDaoImpl();

    public void valid_login(){
        Usuario us = dao.login("admin","admin123");
        if (us !=null){
            System.out.println("ID_USUARIO: "+us.getIdusuario());
            System.out.println("NOMBRE: "+us.getNombre());
            System.out.println("APELLIDOS: "+us.getApellidos());
            System.out.println("DNI: "+us.getDni());
            System.out.println("USER: "+us.getUser());
            System.out.println("TIPO_USER:"+us.getTipoUser());
        }else{
            System.out.println("error de login");
        }
    }

    public static void main(String[] args) {
        TestUsuario t = new TestUsuario();
        t.valid_login();
    }

}
