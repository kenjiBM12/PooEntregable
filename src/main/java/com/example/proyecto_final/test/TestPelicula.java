// TestPelicula.java
package com.example.proyecto_final.test;

import com.example.proyecto_final.implementacion.PeliculaDaoImpl;
import com.example.proyecto_final.interfaces.IPeliculaDao;
import com.example.proyecto_final.modelo.Pelicula;

import java.util.Scanner;

public class TestPelicula {

    static IPeliculaDao dao = new PeliculaDaoImpl();
    static Scanner teclado = new Scanner(System.in);

    public static void insertar() {
        Pelicula p = new Pelicula();
        llenarDatos(p);

        System.out.println(dao.insert(p)
                ? "Película registrada"
                : "Error al registrar");
    }

    public static void listar() {
        var lista = dao.listAll();

        if (lista.isEmpty()) {
            System.out.println("No hay películas");
            return;
        }

        for (Pelicula p : lista) {
            mostrar(p);
        }
    }

    public static void buscar() {
        Pelicula p = dao.searchById(leerId());

        if (p != null) {
            mostrar(p);
        } else {
            System.out.println("Película no encontrada");
        }
    }

    public static void actualizar() {
        Pelicula p = dao.searchById(leerId());

        if (p == null) {
            System.out.println("Película no encontrada");
            return;
        }

        mostrar(p);
        System.out.println("Ingrese los nuevos datos:");
        llenarDatos(p);

        System.out.println(dao.update(p)
                ? "Película actualizada"
                : "Error al actualizar");
    }

    public static void eliminar() {
        Pelicula p = dao.searchById(leerId());

        if (p == null) {
            System.out.println("Película no encontrada");
            return;
        }

        mostrar(p);
        System.out.print("¿Eliminar? Escriba SI: ");

        if (teclado.nextLine().trim().equalsIgnoreCase("SI")) {
            System.out.println(dao.delete(p)
                    ? "Película eliminada"
                    : "Error al eliminar");
        }
    }

    private static void llenarDatos(Pelicula p) {
        System.out.print("Título: ");
        String titulo = teclado.nextLine().trim();

        System.out.print("Género: ");
        String genero = teclado.nextLine().trim();

        System.out.print("Duración en minutos: ");
        int duracion = Integer.parseInt(teclado.nextLine().trim());

        System.out.print("Clasificación: ");
        String clasificacion = teclado.nextLine().trim();

        if (titulo.isEmpty() || genero.isEmpty()
                || clasificacion.isEmpty() || duracion <= 0) {
            throw new IllegalArgumentException(
                    "Complete los campos y use una duración mayor que cero");
        }

        p.setTitulo(titulo);
        p.setGenero(genero);
        p.setDuracion(duracion);
        p.setClasificacion(clasificacion);
    }

    private static int leerId() {
        System.out.print("ID de película: ");
        return Integer.parseInt(teclado.nextLine().trim());
    }

    private static void mostrar(Pelicula p) {
        System.out.println("-------------------------");
        System.out.println("ID: " + p.getIdpelicula());
        System.out.println("Título: " + p.getTitulo());
        System.out.println("Género: " + p.getGenero());
        System.out.println("Duración: " + p.getDuracion());
        System.out.println("Clasificación: " + p.getClasificacion());
    }

    public static void main(String[] args) {
        while (true) {
            System.out.println("""
                    
                    === TEST PELÍCULA ===
                    1. Insertar
                    2. Listar
                    3. Buscar por ID
                    4. Actualizar
                    5. Eliminar
                    0. Salir
                    """);

            System.out.print("Opción: ");
            String opcion = teclado.nextLine().trim();

            try {
                switch (opcion) {
                    case "1" -> insertar();
                    case "2" -> listar();
                    case "3" -> buscar();
                    case "4" -> actualizar();
                    case "5" -> eliminar();
                    case "0" -> {
                        return;
                    }
                    default -> System.out.println("Opción incorrecta");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Datos incorrectos: " + e.getMessage());
            }
        }
    }
}