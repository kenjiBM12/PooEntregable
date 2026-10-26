package pe.edu.upeu.project_final.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import pe.edu.upeu.project_final.implementacion.PeliculaDaoImpl;
import pe.edu.upeu.project_final.interfaces.IPeliculaDao;
import pe.edu.upeu.project_final.modelo.Pelicula;

public class VentaController {

    @FXML
    private ComboBox<String> cbPeliculas;
    @FXML
    private TextField txtCantidad;
    @FXML
    private TextField txtPrecio;

    private final IPeliculaDao daoPelicula = new PeliculaDaoImpl();

    @FXML
    public void initialize() {
        cargarPeliculasEnComboBox();
    }

    private void cargarPeliculasEnComboBox() {
        ObservableList<String> listaPeliculas = FXCollections.observableArrayList();

        for (Pelicula p : daoPelicula.listAll()) {
            listaPeliculas.add(p.getTitulo());
        }
        cbPeliculas.setItems(listaPeliculas);
    }
}
