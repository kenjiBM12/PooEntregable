package pe.edu.upeu.gestioo_de_cine.test;

import pe.edu.upeu.gestioo_de_cine.implementacion.SalaDaoImpl;
import pe.edu.upeu.gestioo_de_cine.interfaces.ISalaDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Sala;
import java.util.List;

public class testSala {
    static ISalaDao sa = new SalaDaoImpl();

    public static void listar() {
        List<Sala> lista = sa.listAll();
        if (lista != null && !lista.isEmpty()) {
            for (Sala sala : lista) {
                System.out.println("ID_sala: " + sala.getIdsala());
                System.out.println("Numero: " + sala.getNumero());
                System.out.println("TipoSala: " + sala.getTipoSala());
                System.out.println("Capacidad: " + sala.getCapacidad());
                System.out.println("Estado: " + sala.getEstado());
            }
        } else {
            System.out.println("no hay registros");
        }
    }

    public static void insertar() {
        Sala s = new Sala();
        s.setNumero(1);
        s.setTipoSala("2D");
        s.setCapacidad(23);
        s.setEstado("disponible");
        boolean result = sa.insert(s);
        if (result) {
            System.out.println("insert success");
        } else {
            System.out.println("insert error");
        }
    }

    public static void eliminar() {
        Sala s = new Sala();
        s.setIdsala(1);
        boolean result = sa.delete(s);
        if (result) {
            System.out.println("delete success");
        } else {
            System.out.println("delete error");
        }
    }

    public static void actualizar(int id) {
        Sala sala = sa.searchById(id);

        if (sala == null) {
            System.out.println("No se encontró la película con ID " + id);
            return;
        }
        sala.setNumero(2);
        sala.setTipoSala("120");
        sala.setCapacidad(24);
        sala.setEstado("disponible");

        boolean resultado = sa.update(sala);

        if (resultado) {
            System.out.println("Actualización exitosa.");
            listar();
        } else {
            System.out.println("Error al actualizar la sala");
        }
    }

    public static void actualizarPorNumero(int num) {
        Sala sala = sa.searchByNum(num);

        if (sala == null) {
            System.out.println("No se encontró la película con Numero " + num);
            return;
        }

        sala.setNumero(11);

        sala.setTipoSala("3D");
        sala.setCapacidad(23);
        sala.setEstado("disponible");

        boolean resultado = sa.update(sala);

        if (resultado) {
            System.out.println("Actualización exitosa.");
            listar();
        } else {
            System.out.println("Error al actualizar la sala.");
        }
    }




    public static void main(String[] args) {
        System.out.println("************");
        insertar();
        System.out.println("\n**********");
        listar();
        System.out.println("\n**********");
        actualizar(1);
        System.out.println("\n**********");
        eliminar();
        System.out.println("\n**********");
        actualizarPorNumero(1);
    }



}
