package com.example.proyecto_final.controller;

import com.example.proyecto_final.implementacion.FuncionDaoImpl;
import com.example.proyecto_final.modelo.Funcion;
import com.example.proyecto_final.modelo.Venta;
import com.example.proyecto_final.utils.UtilAlerts;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;

import java.util.List;

public class VentaFormController {

    public static UtilAlerts utilAlerts = new UtilAlerts();

    @FXML
    private Label lblTitulo;

    @FXML
    private ComboBox<String> cbFuncion;

    @FXML
    private TextField txtCliente, txtCantidad, txtFechaVenta;

    private Venta venta;
    private boolean saved = false;
    private List<Funcion> funciones;

    @FXML
    public void initialize() {
        funciones = new FuncionDaoImpl().listAll();
        for (Funcion f : funciones) {
            cbFuncion.getItems().add(f.getIdfuncion() + " - "
                    + f.getPelicula().getTitulo() + " - " + f.getHorario());
        }
    }

    public boolean isSaved() {
        return saved;
    }

    public Venta getVenta() {
        return venta;
    }

    private boolean validate() {
        if (cbFuncion.getValue() == null
                || txtCliente.getText().isEmpty()
                || txtCantidad.getText().isEmpty()
                || txtFechaVenta.getText().isEmpty()) {
            Alert alert = utilAlerts.mostrarAlerts("",
                    "Función, cliente, cantidad y fecha son obligatorios",
                    "ERROR");
            alert.show();
            return false;
        }
        return true;
    }

    private void cerrar() {
        Stage stage = (Stage) txtCliente.getScene().getWindow();
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
                int cantidad = Integer.parseInt(txtCantidad.getText());
                LocalDate fechaVenta = LocalDate.parse(txtFechaVenta.getText());
                Funcion funcion = funciones.get(cbFuncion.getSelectionModel().getSelectedIndex());
                if (venta == null) {
                    venta = new Venta();
                }
                venta.setFuncion(funcion);
                venta.setCliente(txtCliente.getText());
                venta.setFechaVenta(fechaVenta);
                venta.setCantidadAsientos(cantidad);
                venta.setTotalPagar(cantidad * funcion.getPrecio());
                saved = true;
                cerrar();
            } catch (NumberFormatException e) {
                Alert alert = utilAlerts.mostrarAlerts("",
                        "La cantidad debe ser un número entero",
                        "ERROR");
                alert.show();
            } catch (Exception e) {
                Alert alert = utilAlerts.mostrarAlerts("",
                        "La fecha debe tener el formato aaaa-mm-dd",
                        "ERROR");
                alert.show();
            }
        }
    }

    public void setVenta(Venta v) {
        this.venta = v;
        if (v != null) {
            lblTitulo.setText("Editar venta");
            for (int i = 0; i < funciones.size(); i++) {
                if (funciones.get(i).getIdfuncion() == v.getFuncion().getIdfuncion()) {
                    cbFuncion.getSelectionModel().select(i);
                }
            }
            txtCliente.setText(v.getCliente());
            txtCantidad.setText(String.valueOf(v.getCantidadAsientos()));
            txtFechaVenta.setText(String.valueOf(v.getFechaVenta()));
        }
    }
}
