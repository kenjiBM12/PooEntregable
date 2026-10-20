package com.example.proyecto_final.controller;

import com.example.proyecto_final.modelo.*;
import com.example.proyecto_final.utils.UtilAlerts;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;


public class PeliculaFormController {
    private final UtilAlerts utilAlerts = new UtilAlerts();
    private Pelicula registro;
    private boolean saved = false;
    @FXML private Label lblTitulo;
    @FXML private TextField txtTitulo, txtGenero, txtDuracion, txtClasificacion;

    @FXML
    public void initialize() {

    }

    public boolean isSaved() {
        return saved;
    }

    public Pelicula getPelicula() {
        return registro;
    }

    public void setPelicula(Pelicula value) {
        registro = value;
        saved = false;
        lblTitulo.setText(value == null ? "Nuevo registro" : "Editar registro");
        if (value != null) {
            txtTitulo.setText(value.getTitulo());
            txtGenero.setText(value.getGenero());
            txtDuracion.setText(String.valueOf(value.getDuracion()));
            txtClasificacion.setText(value.getClasificacion());
        }
    }

    private boolean validate() {
        if (txtTitulo.getText().isBlank() || txtGenero.getText().isBlank()
                || txtClasificacion.getText().isBlank()) {
            return error("Complete título, género y clasificación");
        }
        try {
            if (Integer.parseInt(txtDuracion.getText().trim()) <= 0) {
                return error("La duración debe ser mayor que cero");
            }
        } catch (NumberFormatException e) {
            return error("Ingrese una duración entera en minutos");
        }
        return true;
    }

    @FXML
    private void handleSave() {
        if (!validate()) {
            return;
        }
        if (registro == null) {
            registro = new Pelicula();
        }
        registro.setTitulo(txtTitulo.getText().trim());
        registro.setGenero(txtGenero.getText().trim());
        registro.setDuracion(Integer.parseInt(txtDuracion.getText().trim()));
        registro.setClasificacion(txtClasificacion.getText().trim());
        saved = true;
        cerrar();
    }

    @FXML
    private void handleCancel() {
        cerrar();
    }

    private void cerrar() {
        Stage stage = (Stage) lblTitulo.getScene().getWindow();
        stage.close();
    }

    private boolean error(String mensaje) {
        utilAlerts.mostrarAlerts("Datos incorrectos", mensaje, "WARNING").showAndWait();
        return false;
    }
}
