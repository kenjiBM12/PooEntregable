package pe.edu.upeu.gestioo_de_cine.interfaces;

import pe.edu.upeu.gestioo_de_cine.modelo.Funcion;

import java.util.List;

public interface IFuncionDao {
    boolean insert(Funcion f);
    boolean update(Funcion f);
    Funcion searchById(int id);
    boolean delete(Funcion f);
    List<Funcion> listAll();

    boolean cancelar(int idfuncion);
}
