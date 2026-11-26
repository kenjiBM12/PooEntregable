package pe.edu.upeu.gestioo_de_cine.interfaces;

import pe.edu.upeu.gestioo_de_cine.modelo.Butaca;

import java.util.List;

public interface IButacaDao {

    List<Butaca> listarPorSala(int idSala);

    List<Butaca> listarOcupadas(int idFuncion);

    boolean tieneButacas(int idSala);

    int contarPorSala(int idSala);

    boolean insertar(Butaca b);

    int generarButacas(int idSala, int filas, int columnas, int capacidad);
}
