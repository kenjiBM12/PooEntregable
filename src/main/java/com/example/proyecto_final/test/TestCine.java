package com.example.proyecto_final.test;

import com.example.proyecto_final.implementacion.PeliculaDaoImpl;
import com.example.proyecto_final.interfaces.IPeliculaDao;
import com.example.proyecto_final.modelo.Pelicula;

public class TestCine {
    public static void main(String[] args) {
        IPeliculaDao dao = new PeliculaDaoImpl();

        // Probando inserción rápida
        Pelicula p = new Pelicula(0, "Avengers: Endgame", "Acción", 181, "+14");
        int resultado = dao.insertar(p);

        if (resultado > 0) {
            System.out.println("¡Prueba exitosa! Película guardada en SQLite.");
        } else {
            System.out.println("Error al guardar en la prueba.");
        }

        // Listando
        System.out.println("--- LISTA DE PELÍCULAS ---");
        for (Pelicula pelis : dao.listar()) {
            System.out.println(pelis.getId() + " - " + pelis.getTitulo() + " (" + pelis.getGenero() + ")");
        }
    }
}