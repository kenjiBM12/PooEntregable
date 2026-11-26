package pe.edu.upeu.gestioo_de_cine.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pe.edu.upeu.gestioo_de_cine.modelo.Pelicula;
import pe.edu.upeu.gestioo_de_cine.utils.Duracion;
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
        //el codigo de la pelicula debe ser un numero positivo
        if (numero <= 0) {
            utilAlerts.mostrarAlerts("Número inválido",
                    "El número de la película debe ser mayor que cero.", "WARNING").showAndWait();
            return false;
        }
        //la duracion debe ser mayor que cero: "120" minutos o "1:50" horas
        if (!duracionValida(txtDuracion.getText())) {
            utilAlerts.mostrarAlerts("Duración inválida",
                    "Ingresa la duración en minutos (ejemplo: 120) o como horas:minutos (ejemplo: 1:50).",
                    "WARNING").showAndWait();
            return false;
        }

        return true;
    }

    //revisa que la duracion sea un numero positivo, con o sin "h" al final
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
            pelicula.setNumero(Integer.parseInt(txtNumero.getText().trim()));
            pelicula.setTitulo(txtTitulo.getText().trim());
            //MEJORA 1: la duracion se guarda SIEMPRE en minutos. Si el usuario
            //escribe "1:50" queda 110; si escribe "95 min" queda 95.
            pelicula.setDuracion(Duracion.normalizar(txtDuracion.getText()));
            pelicula.setClasificacion(txtClasificacion.getText().trim());
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
            txtTitulo.setText(p.getTitulo());
            //MEJORA 1: las peliculas viejas se muestran ya en minutos
            txtDuracion.setText(Duracion.normalizar(p.getDuracion()));
            txtClasificacion.setText(p.getClasificacion());
            cbCategoria.setValue(p.getCategoria());
        }
    }


}

