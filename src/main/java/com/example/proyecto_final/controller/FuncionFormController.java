package com.example.proyecto_final.controller;

import com.example.proyecto_final.modelo.*;
import com.example.proyecto_final.utils.UtilAlerts;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import com.example.proyecto_final.implementacion.*;
import javafx.collections.FXCollections;
import javafx.util.StringConverter;

public class FuncionFormController {
    private final UtilAlerts utilAlerts = new UtilAlerts();
    private Funcion registro;
    private boolean saved = false;
    @FXML private Label lblTitulo;
    @FXML private ComboBox<Pelicula> cbPelicula;
    @FXML private ComboBox<Sala> cbSala;
    @FXML private TextField txtHorario, txtPrecio;

    @FXML
    public void initialize() {
        cbPelicula.setItems(FXCollections.observableArrayList(new PeliculaDaoImpl().listAll()));
        cbSala.setItems(FXCollections.observableArrayList(new SalaDaoImpl().listAll()));
        cbPelicula.setConverter(new StringConverter<>() {
            public String toString(Pelicula p) { return p == null ? "" : p.getIdpelicula() + " - " + p.getTitulo(); }
            public Pelicula fromString(String text) { return null; }
        });
        cbSala.setConverter(new StringConverter<>() {
            public String toString(Sala s) { return s == null ? "" : s.getIdsala() + " - " + s.getNombre(); }
            public Sala fromString(String text) { return null; }
        });
    }

    public boolean isSaved() {
        return saved;
    }

    public Funcion getFuncion() {
        return registro;
    }

    public void setFuncion(Funcion value) {
        registro = value;
        saved = false;
        lblTitulo.setText(value == null ? "Nuevo registro" : "Editar registro");
        if (value != null) {
            cbPelicula.getItems().stream()
                    .filter(p -> p.getIdpelicula() == value.getPelicula().getIdpelicula())
                    .findFirst().ifPresent(p -> cbPelicula.setValue(p));
            cbSala.getItems().stream()
                    .filter(s -> s.getIdsala() == value.getSala().getIdsala())
                    .findFirst().ifPresent(s -> cbSala.setValue(s));
            txtHorario.setText(value.getHorario());
            txtPrecio.setText(String.valueOf(value.getPrecio()));
        }
    }

    private boolean validate() {
        if (cbPelicula.getValue() == null || cbSala.getValue() == null
                || txtHorario.getText().isBlank()) {
            return error("Seleccione película, sala y complete el horario");
        }
        try {
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            if (!Double.isFinite(precio) || precio <= 0) {
                return error("El precio debe ser mayor que cero");
            }
        } catch (NumberFormatException e) {
            return error("Ingrese un precio numérico");
        }
        return true;
    }

    @FXML
    private void handleSave() {
        if (!validate()) {
            return;
        }
        if (registro == null) {
            registro = new Funcion();
        }
        registro.setPelicula(cbPelicula.getValue());
        registro.setSala(cbSala.getValue());
        registro.setHorario(txtHorario.getText().trim());
        registro.setPrecio(Double.parseDouble(txtPrecio.getText().trim()));
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
