// TestFuncion.java
// package organiza las clases; no es la ruta del archivo SQLite.
package com.example.proyecto_final.test;

// import permite utilizar clases de otros paquetes sin escribir su nombre completo.
import com.example.proyecto_final.implementacion.FuncionDaoImpl;
import com.example.proyecto_final.implementacion.PeliculaDaoImpl;
import com.example.proyecto_final.implementacion.SalaDaoImpl;
import com.example.proyecto_final.interfaces.IFuncionDao;
import com.example.proyecto_final.interfaces.IPeliculaDao;
import com.example.proyecto_final.interfaces.ISalaDao;
import com.example.proyecto_final.modelo.Funcion;
import com.example.proyecto_final.modelo.Pelicula;
import com.example.proyecto_final.modelo.Sala;

import java.util.Scanner;

/**
 * PRUEBA MANUAL DE CONSOLA: permite ejecutar operaciones sin la interfaz gráfica.
 * Utiliza los mismos modelos, interfaces y DAO que las ventanas.
 * Estas operaciones trabajan con la base real configurada en ConexionDB.
 */
public class TestFuncion {

    static IFuncionDao dao = new FuncionDaoImpl();
    static IPeliculaDao peliculaDao = new PeliculaDaoImpl();
    static ISalaDao salaDao = new SalaDaoImpl();

    // Scanner lee lo que se escribe en la consola durante estas pruebas manuales.
    static Scanner teclado = new Scanner(System.in);

    /**
     * Prueba INSERT: crea el modelo, obtiene los datos y llama a dao.insert().
     */
    public static void insertar() {
        Funcion f = new Funcion();

        if (!llenarDatos(f)) {
            return;
        }

        System.out.println(dao.insert(f)
                ? "Función registrada"
                : "Error al registrar");
    }

    /**
     * Prueba SELECT/listAll y muestra cada registro recuperado por consola.
     */
    public static void listar() {
        var lista = dao.listAll();

        if (lista.isEmpty()) {
            System.out.println("No hay funciones");
            return;
        }

        for (Funcion f : lista) {
            mostrar(f);
        }
    }

    /**
     * Pide un ID y prueba searchById; informa si el registro no aparece.
     */
    public static void buscar() {
        Funcion f = dao.searchById(leerId());

        if (f != null) {
            mostrar(f);
        } else {
            System.out.println("Función no encontrada");
        }
    }

    /**
     * Busca por ID, pide nuevos datos y prueba dao.update().
     */
    public static void actualizar() {
        Funcion f = dao.searchById(leerId());

        if (f == null) {
            System.out.println("Función no encontrada");
            return;
        }

        mostrar(f);
        System.out.println("Ingrese los nuevos datos:");

        if (!llenarDatos(f)) {
            return;
        }

        System.out.println(dao.update(f)
                ? "Función actualizada"
                : "Error al actualizar");
    }

    /**
     * Busca por ID, pide escribir SI y prueba dao.delete().
     */
    public static void eliminar() {
        Funcion f = dao.searchById(leerId());

        if (f == null) {
            System.out.println("Función no encontrada");
            return;
        }

        mostrar(f);
        System.out.print("¿Eliminar? Escriba SI: ");

        if (teclado.nextLine().trim().equalsIgnoreCase("SI")) {
            System.out.println(dao.delete(f)
                    ? "Función eliminada"
                    : "Error al eliminar");
        }
    }

    /**
     * Lee y convierte los datos introducidos por consola.
     * No guarda por sí mismo: prepara el objeto para insert o update.
     */
    private static boolean llenarDatos(Funcion f) {
        System.out.print("ID de película: ");
        // Convierte texto a int; texto vacío o letras provocan NumberFormatException.
        int idPelicula = Integer.parseInt(teclado.nextLine().trim());

        System.out.print("ID de sala: ");
        int idSala = Integer.parseInt(teclado.nextLine().trim());

        Pelicula p = peliculaDao.searchById(idPelicula);
        Sala s = salaDao.searchById(idSala);

        if (p == null || s == null) {
            System.out.println("La película o la sala no existe");
            return false;
        }

        System.out.print("Horario (ejemplo: 2026-11-07 18:30): ");
        String horario = teclado.nextLine().trim();

        System.out.print("Precio: ");
        // Convierte texto a double; usa punto decimal, por ejemplo 15.50.
        double precio = Double.parseDouble(teclado.nextLine().trim());

        if (horario.isEmpty()
                || !Double.isFinite(precio) || precio <= 0) {
            throw new IllegalArgumentException(
                    "Complete el horario y use un precio mayor que cero");
        }

        f.setPelicula(p);
        f.setSala(s);
        f.setHorario(horario);
        f.setPrecio(precio);

        return true;
    }

    /**
     * Lee una línea de texto de la consola y la convierte a un número entero.
     */
    private static int leerId() {
        System.out.print("ID de función: ");
        return Integer.parseInt(teclado.nextLine().trim());
    }

    /**
     * Imprime los datos del objeto recibido; no consulta ni cambia la base.
     */
    private static void mostrar(Funcion f) {
        System.out.println("-------------------------");
        System.out.println("ID: " + f.getIdfuncion());
        System.out.println("Película: " + f.getPelicula().getTitulo());
        System.out.println("Sala: " + f.getSala().getNombre());
        System.out.println("Horario: " + f.getHorario());
        System.out.println("Precio: " + f.getPrecio());
    }

    /**
     * Punto de entrada que ejecuta Java al iniciar esta clase.
     * En los tests permite trabajar por consola; en HelloApplication inicia JavaFX.
     */
    public static void main(String[] args) {
        while (true) {
            System.out.println("""
                    
                    === TEST FUNCIÓN ===
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
