package com.example.proyecto_final.controller;

import com.example.proyecto_final.modelo.Pelicula;
import com.example.proyecto_final.utils.UtilAlerts;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PeliculaFormController {

    public static UtilAlerts utilAlerts = new UtilAlerts();

    @FXML
    private Label lblTitulo;

    @FXML
    private TextField txtTitulo, txtGenero, txtDuracion, txtClasificacion;

    private Pelicula pelicula;
    private boolean saved = false;

    public boolean isSaved() {
        return saved;
    }

    public Pelicula getPelicula() {
        return pelicula;
    }

    private boolean validate() {
        if (txtTitulo.getText().isEmpty()
                || txtGenero.getText().isEmpty()
                || txtDuracion.getText().isEmpty()
                || txtClasificacion.getText().isEmpty()) {
            Alert alert = utilAlerts.mostrarAlerts(
                    "",
                    "Título, género, duración y clasificación son obligatorios",
                    "ERROR");
            alert.show();
            return false;
        }
        return true;
    }

    private void cerrar() {
        Stage stage = (Stage) txtTitulo.getScene().getWindow();
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
                int duracion = Integer.parseInt(txtDuracion.getText());
                if (pelicula == null) {
                    pelicula = new Pelicula();
                }
                pelicula.setTitulo(txtTitulo.getText());
                pelicula.setGenero(txtGenero.getText());
                pelicula.setDuracion(duracion);
                pelicula.setClasificacion(txtClasificacion.getText());
                saved = true;
                cerrar();
            } catch (NumberFormatException e) {
                Alert alert = utilAlerts.mostrarAlerts(
                        "",
                        "La duración debe ser un número entero en minutos",
                        "ERROR");
                alert.show();
            }
        }
    }

    public void setPelicula(Pelicula p) {
        pelicula = p;
        saved = false;
        if (p != null) {
            lblTitulo.setText("Editar película");
            txtTitulo.setText(p.getTitulo());
            txtGenero.setText(p.getGenero());
            txtDuracion.setText(String.valueOf(p.getDuracion()));
            txtClasificacion.setText(p.getClasificacion());
        } else {
            lblTitulo.setText("Nueva película");
        }
    }
}
