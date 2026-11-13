package pe.edu.upeu.gestioo_de_cine.interfaces;

import pe.edu.upeu.gestioo_de_cine.modelo.Venta;

import java.util.List;

public interface IVentaDao {
    boolean insert(Venta v);
    boolean update(Venta v);
    Venta searchById(int id);
    boolean delete(Venta v);
    List<Venta> listAll();
}
