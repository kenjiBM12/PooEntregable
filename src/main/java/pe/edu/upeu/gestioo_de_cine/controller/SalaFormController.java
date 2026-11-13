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

    public static UtilAlerts utilAlerts = new UtilAlerts();

    @FXML
    private Label lblTitulo;
    @FXML
    private TextField txtNumero,txtNombre,txtCapacidad,txtEstado;
    @FXML
    private ComboBox<String> cbTipo;
    private Sala sala;
    private boolean saved = false;

    @FXML
    public void initialize(){
        cbTipo.getItems().addAll("2D","3D","XD","VIP");
    }

    public boolean isSaved(){
        return saved;
    }
    public Sala getSala(){
        return sala;
    }

    private boolean validate(){
        if (txtNombre.getText().isBlank()
                || txtNumero.getText().isBlank()
                || txtCapacidad.getText().isBlank()
                || txtEstado.getText().isBlank()
                || cbTipo.getValue() == null) {
            Alert alert = utilAlerts.mostrarAlerts("",
                    "Completa todos los campos obligatorios.","ERROR");
            alert.show();
            return false;
        }
        try {
            Integer.parseInt(txtNumero.getText().trim());
            Integer.parseInt(txtCapacidad.getText().trim());
        } catch (NumberFormatException e) {
            utilAlerts.mostrarAlerts(
                    "Número inválido",
                    "Ingresa números enteros válidos en número y capacidad.",
                    "WARNING"
            ).showAndWait();
            return false;
        }
        return true;
    }

    private void cerrar(){
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCancel(){
        cerrar();
    }

    @FXML
    private void handleSave(){
        if (validate()){
            if (sala == null) {
                sala = new Sala();
            }
            sala.setNumero(Integer.parseInt(txtNumero.getText().trim()));
            sala.setNombre(txtNombre.getText().trim());
            sala.setCapacidad(Integer.parseInt(txtCapacidad.getText().trim()));
            sala.setEstado(txtEstado.getText().trim());
            sala.setTipo(cbTipo.getValue());
            saved = true;
            cerrar();
        }
    }

    public void setSala(Sala s){
        this.sala = s;
        if (s != null){
            if (lblTitulo != null) {
                lblTitulo.setText("Editar Sala");
            }
            txtNumero.setText(String.valueOf(s.getNumero()));
            txtNombre.setText(s.getNombre());
            txtCapacidad.setText(String.valueOf(s.getCapacidad()));
            txtEstado.setText(s.getEstado());
            if (s.getTipo() != null) {
                cbTipo.setValue(s.getTipo());
            }
        }
    }
}