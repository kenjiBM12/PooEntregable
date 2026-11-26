package pe.edu.upeu.gestioo_de_cine.interfaces;

import pe.edu.upeu.gestioo_de_cine.modelo.Tarifa;

import java.util.List;

/**
 * MEJORA 4: acceso a la tabla tarifa.
 * dia 0 = tarifa base para todos los dias; 1..7 = lunes..domingo.
 */
public interface ITarifaDao {

    List<Tarifa> listAll();

    /** Precio configurado para ese tipo de sala y dia, o -1 si no hay. */
    double precio(String tipoSala, int diaSemana);

    /** Inserta o actualiza la tarifa de ese tipo de sala y dia. */
    boolean guardar(String tipoSala, int diaSemana, double precio);
}
