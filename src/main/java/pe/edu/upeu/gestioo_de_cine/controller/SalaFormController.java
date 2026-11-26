package pe.edu.upeu.gestioo_de_cine.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pe.edu.upeu.gestioo_de_cine.modelo.Sala;
import pe.edu.upeu.gestioo_de_cine.utils.UtilAlerts;

public class SalaFormController {

    private final UtilAlerts utilAlerts = new UtilAlerts();

    @FXML
    private Label lblTitulo;

    @FXML
    private TextField txtNumero;
    @FXML
    private TextField txtFilas;
    @FXML
    private TextField txtColumnas;
    @FXML
    private Label lblCapacidad;

    @FXML
    private ComboBox<String> cbTipoSala, cbEstado;

    private Sala sala;
    private boolean saved = false;

    @FXML
    public void initialize() {
        cbTipoSala.getItems().addAll("2D", "3D", "XD", "VIP");
        cbEstado.getItems().addAll("Activa", "Inactiva");
        cbEstado.setValue("Activa");

        txtFilas.textProperty().addListener((obs, viejo, nuevo) -> actualizarCapacidad());
        txtColumnas.textProperty().addListener((obs, viejo, nuevo) -> actualizarCapacidad());
        actualizarCapacidad();
    }

    public boolean isSaved() {
        return saved;
    }

    public Sala getSala() {
        return sala;
    }

    private void actualizarCapacidad() {
        long total = 0;
        try {
            long filas = Long.parseLong(txtFilas.getText().trim());
            long columnas = Long.parseLong(txtColumnas.getText().trim());
            if (filas > 0 && columnas > 0) {
                total = filas * columnas;
            }
        } catch (NumberFormatException e) {
            total = 0;
        }
        lblCapacidad.setText("Capacidad: " + total + " butacas");
    }

    private boolean validate() {
        if (txtNumero.getText().isBlank()
                || txtFilas.getText().isBlank()
                || txtColumnas.getText().isBlank()
                || cbEstado.getValue() == null
                || cbTipoSala.getValue() == null) {
            Alert alert = utilAlerts.mostrarAlerts(
                    "",
                    "Completa todos los campos obligatorios.",
                    "ERROR"
            );
            alert.showAndWait();
            return false;
        }

        int numero;
        int filas;
        int columnas;

        try {
            numero = Integer.parseInt(txtNumero.getText().trim());
            filas = Integer.parseInt(txtFilas.getText().trim());
            columnas = Integer.parseInt(txtColumnas.getText().trim());
        } catch (NumberFormatException e) {
            utilAlerts.mostrarAlerts(
                    "Número inválido",
                    "Ingresa números enteros válidos en número, filas y columnas.",
                    "WARNING"
            ).showAndWait();
            return false;
        }

        if (numero <= 0 || filas <= 0 || columnas <= 0) {
            utilAlerts.mostrarAlerts(
                    "Datos inválidos",
                    "El número de sala, las filas y las columnas deben ser mayores que cero.",
                    "WARNING"
            ).showAndWait();
            return false;
        }

        if (filas > 100 || columnas > 100) {
            utilAlerts.mostrarAlerts(
                    "Cuadricula muy grande",
                    "Las filas y las columnas no pueden pasar de 100.",
                    "WARNING"
            ).showAndWait();
            return false;
        }

        return true;
    }

    private void cerrar() {
        Stage stage = (Stage) cbTipoSala.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCancel() {
        cerrar();
    }

    @FXML
    private void handleSave() {
        if (!validate()) {
            return;
        }

        if (sala == null) {
            sala = new Sala();
        }

        int filas = Integer.parseInt(txtFilas.getText().trim());
        int columnas = Integer.parseInt(txtColumnas.getText().trim());

        sala.setNumero(Integer.parseInt(txtNumero.getText().trim()));
        sala.setTipoSala(cbTipoSala.getValue());
        sala.setFilas(filas);
        sala.setColumnas(columnas);
        sala.setCapacidad(filas * columnas);
        sala.setEstado(cbEstado.getValue().trim());

        saved = true;
        cerrar();
    }

    public void setSala(Sala sala) {
        this.sala = sala;

        if (sala != null) {
            lblTitulo.setText("Editar Sala");
            txtNumero.setText(String.valueOf(sala.getNumero()));
            if (sala.getFilas() > 0) {
                txtFilas.setText(String.valueOf(sala.getFilas()));
            }
            if (sala.getColumnas() > 0) {
                txtColumnas.setText(String.valueOf(sala.getColumnas()));
            }
            cbEstado.setValue(sala.getEstado());

            if (sala.getTipoSala() != null) {
                cbTipoSala.setValue(sala.getTipoSala());
            }
            actualizarCapacidad();
        }
    }
}
