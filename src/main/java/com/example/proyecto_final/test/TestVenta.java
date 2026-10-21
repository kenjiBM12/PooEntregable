// TestVenta.java
// package organiza las clases; no es la ruta del archivo SQLite.
package com.example.proyecto_final.test;

// import permite utilizar clases de otros paquetes sin escribir su nombre completo.
import com.example.proyecto_final.implementacion.FuncionDaoImpl;
import com.example.proyecto_final.implementacion.VentaDaoImpl;
import com.example.proyecto_final.interfaces.IFuncionDao;
import com.example.proyecto_final.interfaces.IVentaDao;
import com.example.proyecto_final.modelo.Funcion;
import com.example.proyecto_final.modelo.Venta;

import java.util.Scanner;

/**
 * PRUEBA MANUAL DE CONSOLA: permite ejecutar operaciones sin la interfaz gráfica.
 * Utiliza los mismos modelos, interfaces y DAO que las ventanas.
 * Estas operaciones trabajan con la base real configurada en ConexionDB.
 */
public class TestVenta {

    static IVentaDao dao = new VentaDaoImpl();
    static IFuncionDao funcionDao = new FuncionDaoImpl();

    // Scanner lee lo que se escribe en la consola durante estas pruebas manuales.
    static Scanner teclado = new Scanner(System.in);

    /**
     * Prueba INSERT: crea el modelo, obtiene los datos y llama a dao.insert().
     */
    public static void insertar() {
        Venta v = new Venta();

        if (!llenarDatos(v)) {
            return;
        }

        System.out.println(dao.insert(v)
                ? "Venta registrada"
                : "Error al registrar");
    }

    /**
     * Prueba SELECT/listAll y muestra cada registro recuperado por consola.
     */
    public static void listar() {
        var lista = dao.listAll();

        if (lista.isEmpty()) {
            System.out.println("No hay ventas");
            return;
        }

        for (Venta v : lista) {
            mostrar(v);
        }
    }

    /**
     * Pide un ID y prueba searchById; informa si el registro no aparece.
     */
    public static void buscar() {
        Venta v = dao.searchById(leerId());

        if (v != null) {
            mostrar(v);
        } else {
            System.out.println("Venta no encontrada");
        }
    }

    /**
     * Busca por ID, pide nuevos datos y prueba dao.update().
     */
    public static void actualizar() {
        Venta v = dao.searchById(leerId());

        if (v == null) {
            System.out.println("Venta no encontrada");
            return;
        }

        mostrar(v);
        System.out.println("Ingrese los nuevos datos:");

        if (!llenarDatos(v)) {
            return;
        }

        System.out.println(dao.update(v)
                ? "Venta actualizada"
                : "Error al actualizar");
    }

    /**
     * Busca por ID, pide escribir SI y prueba dao.delete().
     */
    public static void eliminar() {
        Venta v = dao.searchById(leerId());

        if (v == null) {
            System.out.println("Venta no encontrada");
            return;
        }

        mostrar(v);
        System.out.print("¿Eliminar? Escriba SI: ");

        if (teclado.nextLine().trim().equalsIgnoreCase("SI")) {
            System.out.println(dao.delete(v)
                    ? "Venta eliminada"
                    : "Error al eliminar");
        }
    }

    /**
     * Lee y convierte los datos introducidos por consola.
     * No guarda por sí mismo: prepara el objeto para insert o update.
     */
    private static boolean llenarDatos(Venta v) {
        System.out.print("ID de función: ");
        // Convierte texto a int; texto vacío o letras provocan NumberFormatException.
        int idFuncion = Integer.parseInt(teclado.nextLine().trim());

        Funcion f = funcionDao.searchById(idFuncion);

        if (f == null) {
            System.out.println("La función no existe");
            return false;
        }

        System.out.println("Película: " + f.getPelicula().getTitulo());
        System.out.println("Precio por entrada: " + f.getPrecio());

        System.out.print("Cliente: ");
        String cliente = teclado.nextLine().trim();

        System.out.print("Cantidad de entradas: ");
        int cantidad = Integer.parseInt(teclado.nextLine().trim());

        double total = f.getPrecio() * cantidad;

        if (cliente.isEmpty() || cantidad <= 0
                || f.getPrecio() <= 0 || !Double.isFinite(total)) {
            throw new IllegalArgumentException(
                    "Ingrese cliente, cantidad y precio válidos");
        }

        v.setFuncion(f);
        v.setCliente(cliente);
        v.setCantidadAsientos(cantidad);
        v.setTotalPagar(total);

        System.out.println("Total calculado: " + total);
        return true;
    }

    /**
     * Lee una línea de texto de la consola y la convierte a un número entero.
     */
    private static int leerId() {
        System.out.print("ID de venta: ");
        return Integer.parseInt(teclado.nextLine().trim());
    }

    /**
     * Imprime los datos del objeto recibido; no consulta ni cambia la base.
     */
    private static void mostrar(Venta v) {
        System.out.println("-------------------------");
        System.out.println("ID: " + v.getIdventa());
        System.out.println("Cliente: " + v.getCliente());
        System.out.println("Función: " + v.getFuncion().getIdfuncion());

        System.out.println("Película: "
                + v.getFuncion().getPelicula().getTitulo());

        System.out.println("Sala: "
                + v.getFuncion().getSala().getNombre());

        System.out.println("Cantidad: " + v.getCantidadAsientos());
        System.out.println("Total: " + v.getTotalPagar());
        System.out.println("Fecha: " + v.getFechaVenta());
        System.out.println("Hora: " + v.getHoraVenta());
    }

    /**
     * Punto de entrada que ejecuta Java al iniciar esta clase.
     * En los tests permite trabajar por consola; en HelloApplication inicia JavaFX.
     */
    public static void main(String[] args) {
        while (true) {
            System.out.println("""
                    
                    === TEST VENTA ===
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
