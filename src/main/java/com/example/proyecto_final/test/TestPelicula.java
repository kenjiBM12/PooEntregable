// TestPelicula.java
// package organiza las clases; no es la ruta del archivo SQLite.
package com.example.proyecto_final.test;

// import permite utilizar clases de otros paquetes sin escribir su nombre completo.
import com.example.proyecto_final.implementacion.PeliculaDaoImpl;
import com.example.proyecto_final.interfaces.IPeliculaDao;
import com.example.proyecto_final.modelo.Pelicula;

import java.util.Scanner;

/**
 * PRUEBA MANUAL DE CONSOLA: permite ejecutar operaciones sin la interfaz gráfica.
 * Utiliza los mismos modelos, interfaces y DAO que las ventanas.
 * Estas operaciones trabajan con la base real configurada en ConexionDB.
 */
public class TestPelicula {

    static IPeliculaDao dao = new PeliculaDaoImpl();
    // Scanner lee lo que se escribe en la consola durante estas pruebas manuales.
    static Scanner teclado = new Scanner(System.in);

    /**
     * Prueba INSERT: crea el modelo, obtiene los datos y llama a dao.insert().
     */
    public static void insertar() {
        Pelicula p = new Pelicula();
        llenarDatos(p);

        System.out.println(dao.insert(p)
                ? "Película registrada"
                : "Error al registrar");
    }

    /**
     * Prueba SELECT/listAll y muestra cada registro recuperado por consola.
     */
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

    /**
     * Pide un ID y prueba searchById; informa si el registro no aparece.
     */
    public static void buscar() {
        Pelicula p = dao.searchById(leerId());

        if (p != null) {
            mostrar(p);
        } else {
            System.out.println("Película no encontrada");
        }
    }

    /**
     * Busca por ID, pide nuevos datos y prueba dao.update().
     */
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

    /**
     * Busca por ID, pide escribir SI y prueba dao.delete().
     */
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

    /**
     * Lee y convierte los datos introducidos por consola.
     * No guarda por sí mismo: prepara el objeto para insert o update.
     */
    private static void llenarDatos(Pelicula p) {
        System.out.print("Título: ");
        String titulo = teclado.nextLine().trim();

        System.out.print("Género: ");
        String genero = teclado.nextLine().trim();

        System.out.print("Duración en minutos: ");
        // Convierte texto a int; texto vacío o letras provocan NumberFormatException.
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

    /**
     * Lee una línea de texto de la consola y la convierte a un número entero.
     */
    private static int leerId() {
        System.out.print("ID de película: ");
        return Integer.parseInt(teclado.nextLine().trim());
    }

    /**
     * Imprime los datos del objeto recibido; no consulta ni cambia la base.
     */
    private static void mostrar(Pelicula p) {
        System.out.println("-------------------------");
        System.out.println("ID: " + p.getIdpelicula());
        System.out.println("Título: " + p.getTitulo());
        System.out.println("Género: " + p.getGenero());
        System.out.println("Duración: " + p.getDuracion());
        System.out.println("Clasificación: " + p.getClasificacion());
    }

    /**
     * Punto de entrada que ejecuta Java al iniciar esta clase.
     * En los tests permite trabajar por consola; en HelloApplication inicia JavaFX.
     */
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
                // El menú decide qué método de prueba ejecutar según la opción escrita.
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
