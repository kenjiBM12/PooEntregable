package com.example.proyecto_final.test;

import com.example.proyecto_final.implementacion.VentaDaoImpl;
import com.example.proyecto_final.interfaces.IVentaDao;
import com.example.proyecto_final.modelo.Venta;
import java.time.LocalDate;
import java.util.List;

public class TestReporte {
    static IVentaDao dao = new VentaDaoImpl();

    public static void listar(LocalDate fecha) {
        List<Venta> lista = dao.listByDate(fecha);
        double total = 0;
        int entradas = 0;
        for (Venta venta : lista) {
            System.out.println("Cliente: " + venta.getCliente());
            System.out.println("Cantidad: " + venta.getCantidadAsientos());
            System.out.println("Total: " + venta.getTotalPagar());
            total = total + venta.getTotalPagar();
            entradas = entradas + venta.getCantidadAsientos();
        }
        System.out.println("Total vendido: S/ " + total);
        System.out.println("Entradas vendidas: " + entradas);
    }

    public static void main(String[] args) {
        listar(LocalDate.now());
    }
}