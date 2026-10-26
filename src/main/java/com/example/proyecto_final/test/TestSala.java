package com.example.proyecto_final.test;

import com.example.proyecto_final.implementacion.SalaDaoImpl;
import com.example.proyecto_final.interfaces.ISalaDao;
import com.example.proyecto_final.modelo.Sala;
import java.util.List;

public class TestSala {
    static ISalaDao dao = new SalaDaoImpl();

    public static void listar() {
        List<Sala> lista = dao.listAll();
        if (lista != null && !lista.isEmpty()) {
            for (Sala sala : lista) {
                System.out.println("Idsala: " + sala.getIdsala());
                System.out.println("Nombre: " + sala.getNombre());
                System.out.println("Tipo: " + sala.getTipo());
                System.out.println("Capacidad: " + sala.getCapacidad());
            }
        } else {
            System.out.println("No hay registros");
        }
    }

    public static void insertar(Sala sala) {
        boolean result = dao.insert(sala);
        if (result) {
            System.out.println("Registro satisfactorio");
        } else {
            System.out.println("Error al insertar");
        }
    }

    public static void actualizar(Sala sala) {
        boolean result = dao.update(sala);
        if (result) {
            System.out.println("Registro actualizado");
        } else {
            System.out.println("Error al actualizar");
        }
    }

    public static void eliminar(Sala sala) {
        boolean result = dao.delete(sala);
        if (result) {
            System.out.println("Registro eliminado");
        } else {
            System.out.println("Error al eliminar");
        }
    }

    public static void main(String[] args) {
        listar();
    }
}