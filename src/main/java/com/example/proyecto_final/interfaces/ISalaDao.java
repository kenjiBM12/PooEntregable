package com.example.proyecto_final.interfaces;

import com.example.proyecto_final.modelo.Sala;

import java.util.List;

public interface ISalaDao {

    boolean insert(Sala s);

    boolean update(Sala s);

    Sala searchById(int id);

    boolean delete(Sala s);

    List<Sala> listAll();
}