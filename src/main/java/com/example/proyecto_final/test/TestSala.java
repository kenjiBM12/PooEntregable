package com.example.proyecto_final.test;

import com.example.proyecto_final.implementacion.SalaDaoImpl;
import com.example.proyecto_final.interfaces.ISalaDao;
import com.example.proyecto_final.modelo.Sala;

import java.util.Scanner;

public class TestSala {

    static ISalaDao dao = new SalaDaoImpl();

    static Scanner teclado = new Scanner(System.in);

    public static void insertar() {
        Sala s = new Sala();
        llenarDatos(s);

        System.out.println(dao.insert(s)
                ? "Sala registrada"
                : "Error al registrar");
    }

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

    public static void buscar() {
        Sala s = dao.searchById(leerId());

        if (s != null) {
            mostrar(s);
        } else {
            System.out.println("Sala no encontrada");
        }
    }

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

    private static void llenarDatos(Sala s) {
        System.out.print("Nombre: ");
        String nombre = teclado.nextLine().trim();

        System.out.print("Tipo (2D, 3D o VIP): ");
        String tipo = teclado.nextLine().trim();

        System.out.print("Capacidad: ");

        int capacidad = Integer.parseInt(teclado.nextLine().trim());

        if (nombre.isEmpty() || tipo.isEmpty() || capacidad <= 0) {
            throw new IllegalArgumentException(
                    "Complete los campos y use una capacidad mayor que cero");
        }

        s.setNombre(nombre);
        s.setTipo(tipo);
        s.setCapacidad(capacidad);
    }

    private static int leerId() {
        System.out.print("ID de sala: ");
        return Integer.parseInt(teclado.nextLine().trim());
    }

    private static void mostrar(Sala s) {
        System.out.println("-------------------------");
        System.out.println("ID: " + s.getIdsala());
        System.out.println("Nombre: " + s.getNombre());
        System.out.println("Tipo: " + s.getTipo());
        System.out.println("Capacidad: " + s.getCapacidad());
    }

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
