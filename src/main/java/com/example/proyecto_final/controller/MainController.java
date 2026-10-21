// package organiza las clases; no es la ruta del archivo SQLite.
package com.example.proyecto_final.controller;

// import permite utilizar clases de otros paquetes sin escribir su nombre completo.
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;

/**
 * CONTROLADOR PRINCIPAL: atiende los botones del menú lateral.
 * Carga cada archivo FXML y lo coloca en contentArea sin abrir otra ventana.
 */
public class MainController {

    @FXML
    private StackPane contentArea;

    @FXML
    /**
     * Se ejecuta al terminar de cargar main-view.fxml y muestra Inicio.
     */
    public void initialize() {
        showHome();
    }

    @FXML
    /**
     * Muestra la portada home-view.fxml dentro de la ventana principal.
     */
    private void showHome() {
        loadView("home-view.fxml");
    }

    @FXML
    /**
     * Muestra la tabla de películas; el nombre viene de la adaptación del hotel.
     */
    private void showClient() {
        loadView("pelicula-view.fxml");
    }

    @FXML
    /**
     * Muestra la pantalla de ventas de entradas.
     */
    private void showVentas() {
        loadView("venta-view.fxml"); // Vista para comprar entradas
    }

    @FXML
    /**
     * Muestra la pantalla del reporte; al cargarla se consulta la fecha de hoy.
     */
    private void showReportes() {
        loadView("reporte-view.fxml"); // Vista para ver las ventas del día, totales, etc.
    }

    @FXML
    /**
     * Muestra la tabla de salas y sus botones de mantenimiento.
     */
    private void showSalas() {
        loadView("sala-view.fxml");
    }

    @FXML
    /**
     * Muestra la tabla de funciones y sus botones de mantenimiento.
     */
    private void showFunciones() {
        loadView("funcion-view.fxml");
    }

    // Método que busca y carga dinámicamente las vistas
    /**
     * Localiza un FXML en resources, lo carga y sustituye el contenido central.
     */
    private void loadView(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/proyecto_final/views/" + fxml));
            Parent view = loader.load();
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            System.err.println("Error de carga de vista (" + fxml + "): " + e.getMessage());
        }
    }
}
