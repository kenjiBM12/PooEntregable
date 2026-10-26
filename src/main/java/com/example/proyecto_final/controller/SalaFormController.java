package com.example.proyecto_final.controller;

import com.example.proyecto_final.modelo.Sala;
import com.example.proyecto_final.utils.UtilAlerts;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SalaFormController {

    public static UtilAlerts utilAlerts = new UtilAlerts();

    @FXML
    private Label lblTitulo;

    @FXML
    private TextField txtNombre, txtTipo, txtCapacidad;

    private Sala sala;
    private boolean saved = false;

    public boolean isSaved() {
        return saved;
    }

    public Sala getSala() {
        return sala;
    }

    private boolean validate() {
        if (txtNombre.getText().isEmpty()
                || txtTipo.getText().isEmpty()
                || txtCapacidad.getText().isEmpty()) {
            Alert alert = utilAlerts.mostrarAlerts(
                    "",
                    "Nombre, tipo y capacidad son obligatorios",
                    "ERROR");
            alert.show();
            return false;
        }
        return true;
    }

    private void cerrar() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCancel() {
        cerrar();
    }

    @FXML
    private void handleSave() {
        if (validate()) {
            try {
                int capacidad = Integer.parseInt(txtCapacidad.getText());
                if (sala == null) {
                    sala = new Sala();
                }
                sala.setNombre(txtNombre.getText());
                sala.setTipo(txtTipo.getText());
                sala.setCapacidad(capacidad);
                saved = true;
                cerrar();
            } catch (NumberFormatException e) {
                Alert alert = utilAlerts.mostrarAlerts(
                        "",
                        "La capacidad debe ser un número entero",
                        "ERROR");
                alert.show();
            }
        }
    }

    public void setSala(Sala s) {
        sala = s;
        saved = false;
        if (s != null) {
            lblTitulo.setText("Editar sala");
            txtNombre.setText(s.getNombre());
            txtTipo.setText(s.getTipo());
            txtCapacidad.setText(String.valueOf(s.getCapacidad()));
        } else {
            lblTitulo.setText("Nueva sala");
        }
    }
}
