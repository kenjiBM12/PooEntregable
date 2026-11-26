package pe.edu.upeu.gestioo_de_cine.interfaces;

import pe.edu.upeu.gestioo_de_cine.modelo.CierreCaja;
import pe.edu.upeu.gestioo_de_cine.modelo.ResumenPago;

import java.util.List;

/**
 * MEJORA 1: DAO del cierre de caja.
 *
 * - calcularDia: cuanto se espera por cada forma de pago y cuanto se devolvio.
 * - registrar: guarda el arqueo del dia (reemplaza el cierre anterior de esa fecha).
 * - listar: historial de cierres para mostrarlo en pantalla.
 */
public interface ICierreCajaDao {

    List<ResumenPago> calcularDia(String fecha);

    boolean registrar(CierreCaja cierre);

    List<CierreCaja> listar(int limite);

    CierreCaja buscarPorFecha(String fecha);
}
