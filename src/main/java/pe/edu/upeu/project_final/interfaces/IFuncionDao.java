package pe.edu.upeu.project_final.interfaces;

import pe.edu.upeu.project_final.modelo.Funcion;
import java.util.List;

public interface IFuncionDao {
    boolean insert(Funcion f);
    boolean update(Funcion f);
    Funcion searchById(int id);
    boolean delete(Funcion f);
    List<Funcion> listAll();
}