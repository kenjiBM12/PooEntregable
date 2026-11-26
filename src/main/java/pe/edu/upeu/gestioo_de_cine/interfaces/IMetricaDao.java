package pe.edu.upeu.gestioo_de_cine.interfaces;

import pe.edu.upeu.gestioo_de_cine.modelo.Metrica;

/**
 * MEJORA 10: interfaz de los numeros de la pantalla de Inicio.
 * El DAO devuelve un solo objeto Metrica con todos los contadores.
 */
public interface IMetricaDao {

    Metrica obtener();
}
