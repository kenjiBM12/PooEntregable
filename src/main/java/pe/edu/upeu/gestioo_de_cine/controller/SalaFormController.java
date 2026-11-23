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
    private TextField txtCapacidad;

    @FXML
    private ComboBox<String> cbTipoSala, cbEstado;

    private Sala sala;
    private boolean saved = false;

    @FXML
    public void initialize() {
        cbTipoSala.getItems().addAll("2D", "3D", "XD", "VIP");
        cbEstado.getItems().addAll("Activa", "Inactiva");

    }

    public boolean isSaved() {
        return saved;
    }

    public Sala getSala() {
        return sala;
    }

    private boolean validate() {
        if (txtNumero.getText().isBlank()
                || txtCapacidad.getText().isBlank()
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
        int capacidad;

        try {
            numero = Integer.parseInt(txtNumero.getText().trim());
            capacidad = Integer.parseInt(txtCapacidad.getText().trim());
        } catch (NumberFormatException e) {
            utilAlerts.mostrarAlerts(
                    "Número inválido",
                    "Ingresa números enteros válidos en número y capacidad.",
                    "WARNING"
            ).showAndWait();
            return false;
        }

        if (numero <= 0 || capacidad <= 0) {
            utilAlerts.mostrarAlerts(
                    "Datos inválidos",
                    "El número de sala y la capacidad deben ser mayores que cero.",
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

        sala.setNumero(Integer.parseInt(txtNumero.getText().trim()));
        sala.setTipoSala(cbTipoSala.getValue());
        sala.setCapacidad(Integer.parseInt(txtCapacidad.getText().trim()));
        sala.setEstado(cbEstado.getValue().trim());

        saved = true;
        cerrar();
    }

    public void setSala(Sala sala) {
        this.sala = sala;

        if (sala != null) {
            lblTitulo.setText("Editar Sala");
            txtNumero.setText(String.valueOf(sala.getNumero()));
            txtCapacidad.setText(String.valueOf(sala.getCapacidad()));
            cbEstado.setValue(sala.getEstado());

            if (sala.getTipoSala() != null) {
                cbTipoSala.setValue(sala.getTipoSala());
            }
        }
    }
}