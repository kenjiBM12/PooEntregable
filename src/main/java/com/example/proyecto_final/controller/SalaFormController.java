package com.example.proyecto_final.controller;

import com.example.proyecto_final.modelo.*;
import com.example.proyecto_final.utils.UtilAlerts;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class SalaFormController {
    private final UtilAlerts utilAlerts = new UtilAlerts();
    private Sala registro;

    private boolean saved = false;

    @FXML private Label lblTitulo;

    @FXML private TextField txtNombre, txtTipo, txtCapacidad;

    @FXML

    public void initialize() {

    }

    public boolean isSaved() {
        return saved;
    }

    public Sala getSala() {
        return registro;
    }

    public void setSala(Sala value) {
        registro = value;
        saved = false;
        lblTitulo.setText(value == null ? "Nuevo registro" : "Editar registro");
        if (value != null) {
            txtNombre.setText(value.getNombre());
            txtTipo.setText(value.getTipo());
            txtCapacidad.setText(String.valueOf(value.getCapacidad()));
        }
    }

    private boolean validate() {
        if (txtNombre.getText().isBlank() || txtTipo.getText().isBlank()) {
            return error("Complete nombre y tipo");
        }
        try {

            if (Integer.parseInt(txtCapacidad.getText().trim()) <= 0) {
                return error("La capacidad debe ser mayor que cero");
            }
        } catch (NumberFormatException e) {
            return error("Ingrese una capacidad entera");
        }
        return true;
    }

    @FXML

    private void handleSave() {
        if (!validate()) {
            return;
        }
        if (registro == null) {
            registro = new Sala();
        }
        registro.setNombre(txtNombre.getText().trim());
        registro.setTipo(txtTipo.getText().trim());
        registro.setCapacidad(Integer.parseInt(txtCapacidad.getText().trim()));
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
