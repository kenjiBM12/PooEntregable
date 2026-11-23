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

    //registra el cliente (si es nuevo) y una venta por butaca en una sola transaccion.
    //devuelve el codigo de compra generado, o null si una butaca ya estaba vendida.
    //los errores de BD se lanzan como RuntimeException.
    String registrarVentas(Funcion funcion, Cliente cliente, List<Butaca> butacas, int idUsuario);

    //anula la venta conservando el registro (ya no borra la fila)
    boolean anular(Venta v);

    //anula todos los boletos de una misma compra (mismo codigo)
    boolean anularCompra(String codigoCompra);
}
