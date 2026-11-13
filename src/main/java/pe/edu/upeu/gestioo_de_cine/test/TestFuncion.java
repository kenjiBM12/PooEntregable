package pe.edu.upeu.gestioo_de_cine.test;

import pe.edu.upeu.gestioo_de_cine.implementacion.FuncionDaoImpl;
import pe.edu.upeu.gestioo_de_cine.implementacion.PeliculaDaoImpl;
import pe.edu.upeu.gestioo_de_cine.implementacion.SalaDaoImpl;
import pe.edu.upeu.gestioo_de_cine.interfaces.IFuncionDao;
import pe.edu.upeu.gestioo_de_cine.interfaces.IPeliculaDao;
import pe.edu.upeu.gestioo_de_cine.interfaces.ISalaDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Funcion;
import pe.edu.upeu.gestioo_de_cine.modelo.Pelicula;
import pe.edu.upeu.gestioo_de_cine.modelo.Sala;

import java.util.List;

public class TestFuncion {
    static IFuncionDao fu = new FuncionDaoImpl();
    static IPeliculaDao pe = new PeliculaDaoImpl();
    static ISalaDao sa = new SalaDaoImpl();

    public static void listar() {
        List<Funcion> lista = fu.listAll();
        if (lista != null && !lista.isEmpty()) {
            for (Funcion f : lista) {
                System.out.println("ID_FUNCION: " + f.getIdfuncion());
                System.out.println("Pelicula: " + f.getPelicula().getNombre());
                System.out.println("Sala: " + f.getSala().getNombre());
                System.out.println("Horario: " + f.getHorario());
                System.out.println("Precio: " + f.getPrecio());
            }
        } else {
            System.out.println("no hay registros");
        }
    }

    public static void insertar() {
        List<Pelicula> peliculas = pe.listAll();
        List<Sala> salas = sa.listAll();
        if (peliculas.isEmpty() || salas.isEmpty()) {
            System.out.println("Registra primero una pelicula y una sala");
            return;
        }
        Funcion f = new Funcion();
        f.setPelicula(peliculas.get(0));
        f.setSala(salas.get(0));
        f.setHorario("18:30");
        f.setPrecio(12.50);
        boolean result = fu.insert(f);
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
