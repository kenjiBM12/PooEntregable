package pe.edu.upeu.gestioo_de_cine.test;

import pe.edu.upeu.gestioo_de_cine.implementacion.ClienteDaoImpl;
import pe.edu.upeu.gestioo_de_cine.interfaces.IClienteDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Cliente;

import java.util.List;

public class TestCliente {
    static IClienteDao cl = new ClienteDaoImpl();

    public static void listar() {
        List<Cliente> lista = cl.listAll();
        if (lista != null && !lista.isEmpty()) {
            for (Cliente cliente : lista) {
                System.out.println("ID_CLIENTE:" + cliente.getIdcliente());
                System.out.println("NOMBRE:" + cliente.getNombre());
                System.out.println("APELLIDOS:" + cliente.getApellidos());
                System.out.println("DNI:" + cliente.getDni());
                System.out.println("EMAIL:" + cliente.getEmail());
                System.out.println("GENERO:" + cliente.getGenero());
            }
        } else {
            System.out.println("no hay registros");
        }
    }
    public static void insertar(){
        Cliente c = new Cliente();
        c.setNombre("Jhan ");
        c.setApellidos("S T");
        c.setDni("1111111");
        c.setEmail("jhan@gmail.com");
        c.setGenero("M");
        boolean result = cl.insert(c);
        if (result){
            System.out.println("insert success");
        }else{
            System.out.println("insert error");
        }
    }

    public static void eliminar(){
        Cliente c = new Cliente();
        c.setIdcliente(2);
        boolean result = cl.delete(c);
        if (result){
            System.out.println("delete success");
        }else{
            System.out.println("delete error");
        }
    }
    private static void mostrarRutaBD() {
        try (var cn = pe.edu.upeu.gestioo_de_cine.utils.ConexionDB.getConnection();
        var st = cn.createStatement();
        var rs = st.executeQuery("PRAGMA database_list")) {

            while (rs.next()) {
                System.out.println("Base de datos usada: " + rs.getString("file"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("************");
        mostrarRutaBD();
        insertar();
    }

}
