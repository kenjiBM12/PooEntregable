package com.example.proyecto_final.interfaces;

import com.example.proyecto_final.modelo.Sala;
import java.util.List;

public interface ISalaDao {
    int insertar(Sala s);
    List<Sala> listar();
    int actualizar(Sala s);
    int eliminar(int id);
}