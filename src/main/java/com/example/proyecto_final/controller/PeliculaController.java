package com.example.proyecto_final.controller;

import com.example.proyecto_final.modelo.Pelicula;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class PeliculaController {

    @FXML
    private TextField txtTitulo;
    @FXML
    private TextField txtGenero;
    @FXML
    private TextField txtDuracion;
    @FXML
    private TextField txtClasificacion;

    @FXML
    private TableView<Pelicula> tablaPeliculas;
    @FXML
    private TableColumn<Pelicula, Integer> colId;
    @FXML
    private TableColumn<Pelicula, String> colTitulo;
    @FXML
    private TableColumn<Pelicula, String> colGenero;
    @FXML
    private TableColumn<Pelicula, Integer> colDuracion;
    @FXML
    private TableColumn<Pelicula, String> colClasificacion;

    @FXML
    public void initialize() {
        if (colId != null) {
            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
            colGenero.setCellValueFactory(new PropertyValueFactory<>("genero"));
            colDuracion.setCellValueFactory(new PropertyValueFactory<>("duracion"));
            colClasificacion.setCellValueFactory(new PropertyValueFactory<>("clasificacion"));
        }
    }
}