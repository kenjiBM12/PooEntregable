package pe.edu.upeu.gestioo_de_cine.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pe.edu.upeu.gestioo_de_cine.modelo.Pelicula;
import pe.edu.upeu.gestioo_de_cine.utils.UtilAlerts;

public class PeliculaFormController {

    public static UtilAlerts utilAlerts = new UtilAlerts();

    @FXML
    private Label lblTitulo;
    @FXML
    private TextField txtNumero,txtNombre,txtDuracion,txtClasificacion;
    @FXML
    private ComboBox<String> cbCategoria;
    private Pelicula pelicula;
    private boolean saved = false;

    @FXML
    public void initialize(){
        cbCategoria.getItems().addAll("Accion","Drama","Comedia","Suspenso");
    }

    public boolean isSaved(){
        return saved;
    }
    public Pelicula getPelicula(){
        return pelicula;
    }
    private boolean validate(){
        if (txtNombre.getText().isBlank()
                || txtNumero.getText().isBlank()
                || txtDuracion.getText().isBlank()
                || txtClasificacion.getText().isBlank()
                || cbCategoria.getValue() == null) {
            Alert alert = utilAlerts.mostrarAlerts("",
                    "Completa todos los campos obligatorios.","ERROR");
            alert.show();
            return false;
        }
        try {
            Integer.parseInt(txtNumero.getText().trim());
        } catch (NumberFormatException e) {
            utilAlerts.mostrarAlerts(
                    "Número inválido",
                    "Ingresa un número entero.",
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
    //actions of the views
    @FXML
    private void handleCancel(){
        cerrar();
    }
    @FXML
    private void handleSave(){
        if (validate()){
            if (pelicula == null)
                pelicula = new Pelicula();
            pelicula.setNumero(Integer.parseInt(txtNumero.getText()));
            pelicula.setNombre(txtNombre.getText());
            pelicula.setDuracion(txtDuracion.getText());
            pelicula.setClasificacion(txtClasificacion.getText());
            pelicula.setCategoria(cbCategoria.getValue());
            saved =true;
            cerrar();
        }
    }
    //metodo para enviar los datos al formulario y editar
    public void setPelicula(Pelicula p){
        this.pelicula = p;
        if (p!=null){
            lblTitulo.setText("Editar Pelicula");
            txtNumero.setText(p.getNumero().toString());
            txtNombre.setText(p.getNombre());
            txtDuracion.setText(p.getDuracion());
            txtClasificacion.setText(p.getClasificacion());
            cbCategoria.setValue(p.getCategoria());
        }
    }


}

