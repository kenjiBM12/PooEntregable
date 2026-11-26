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
    private TextField txtNumero,txtTitulo,txtDuracion,txtClasificacion;
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
        if (txtTitulo.getText().isBlank()
                || txtNumero.getText().isBlank()
                || txtDuracion.getText().isBlank()
                || txtClasificacion.getText().isBlank()
                || cbCategoria.getValue() == null) {
            Alert alert = utilAlerts.mostrarAlerts("",
                    "Completa todos los campos obligatorios.","ERROR");
            alert.showAndWait();
            return false;
        }
        int numero;
        try {
            numero = Integer.parseInt(txtNumero.getText().trim());
        } catch (NumberFormatException e) {
            utilAlerts.mostrarAlerts(
                    "Número inválido",
                    "Ingresa un número entero.",
                    "WARNING"
            ).showAndWait();
            return false;
        }
        if (numero <= 0) {
            utilAlerts.mostrarAlerts("Número inválido",
                    "El número de la película debe ser mayor que cero.", "WARNING").showAndWait();
            return false;
        }
        if (!duracionValida(txtDuracion.getText())) {
            utilAlerts.mostrarAlerts("Duración inválida",
                    "Ingresa la duración en minutos (ejemplo: 120) o como horas:minutos (ejemplo: 1:50).",
                    "WARNING").showAndWait();
            return false;
        }

        return true;
    }

    private boolean duracionValida(String texto) {
        String valor = texto.trim().replace("h", "").replace("H", "").trim();
        if (valor.matches("[0-9]{1,3}")) {
            return Integer.parseInt(valor) > 0;
        }
        if (valor.matches("[0-9]{1,2}:[0-5][0-9]")) {
            String[] partes = valor.split(":");
            return Integer.parseInt(partes[0]) > 0 || Integer.parseInt(partes[1]) > 0;
        }
        return false;
    }
    private void cerrar(){
        Stage stage = (Stage) txtTitulo.getScene().getWindow();
        stage.close();
    }
    @FXML
    private void handleCancel(){
        cerrar();
    }
    @FXML
    private void handleSave(){
        if (validate()){
            if (pelicula == null)
                pelicula = new Pelicula();
            pelicula.setNumero(Integer.parseInt(txtNumero.getText().trim()));
            pelicula.setTitulo(txtTitulo.getText().trim());
            pelicula.setDuracion(txtDuracion.getText().trim());
            pelicula.setClasificacion(txtClasificacion.getText().trim());
            pelicula.setCategoria(cbCategoria.getValue());
            saved =true;
            cerrar();
        }
    }
    public void setPelicula(Pelicula p){
        this.pelicula = p;
        if (p!=null){
            lblTitulo.setText("Editar Pelicula");
            txtNumero.setText(p.getNumero().toString());
            txtTitulo.setText(p.getTitulo());
            txtDuracion.setText(p.getDuracion());
            txtClasificacion.setText(p.getClasificacion());
            cbCategoria.setValue(p.getCategoria());
        }
    }

}
