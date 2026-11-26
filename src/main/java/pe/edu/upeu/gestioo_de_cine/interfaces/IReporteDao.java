package pe.edu.upeu.gestioo_de_cine.interfaces;

import pe.edu.upeu.gestioo_de_cine.modelo.Reporte;

import java.time.LocalDate;

public interface IReporteDao {

    Reporte generar(LocalDate desde, LocalDate hasta);
}
