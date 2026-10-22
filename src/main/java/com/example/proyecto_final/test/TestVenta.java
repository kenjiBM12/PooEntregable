package com.example.proyecto_final.test;

import com.example.proyecto_final.implementacion.FuncionDaoImpl;
import com.example.proyecto_final.implementacion.VentaDaoImpl;
import com.example.proyecto_final.interfaces.IFuncionDao;
import com.example.proyecto_final.interfaces.IVentaDao;
import com.example.proyecto_final.modelo.Funcion;
import com.example.proyecto_final.modelo.Venta;

import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class TestVenta {

    static IVentaDao dao = new VentaDaoImpl();
    static IFuncionDao funcionDao = new FuncionDaoImpl();

    static Scanner teclado = new Scanner(System.in);

    public static void insertar() {
        Venta v = new Venta();

        if (!llenarDatos(v)) {
            return;
        }

        System.out.println(dao.insert(v)
                ? "Venta registrada"
                : "Error al registrar");
    }

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

    public static void buscar() {
        Venta v = dao.searchById(leerId());

        if (v != null) {
            mostrar(v);
        } else {
            System.out.println("Venta no encontrada");
        }
    }

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

    private static boolean llenarDatos(Venta v) {
        System.out.print("ID de función: ");

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

        System.out.print("Fecha de venta (yyyy-MM-dd): ");
        LocalDate fecha = LocalDate.parse(teclado.nextLine().trim());
        v.setFechaVenta(fecha);
        v.setFuncion(f);
        v.setCliente(cliente);
        v.setCantidadAsientos(cantidad);
        v.setTotalPagar(total);

        System.out.println("Total calculado: " + total);
        return true;
    }

    private static int leerId() {
        System.out.print("ID de venta: ");
        return Integer.parseInt(teclado.nextLine().trim());
    }

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

    }

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
            } catch (IllegalArgumentException | DateTimeParseException e) {
                System.out.println("Datos incorrectos: " + e.getMessage());
            }
        }
    }
}
