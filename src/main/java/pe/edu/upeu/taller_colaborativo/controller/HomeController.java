package pe.edu.upeu.taller_colaborativo.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import pe.edu.upeu.taller_colaborativo.modelo.Funcion;

import java.util.List;
import java.util.Locale;

public class HomeController {

    @FXML
    private Label lblPeliculas;
    @FXML
    private Label lblFunciones;
    @FXML
    private Label lblRecaudacion;
    @FXML
    private Label lblEntradas;
    @FXML
    private Label lblProximaFuncion;

    @FXML
    public void initialize() {
        long peliculasActivas = PeliculaController.getListaPeliculas().stream()
                .filter(pelicula -> pelicula.getEstado().equals("Activa"))
                .count();

        lblPeliculas.setText(String.valueOf(peliculasActivas));
        lblFunciones.setText(String.valueOf(FuncionController.getListaFunciones().size()));
        lblRecaudacion.setText(String.format(Locale.US, "S/ %.2f",
                VentaListaController.totalRecaudado()));
        lblEntradas.setText(String.valueOf(VentaListaController.entradasVendidas()));

        List<Funcion> funciones = FuncionController.getListaFunciones();
        lblProximaFuncion.setText(funciones.isEmpty()
                ? "Todavía no hay funciones programadas."
                : funciones.get(0).getPelicula().getTitulo() + " · " + funciones.get(0).getDescripcion());
    }
}
