package com.example.proyecto_final.test;

import com.example.proyecto_final.implementacion.FuncionDaoImpl;
import com.example.proyecto_final.interfaces.IFuncionDao;
import com.example.proyecto_final.modelo.Funcion;
import java.util.List;

public class TestFuncion {
    static IFuncionDao dao = new FuncionDaoImpl();

    public static void listar() {
        List<Funcion> lista = dao.listAll();
        if (lista != null && !lista.isEmpty()) {
            for (Funcion funcion : lista) {
                System.out.println("Idfuncion: " + funcion.getIdfuncion());
                System.out.println("Horario: " + funcion.getHorario());
                System.out.println("Precio: " + funcion.getPrecio());
            }
        } else {
            System.out.println("No hay registros");
        }
    }

    public static void insertar(Funcion funcion) {
        boolean result = dao.insert(funcion);
        if (result) {
            System.out.println("Registro satisfactorio");
        } else {
            System.out.println("Error al insertar");
        }
    }

    public static void actualizar(Funcion funcion) {
        boolean result = dao.update(funcion);
        if (result) {
            System.out.println("Registro actualizado");
        } else {
            System.out.println("Error al actualizar");
        }
    }

    public static void eliminar(Funcion funcion) {
        boolean result = dao.delete(funcion);
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