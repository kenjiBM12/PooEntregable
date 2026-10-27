package pe.edu.upeu.project_final.interfaces;

import pe.edu.upeu.project_final.modelo.Venta;

import java.time.LocalDate;
import java.util.List;

public interface IVentaDao {
    boolean insert(Venta v);
    boolean update(Venta v);
    Venta searchById(int id);
    boolean delete(Venta v);
    List<Venta> listAll();
    List<Venta> listByDate(LocalDate fecha);
}
