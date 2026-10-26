package com.example.proyecto_final.test;

import com.example.proyecto_final.implementacion.PeliculaDaoImpl;
import com.example.proyecto_final.interfaces.IPeliculaDao;
import com.example.proyecto_final.modelo.Pelicula;
import java.util.List;

public class TestPelicula {
    static IPeliculaDao dao = new PeliculaDaoImpl();

    public static void listar() {
        List<Pelicula> lista = dao.listAll();
        if (lista != null && !lista.isEmpty()) {
            for (Pelicula pelicula : lista) {
                System.out.println("Idpelicula: " + pelicula.getIdpelicula());
                System.out.println("Titulo: " + pelicula.getTitulo());
                System.out.println("Genero: " + pelicula.getGenero());
                System.out.println("Duracion: " + pelicula.getDuracion());
                System.out.println("Clasificacion: " + pelicula.getClasificacion());
            }
        } else {
            System.out.println("No hay registros");
        }
    }

    public static void insertar(Pelicula pelicula) {
        boolean result = dao.insert(pelicula);
        if (result) {
            System.out.println("Registro satisfactorio");
        } else {
            System.out.println("Error al insertar");
        }
    }

    public static void actualizar(Pelicula pelicula) {
        boolean result = dao.update(pelicula);
        if (result) {
            System.out.println("Registro actualizado");
        } else {
            System.out.println("Error al actualizar");
        }
    }

    public static void eliminar(Pelicula pelicula) {
        boolean result = dao.delete(pelicula);
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