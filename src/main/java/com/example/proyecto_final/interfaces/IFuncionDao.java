package com.example.proyecto_final.interfaces;

import com.example.proyecto_final.modelo.Funcion;
import java.util.List;

public interface IFuncionDao {
    int insertar(Funcion f);
    List<Funcion> listar();
    int actualizar(Funcion f);
    int eliminar(int id);
}