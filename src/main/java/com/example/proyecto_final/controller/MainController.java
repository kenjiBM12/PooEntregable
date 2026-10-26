package com.example.proyecto_final.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {

    @FXML
    private StackPane contentArea;

    @FXML

    public void initialize() {
        showHome();
    }

    @FXML

    private void showHome() {
        loadView("home-view.fxml");
    }

    @FXML

    private void showPeliculas() {
        loadView("pelicula-view.fxml");
    }

    @FXML

    private void showVentas() {
        loadView("venta-view.fxml");
    }

    @FXML

    private void showReportes() {
        loadView("reporte-view.fxml");
    }

    @FXML

    private void showSalas() {
        loadView("sala-view.fxml");
    }

    @FXML

    private void showFunciones() {
        loadView("funcion-view.fxml");
    }

    private void loadView(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource
                    ("/com/example/proyecto_final/views/" + fxml));
            Parent view = loader.load();
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            System.err.println("Error de carga de vista (" + fxml + "): " + e.getMessage());
        }
    }
}
