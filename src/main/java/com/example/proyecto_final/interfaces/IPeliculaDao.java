package com.example.proyecto_final.interfaces;

import com.example.proyecto_final.modelo.Pelicula;
import java.util.List;

public interface IPeliculaDao {
    int insertar(Pelicula p);
    List<Pelicula> listar();
    int actualizar(Pelicula p);
    int eliminar(int id);
}