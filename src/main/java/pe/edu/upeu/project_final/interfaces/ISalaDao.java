package pe.edu.upeu.project_final.interfaces;

import pe.edu.upeu.project_final.modelo.Sala;

import java.util.List;

public interface ISalaDao {
    boolean insert(Sala s);
    boolean update(Sala s);
    Sala searchById(int id);
    boolean delete(Sala s);
    List<Sala> listAll();


}
