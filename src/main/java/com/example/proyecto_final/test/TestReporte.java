package com.example.proyecto_final.test;

import com.example.proyecto_final.implementacion.VentaDaoImpl;
import com.example.proyecto_final.interfaces.IVentaDao;
import com.example.proyecto_final.modelo.Venta;
import java.math.BigDecimal;
import java.time.LocalDate;

import java.util.List;
import java.util.Locale;

public class TestReporte {
    static IVentaDao dao = new VentaDaoImpl();

    public static void listar(LocalDate fecha) {
        List<Venta> lista = dao.listByDate(fecha);

        BigDecimal total = BigDecimal.ZERO;
        long entradas = 0;
        System.out.println("REPORTE DEL DÍA: " + fecha);
        for (Venta v : lista) {
            System.out.println(v.getIdventa() + " | " + v.getCliente() + " | "
                    + v.getFuncion().getPelicula().getTitulo() + " | "
                    + v.getCantidadAsientos() + " | S/ " + v.getTotalPagar()
                    + " | Fecha función: " + v.getFuncion().getHorario());
            total = total.add(BigDecimal.valueOf(v.getTotalPagar()));
            entradas += v.getCantidadAsientos();
        }
        System.out.println("Ventas: " + lista.size());
        System.out.println("Entradas: " + entradas);
        System.out.printf(Locale.US, "Total: S/ %.2f%n", total);
    }

    public static void main(String[] args) {
        LocalDate fecha = args.length == 0
                ? LocalDate.now() : LocalDate.parse(args[0]);
        listar(fecha);
    }
}
