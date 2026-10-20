package com.example.proyecto_final.interfaces;

import com.example.proyecto_final.modelo.Venta;

import java.util.List;

public interface IVentaDao {

    boolean insert(Venta v);

    boolean update(Venta v);

    Venta searchById(int id);

    boolean delete(Venta v);

    List<Venta> listAll();
}