package com.example.proyecto_final.test;

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
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class TestFuncion {

    static IFuncionDao dao = new FuncionDaoImpl();
    static IPeliculaDao peliculaDao = new PeliculaDaoImpl();
    static ISalaDao salaDao = new SalaDaoImpl();

    static Scanner teclado = new Scanner(System.in);

    public static void insertar() {
        Funcion f = new Funcion();

        if (!llenarDatos(f)) {
            return;
        }

        System.out.println(dao.insert(f)
                ? "Función registrada"
                : "Error al registrar");
    }

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

    public static void buscar() {
        Funcion f = dao.searchById(leerId());

        if (f != null) {
            mostrar(f);
        } else {
            System.out.println("Función no encontrada");
        }
    }

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

    private static boolean llenarDatos(Funcion f) {
        System.out.print("ID de película: ");

        int idPelicula = Integer.parseInt(teclado.nextLine().trim());

        System.out.print("ID de sala: ");
        int idSala = Integer.parseInt(teclado.nextLine().trim());

        Pelicula p = peliculaDao.searchById(idPelicula);
        Sala s = salaDao.searchById(idSala);

        if (p == null || s == null) {
            System.out.println("La película o la sala no existe");
            return false;
        }

        System.out.print("Fecha de función (yyyy-MM-dd): ");
        LocalDate horario = LocalDate.parse(teclado.nextLine().trim());

        System.out.print("Precio: ");

        double precio = Double.parseDouble(teclado.nextLine().trim());

        if (horario == null
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

    private static int leerId() {
        System.out.print("ID de función: ");
        return Integer.parseInt(teclado.nextLine().trim());
    }

    private static void mostrar(Funcion f) {
        System.out.println("-------------------------");
        System.out.println("ID: " + f.getIdfuncion());
        System.out.println("Película: " + f.getPelicula().getTitulo());
        System.out.println("Sala: " + f.getSala().getNombre());
        System.out.println("Horario: " + f.getHorario());
        System.out.println("Precio: " + f.getPrecio());
    }

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
