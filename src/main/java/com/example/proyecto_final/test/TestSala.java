// TestSala.java
// package organiza las clases; no es la ruta del archivo SQLite.
package com.example.proyecto_final.test;

// import permite utilizar clases de otros paquetes sin escribir su nombre completo.
import com.example.proyecto_final.implementacion.SalaDaoImpl;
import com.example.proyecto_final.interfaces.ISalaDao;
import com.example.proyecto_final.modelo.Sala;

import java.util.Scanner;

/**
 * PRUEBA MANUAL DE CONSOLA: permite ejecutar operaciones sin la interfaz gráfica.
 * Utiliza los mismos modelos, interfaces y DAO que las ventanas.
 * Estas operaciones trabajan con la base real configurada en ConexionDB.
 */
public class TestSala {

    static ISalaDao dao = new SalaDaoImpl();
    // Scanner lee lo que se escribe en la consola durante estas pruebas manuales.
    static Scanner teclado = new Scanner(System.in);

    /**
     * Prueba INSERT: crea el modelo, obtiene los datos y llama a dao.insert().
     */
    public static void insertar() {
        Sala s = new Sala();
        llenarDatos(s);

        System.out.println(dao.insert(s)
                ? "Sala registrada"
                : "Error al registrar");
    }

    /**
     * Prueba SELECT/listAll y muestra cada registro recuperado por consola.
     */
    public static void listar() {
        var lista = dao.listAll();

        if (lista.isEmpty()) {
            System.out.println("No hay salas");
            return;
        }

        for (Sala s : lista) {
            mostrar(s);
        }
    }

    /**
     * Pide un ID y prueba searchById; informa si el registro no aparece.
     */
    public static void buscar() {
        Sala s = dao.searchById(leerId());

        if (s != null) {
            mostrar(s);
        } else {
            System.out.println("Sala no encontrada");
        }
    }

    /**
     * Busca por ID, pide nuevos datos y prueba dao.update().
     */
    public static void actualizar() {
        Sala s = dao.searchById(leerId());

        if (s == null) {
            System.out.println("Sala no encontrada");
            return;
        }

        mostrar(s);
        System.out.println("Ingrese los nuevos datos:");
        llenarDatos(s);

        System.out.println(dao.update(s)
                ? "Sala actualizada"
                : "Error al actualizar");
    }

    /**
     * Busca por ID, pide escribir SI y prueba dao.delete().
     */
    public static void eliminar() {
        Sala s = dao.searchById(leerId());

        if (s == null) {
            System.out.println("Sala no encontrada");
            return;
        }

        mostrar(s);
        System.out.print("¿Eliminar? Escriba SI: ");

        if (teclado.nextLine().trim().equalsIgnoreCase("SI")) {
            System.out.println(dao.delete(s)
                    ? "Sala eliminada"
                    : "Error al eliminar");
        }
    }

    /**
     * Lee y convierte los datos introducidos por consola.
     * No guarda por sí mismo: prepara el objeto para insert o update.
     */
    private static void llenarDatos(Sala s) {
        System.out.print("Nombre: ");
        String nombre = teclado.nextLine().trim();

        System.out.print("Tipo (2D, 3D o VIP): ");
        String tipo = teclado.nextLine().trim();

        System.out.print("Capacidad: ");
        // Convierte texto a int; texto vacío o letras provocan NumberFormatException.
        int capacidad = Integer.parseInt(teclado.nextLine().trim());

        if (nombre.isEmpty() || tipo.isEmpty() || capacidad <= 0) {
            throw new IllegalArgumentException(
                    "Complete los campos y use una capacidad mayor que cero");
        }

        s.setNombre(nombre);
        s.setTipo(tipo);
        s.setCapacidad(capacidad);
    }

    /**
     * Lee una línea de texto de la consola y la convierte a un número entero.
     */
    private static int leerId() {
        System.out.print("ID de sala: ");
        return Integer.parseInt(teclado.nextLine().trim());
    }

    /**
     * Imprime los datos del objeto recibido; no consulta ni cambia la base.
     */
    private static void mostrar(Sala s) {
        System.out.println("-------------------------");
        System.out.println("ID: " + s.getIdsala());
        System.out.println("Nombre: " + s.getNombre());
        System.out.println("Tipo: " + s.getTipo());
        System.out.println("Capacidad: " + s.getCapacidad());
    }

    /**
     * Punto de entrada que ejecuta Java al iniciar esta clase.
     * En los tests permite trabajar por consola; en HelloApplication inicia JavaFX.
     */
    public static void main(String[] args) {
        while (true) {
            System.out.println("""
                    
                    === TEST SALA ===
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
