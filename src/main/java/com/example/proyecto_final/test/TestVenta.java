package com.example.proyecto_final.test;

import com.example.proyecto_final.implementacion.VentaDaoImpl;
import com.example.proyecto_final.interfaces.IVentaDao;
import com.example.proyecto_final.modelo.Venta;
import java.util.List;

public class TestVenta {
    static IVentaDao dao = new VentaDaoImpl();

    public static void listar() {
        List<Venta> lista = dao.listAll();
        if (lista != null && !lista.isEmpty()) {
            for (Venta venta : lista) {
                System.out.println("Idventa: " + venta.getIdventa());
                System.out.println("Cliente: " + venta.getCliente());
                System.out.println("CantidadAsientos: " + venta.getCantidadAsientos());
                System.out.println("TotalPagar: " + venta.getTotalPagar());
                System.out.println("FechaVenta: " + venta.getFechaVenta());
            }
        } else {
            System.out.println("No hay registros");
        }
    }

    public static void insertar(Venta venta) {
        boolean result = dao.insert(venta);
        if (result) {
            System.out.println("Registro satisfactorio");
        } else {
            System.out.println("Error al insertar");
        }
    }

    public static void actualizar(Venta venta) {
        boolean result = dao.update(venta);
        if (result) {
            System.out.println("Registro actualizado");
        } else {
            System.out.println("Error al actualizar");
        }
    }

    public static void eliminar(Venta venta) {
        boolean result = dao.delete(venta);
        if (result) {
            System.out.println("Registro eliminado");
        } else {
            System.out.println("Error al eliminar");
        }
    }

    public static void main(String[] args) {
        listar();
    }
}