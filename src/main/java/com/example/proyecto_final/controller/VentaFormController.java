package com.example.proyecto_final.controller;

import com.example.proyecto_final.modelo.*;
import com.example.proyecto_final.utils.UtilAlerts;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import com.example.proyecto_final.implementacion.*;
import javafx.collections.FXCollections;
import javafx.util.StringConverter;

public class VentaFormController {
    private final UtilAlerts utilAlerts = new UtilAlerts();
    private Venta registro;
    private boolean saved = false;
    @FXML private Label lblTitulo;
    @FXML private ComboBox<Funcion> cbFuncion;
    @FXML private TextField txtCliente, txtCantidad;

    @FXML
    public void initialize() {
        cbFuncion.setItems(FXCollections.observableArrayList(new FuncionDaoImpl().listAll()));
        cbFuncion.setConverter(new StringConverter<>() {
            public String toString(Funcion f) {
                return f == null ? "" : f.getIdfuncion() + " - " + f.getPelicula().getTitulo()
                        + " | " + f.getSala().getNombre() + " | " + f.getHorario()
                        + " | S/ " + f.getPrecio();
            }
            public Funcion fromString(String text) { return null; }
        });
    }

    public boolean isSaved() {
        return saved;
    }

    public Venta getVenta() {
        return registro;
    }

    public void setVenta(Venta value) {
        registro = value;
        saved = false;
        lblTitulo.setText(value == null ? "Nuevo registro" : "Editar registro");
        if (value != null) {
            cbFuncion.getItems().stream()
                    .filter(f -> f.getIdfuncion() == value.getFuncion().getIdfuncion())
                    .findFirst().ifPresent(f -> cbFuncion.setValue(f));
            txtCliente.setText(value.getCliente());
            txtCantidad.setText(String.valueOf(value.getCantidadAsientos()));
        }
    }

    private boolean validate() {
        if (cbFuncion.getValue() == null || txtCliente.getText().isBlank()) {
            return error("Seleccione función e ingrese el cliente");
        }
        try {
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            double precio = cbFuncion.getValue().getPrecio();
            if (cantidad <= 0 || precio <= 0 || !Double.isFinite(precio * cantidad)) {
                return error("Ingrese cantidad y precio de función válidos");
            }
        } catch (NumberFormatException e) {
            return error("Ingrese una cantidad entera");
        }
        return true;
    }

    @FXML
    private void handleSave() {
        if (!validate()) {
            return;
        }
        if (registro == null) {
            registro = new Venta();
        }
        registro.setFuncion(cbFuncion.getValue());
        registro.setCliente(txtCliente.getText().trim());
        registro.setCantidadAsientos(Integer.parseInt(txtCantidad.getText().trim()));
        registro.setTotalPagar(registro.getCantidadAsientos() * registro.getFuncion().getPrecio());
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
