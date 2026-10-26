package pe.edu.upeu.project_final.interfaces;

import pe.edu.upeu.project_final.modelo.Venta;

import java.util.List;

public interface IVentaDao {
    public boolean insert(Venta v);

    public boolean update(Venta v);

    public Venta searchById(int id);

    public boolean delete(Venta v);

    public List<Venta> listAll();

}
