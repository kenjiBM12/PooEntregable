package pe.edu.upeu.gestioo_de_cine.interfaces;

import pe.edu.upeu.gestioo_de_cine.modelo.Estadistica;

import java.time.LocalDate;
import java.util.List;

/**
 * MEJORA 3: consultas de agregacion para los gráficos de Reportes.
 * Todas cuentan solo boletos activos (los anulados no son ingreso).
 */
public interface IEstadisticaDao {

    /** Boletos e ingresos por dia de venta dentro del rango. */
    List<Estadistica> ventasPorDia(LocalDate desde, LocalDate hasta);

    /** Peliculas con mas boletos vendidos. */
    List<Estadistica> topPeliculas(int limite);

    /** Funciones con mas ocupacion (boletos / capacidad de la sala). */
    List<Estadistica> ocupacionPorFuncion(int limite);
}
