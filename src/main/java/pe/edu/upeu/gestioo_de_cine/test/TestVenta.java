package pe.edu.upeu.gestioo_de_cine.test;

import pe.edu.upeu.gestioo_de_cine.implementacion.FuncionDaoImpl;
import pe.edu.upeu.gestioo_de_cine.implementacion.VentaDaoImpl;
import pe.edu.upeu.gestioo_de_cine.interfaces.IFuncionDao;
import pe.edu.upeu.gestioo_de_cine.interfaces.IVentaDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Funcion;
import pe.edu.upeu.gestioo_de_cine.modelo.Venta;

import java.time.LocalDate;
import java.util.List;

public class TestVenta {
    static IVentaDao ve = new VentaDaoImpl();
    static IFuncionDao fu = new FuncionDaoImpl();

    public static void listar() {
        List<Venta> lista = ve.listAll();
        if (lista != null && !lista.isEmpty()) {
            for (Venta v : lista) {
                System.out.println("ID_VENTA: " + v.getIdventa());
                System.out.println("Pelicula: " + v.getFuncion().getPelicula().getTitulo());
                System.out.println("Horario: " + v.getFuncion().getHorario());
                System.out.println("Cliente: " + v.getCliente());
                System.out.println("Asientos: " + v.getCantidadAsientos());
                System.out.println("Total: " + v.getTotalPagar());
                System.out.println("Fecha: " + v.getFechaVenta());
            }
        } else {
            System.out.println("no hay registros");
        }
    }

    public static void insertar() {
        List<Funcion> funciones = fu.listAll();
        if (funciones.isEmpty()) {
            System.out.println("Registra primero una funcion");
            return;
        }
        Funcion f = funciones.get(0);
        Venta v = new Venta();
        v.setFuncion(f);
        v.setCliente("Jhan S T");
        v.setCantidadAsientos(2);
        v.setTotalPagar(2 * f.getPrecio());
        v.setFechaVenta(LocalDate.now());
        boolean result = ve.insert(v);
        if (result) {
            System.out.println("insert success");
        } else {
            System.out.println("insert error");
        }
    }

    public static void main(String[] args) {
        System.out.println("************");
        insertar();
        System.out.println("\n**********");
        listar();
    }
}
