package pe.edu.upeu.gestioo_de_cine.interfaces;

import pe.edu.upeu.gestioo_de_cine.modelo.Butaca;
import pe.edu.upeu.gestioo_de_cine.modelo.Cliente;
import pe.edu.upeu.gestioo_de_cine.modelo.Funcion;
import pe.edu.upeu.gestioo_de_cine.modelo.Venta;

import java.util.List;

public interface IVentaDao {
    boolean insert(Venta v);
    boolean update(Venta v);
    Venta searchById(int id);
    List<Venta> listAll();

    String registrarVentas(Funcion funcion, Cliente cliente, List<Butaca> butacas,
                           int idUsuario, String metodoPago);

    boolean anular(Venta v, int idUsuario, String motivo);

    boolean anularCompra(String codigoCompra, int idUsuario, String motivo);

    List<Venta> listarPorCliente(int idCliente);

    List<Venta> listarPorCodigo(String codigoCompra);

    int anularPorFuncion(int idFuncion, int idUsuario, String motivo);
}
