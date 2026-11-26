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
    //MEJORA 6: metodoPago es la forma de pago elegida en el formulario
    String registrarVentas(Funcion funcion, Cliente cliente, List<Butaca> butacas,
                           int idUsuario, String metodoPago);

    //anula la venta conservando el registro (ya no borra la fila)
    //MEJORA 6: se guarda quien anulo, cuando y el motivo
    boolean anular(Venta v, int idUsuario, String motivo);

    //anula todos los boletos de una misma compra (mismo codigo)
    boolean anularCompra(String codigoCompra, int idUsuario, String motivo);

    //MEJORA 3: historial de compras de un cliente, de la mas nueva a la mas vieja
    List<Venta> listarPorCliente(int idCliente);

    /**
     * MEJORA 9: todos los boletos de una misma compra (mismo codigo).
     * Sirve para reimprimir el comprobante desde la pantalla de Ventas.
     */
    List<Venta> listarPorCodigo(String codigoCompra);

    /**
     * MEJORA 7: anula todos los boletos activos de una funcion cancelada.
     * Devuelve la cantidad de boletos anulados.
     */
    int anularPorFuncion(int idFuncion, int idUsuario, String motivo);
}
