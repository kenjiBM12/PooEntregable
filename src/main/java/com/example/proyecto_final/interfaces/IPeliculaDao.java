package com.example.proyecto_final.interfaces;

import com.example.proyecto_final.modelo.Pelicula;

import java.util.List;

public interface IPeliculaDao {

    boolean insert(Pelicula p);

    boolean update(Pelicula p);

    Pelicula searchById(int id);

    boolean delete(Pelicula p);

    List<Pelicula> listAll();
}