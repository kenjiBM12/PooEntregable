package pe.edu.upeu.gestioo_de_cine.interfaces;

import pe.edu.upeu.gestioo_de_cine.modelo.Butaca;

import java.util.List;

public interface IButacaDao {

    //butacas de una sala, ordenadas por fila y numero
    List<Butaca> listarPorSala(int idSala);

    //butacas ocupadas en una funcion (solo ventas activas, las anuladas no cuentan)
    List<Butaca> listarOcupadas(int idFuncion);

    boolean tieneButacas(int idSala);

    //cuantas butacas hay registradas en la sala
    int contarPorSala(int idSala);

    boolean insertar(Butaca b);

    //genera la matriz de butacas de una sala en una sola transaccion.
    //devuelve cuantas butacas nuevas se crearon; no duplica las que ya existen.
    int generarButacas(int idSala, int filas, int columnas, int capacidad);
}
