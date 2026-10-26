package pe.edu.upeu.project_final.interfaces;

import pe.edu.upeu.project_final.modelo.Sala;

import java.util.List;

public interface ISalaDao {
    public boolean insert(Sala s);

    public boolean update(Sala s);

    public Sala searchById(int id);

    public boolean delete(Sala s);

    public List<Sala> listAll();

}
