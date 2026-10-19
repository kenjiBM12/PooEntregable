package com.example.proyecto_final.interfaces;

import com.example.proyecto_final.modelo.Venta;
import java.util.List;

public interface IVentaDao {
    int registrarVenta(Venta venta);
    List<Venta> listarVentas();
}