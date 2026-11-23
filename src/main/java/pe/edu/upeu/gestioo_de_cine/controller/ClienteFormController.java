package pe.edu.upeu.gestioo_de_cine.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pe.edu.upeu.gestioo_de_cine.modelo.Cliente;
import pe.edu.upeu.gestioo_de_cine.utils.UtilAlerts;

public class ClienteFormController {

    public static UtilAlerts utilAlerts = new UtilAlerts();

    @FXML
    private Label lblTitulo;
    @FXML
    private TextField txtNombre, txtApellidos, txtDni, txtEmail, txtTelefono;
    @FXML
    private ComboBox<String> cbGenero;

    private Cliente cliente;
    private boolean saved = false;

    @FXML
    public void initialize() {
        cbGenero.getItems().addAll("Masculino", "Femenino");
    }

    public boolean isSaved() {
        return saved;
    }

    public Cliente getCliente() {
        return cliente;
    }

    private boolean validate() {
        if (txtNombre.getText().isBlank()
                || txtApellidos.getText().isBlank()
                || txtDni.getText().isBlank()
                || txtEmail.getText().isBlank()
                || cbGenero.getValue() == null) {
            Alert alert = utilAlerts.mostrarAlerts("",
                    "Completa todos los campos obligatorios.", "ERROR");
            alert.showAndWait();
            return false;
        }
        //el DNI debe tener exactamente 8 numeros, no letras ni espacios
        String dni = txtDni.getText().trim();
        if (!dni.matches("[0-9]{8}")) {
            utilAlerts.mostrarAlerts("DNI invalido",
                    "El DNI debe tener 8 numeros (sin letras ni espacios).", "WARNING").showAndWait();
            return false;
        }
        return true;
    }

    private void cerrar() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }

    //actions of the views
    @FXML
    private void handleCancel() {
        cerrar();
    }

    @FXML
    private void handleSave() {
        if (validate()) {
            if (cliente == null)
                cliente = new Cliente();
            cliente.setNombre(txtNombre.getText().trim());
            cliente.setApellidos(txtApellidos.getText().trim());
            cliente.setDni(txtDni.getText().trim());
            cliente.setEmail(txtEmail.getText().trim());
            cliente.setGenero(cbGenero.getValue());
            //el telefono no es obligatorio, pero se guarda si lo escriben
            cliente.setTelefono(txtTelefono.getText().trim());
            saved = true;
            cerrar();
        }
    }

    //metodo para enviar los datos al formulario y editar
    public void setCliente(Cliente c) {
        this.cliente = c;
        if (c != null) {
            lblTitulo.setText("Editar Cliente");
            txtNombre.setText(c.getNombre());
            txtApellidos.setText(c.getApellidos());
            txtDni.setText(c.getDni());
            txtEmail.setText(c.getEmail());
            cbGenero.setValue(c.getGenero());
            txtTelefono.setText(c.getTelefono() != null ? c.getTelefono() : "");
        }
    }
}