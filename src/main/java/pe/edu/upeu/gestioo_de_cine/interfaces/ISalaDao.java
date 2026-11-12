package pe.edu.upeu.gestioo_de_cine.interfaces;

import pe.edu.upeu.gestioo_de_cine.modelo.Sala;

import java.util.List;

public interface ISalaDao {
    boolean insert(Sala s);
    boolean update(Sala s);
    Sala searchById(int id);
    Sala searchByNum(int num);
    boolean delete(Sala s);
    List<Sala> listAll();
}
