package pe.edu.upeu.gestioo_de_cine.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pe.edu.upeu.gestioo_de_cine.HelloApplication;
import pe.edu.upeu.gestioo_de_cine.implementacion.PeliculaDaoImpl;
import pe.edu.upeu.gestioo_de_cine.interfaces.IPeliculaDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Pelicula;
import pe.edu.upeu.gestioo_de_cine.modelo.UserSession;
import pe.edu.upeu.gestioo_de_cine.utils.UtilAlerts;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PeliculaController {

    private static UtilAlerts utilAlert = new UtilAlerts();

    @FXML
    private TableView<Pelicula> tablePelicula;
    @FXML
    private TableColumn<Pelicula,Number> ColNumero;
    @FXML
    private TableColumn<Pelicula, Integer> ColNumeroPelicula;
    @FXML
    private TableColumn<Pelicula,Integer> ColId;
    @FXML
    private TableColumn<Pelicula,String> ColTitulo;
    @FXML
    private TableColumn<Pelicula,String> ColDuracion;
    @FXML
    private TableColumn<Pelicula,String> ColCategoria;
    @FXML
    private TableColumn<Pelicula,String> ColClasificacion;

    private final IPeliculaDao peliculaDao = new PeliculaDaoImpl();
    private ObservableList<Pelicula> listaPelicula;
    @FXML
    private TextField txtBuscar;
    private List<Pelicula> todasLasPeliculas = new ArrayList<>();

    @FXML
    public void initialize(){
        ColNumero.setCellFactory(colum -> new TableCell<>(){
            @Override
            protected void updateItem(Number item, boolean empty){
                super.updateItem(item,empty);
                if (empty){
                    setText(null);
                }else{
                    setText(String.valueOf(getIndex() +1));
                }
            }
        });
        ColNumeroPelicula.setCellValueFactory(
               cellData -> cellData.getValue()
                       .numeroProperty().asObject());

        ColId.setCellValueFactory(
                cellData -> cellData.getValue()
                        .idpeliculaProperty().asObject());
        ColTitulo.setCellValueFactory(
                cellData -> cellData.getValue()
                        .tituloProperty());
        ColDuracion.setCellValueFactory(
                cellData -> cellData.getValue()
                        .duracionProperty());
        ColCategoria.setCellValueFactory(
                cellData -> cellData.getValue()
                        .categoriaProperty());
        ColClasificacion.setCellValueFactory(
                cellData -> cellData.getValue()
                        .clasificacionProperty());
        cargarDatos();
    }
    private void cargarDatos(){
        todasLasPeliculas = peliculaDao.listAll();
        listaPelicula = FXCollections.observableArrayList();
        tablePelicula.setItems(listaPelicula);
        filtrar();
    }

    @FXML
    private void handleBuscar(){
        filtrar();
    }

    private void filtrar(){
        String texto = txtBuscar != null ? txtBuscar.getText().trim().toLowerCase() : "";
        listaPelicula.clear();
        for (Pelicula p : todasLasPeliculas){
            if (texto.isEmpty() || contiene(p.getTitulo(), texto)
                    || contiene(p.getCategoria(), texto)){
                listaPelicula.add(p);
            }
        }
    }

    private boolean contiene(String valor, String texto){
        return valor != null && valor.toLowerCase().contains(texto);
    }

    private boolean sinPermiso(){
        if (UserSession.esAdmin()){
            return false;
        }
        utilAlert.mostrarAlerts("Sin permiso",
                "Solo el administrador puede administrar peliculas.", "WARNING").showAndWait();
        return true;
    }

    @FXML
    private void handleDelete(){
        if (sinPermiso()) return;
        Pelicula selectId = tablePelicula.getSelectionModel().getSelectedItem();
        if (selectId != null){
            int funciones;
            int ventas;
            try {
                funciones = peliculaDao.contarFunciones(selectId.getidpelicula());
                ventas = peliculaDao.contarVentas(selectId.getidpelicula());
            } catch (RuntimeException e) {
                System.out.println("Error al revisar la pelicula: " + e.getMessage());
                utilAlert.mostrarAlerts("Error",
                        "No se pudo revisar la pelicula: " + e.getMessage(),
                        "ERROR").showAndWait();
                return;
            }
            if (ventas > 0){
                utilAlert.mostrarAlerts("No se puede eliminar",
                        "La pelicula \"" + selectId.getTitulo() + "\" tiene "
                                + ventas + " boleto(s) vendido(s).\n"
                                + "No se elimina para conservar el historial de ventas.",
                        "WARNING").showAndWait();
                return;
            }
            String mensaje;
            if (funciones > 0){
                mensaje = "La pelicula \"" + selectId.getTitulo() + "\" tiene "
                        + funciones + " funcion(es) programada(s)\n"
                        + "y se van a eliminar tambien.\n\nEstas seguro de continuar?";
            } else {
                mensaje = "Estas seguro de eliminar la pelicula: " + selectId.getTitulo() + "?";
            }
            Alert confirm = utilAlert.mostrarAlerts("Confirmar eliminacion", mensaje, "CONFIRMATION");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK){
                try {
                    if (peliculaDao.delete(selectId)){
                        cargarDatos();
                    }else{
                        utilAlert.mostrarAlerts("No se pudo eliminar",
                                "La pelicula no se elimino.", "ERROR").showAndWait();
                    }
                } catch (RuntimeException e) {
                    System.out.println("Error al eliminar pelicula: " + e.getMessage());
                    utilAlert.mostrarAlerts("No se pudo eliminar",
                            e.getMessage(), "ERROR").showAndWait();
                }
            }
        }else{
            Alert warning = utilAlert.mostrarAlerts("Seleccion requerida",
                    "Seleccione una pelicula para eliminar", "WARNING");
            warning.showAndWait();
        }
    }
    @FXML
    private void handleNew(){
        if (sinPermiso()) return;
        showForm(null);
    }
    @FXML
    private void handleEdit(){
        if (sinPermiso()) return;
        Pelicula selectClient = tablePelicula.getSelectionModel().getSelectedItem();
        if (selectClient !=null){
            Pelicula peliculaFull = peliculaDao.searchById(selectClient.getidpelicula());
            if (peliculaFull == null) {
                utilAlert.mostrarAlerts("Pelicula no encontrada",
                        "La pelicula ya no existe en la base de datos.",
                        "WARNING").showAndWait();
                cargarDatos();
                return;
            }
            showForm(peliculaFull);
        }else{
            Alert alert = utilAlert.mostrarAlerts("Seleccion requerida",
                    "Seleccione una pelicula de la tabla para editar.","WARNING");
            alert.showAndWait();
        }
    }
    private void showForm(Pelicula p){
        try{
            FXMLLoader loader = new FXMLLoader(HelloApplication.class
                    .getResource("/pe/edu/upeu/gestioo_de_cine/views/pelicula-form.fxml"));
            Parent root = loader.load();
            PeliculaFormController controller = loader.getController();
            controller.setPelicula(p);             Stage stage = new Stage();
            stage.setTitle(p==null ? "Nueva Pelicula": "Editar Pelicula");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene( new Scene(root));
            stage.showAndWait();
            if (controller.isSaved()){
                Pelicula peliculaProcesado = controller.getPelicula();
                try {
                    boolean result;
                    if (p == null){
                        result = peliculaDao.insert(peliculaProcesado);
                    }else{
                        peliculaProcesado.setidpelicula(p.getidpelicula());
                        result = peliculaDao.update(peliculaProcesado);
                    }
                    if (result){
                        utilAlert.mostrarAlerts("Correcto",
                                "Los datos de la pelicula se guardaron.","CONFIRMATION").showAndWait();
                    }else{
                        utilAlert.mostrarAlerts("Error",
                                "No se pudieron guardar los datos de la pelicula.","ERROR").showAndWait();
                    }
                } catch (RuntimeException e) {
                    System.out.println("Error al guardar pelicula: " + e.getMessage());
                    utilAlert.mostrarAlerts("No se pudo guardar",
                            "Revise los datos de la pelicula.\nDetalle: " + e.getMessage(),
                            "ERROR").showAndWait();
                }
                cargarDatos();
            }

        } catch (Exception e) {
            System.out.println("Error al abrir el formulario de pelicula: " + e.getMessage());
            utilAlert.mostrarAlerts("Error",
                    "No se pudo abrir el formulario de pelicula.", "ERROR").showAndWait();
        }
    }

}
