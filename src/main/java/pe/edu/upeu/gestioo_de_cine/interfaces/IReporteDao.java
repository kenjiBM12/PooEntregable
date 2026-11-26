package pe.edu.upeu.gestioo_de_cine.interfaces;

import pe.edu.upeu.gestioo_de_cine.modelo.Reporte;

import java.time.LocalDate;

/**
 * MEJORA 3: interfaz del reporte de cierre de caja.
 * Antes esta interfaz estaba vacia y no se usaba en ninguna pantalla.
 *
 * El DAO recibe el rango de fechas (desde / hasta) y devuelve un Reporte
 * con los totales. La pantalla de Reportes usa estos numeros para mostrarlos.
 */
public interface IReporteDao {

    /**
     * Calcula los totales de las ventas cuya funcion esta entre las dos fechas.
     * Las fechas se comparan solo por el dia (yyyy-MM-dd).
     */
    Reporte generar(LocalDate desde, LocalDate hasta);
}
