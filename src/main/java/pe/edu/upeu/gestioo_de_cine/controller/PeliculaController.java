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
import pe.edu.upeu.gestioo_de_cine.utils.UtilAlerts;

import java.util.Optional;

public class PeliculaController {

    private static UtilAlerts utilAlert = new UtilAlerts();

    //definimos la tabla
    @FXML
    private TableView<Pelicula> tablePelicula;
    //variable correlativa
    @FXML
    private TableColumn<Pelicula,Number> ColNumero;
    @FXML
    private TableColumn<Pelicula, Integer> ColNumeroPelicula;
    @FXML
    private TableColumn<Pelicula,Integer> ColId;
    @FXML
    private TableColumn<Pelicula,String> ColNombre;
    @FXML
    private TableColumn<Pelicula,String> ColDuracion;
    @FXML
    private TableColumn<Pelicula,String> ColCategoria;
    @FXML
    private TableColumn<Pelicula,String> ColClasificacion;


    private final IPeliculaDao peliculaDao = new PeliculaDaoImpl();
    private ObservableList<Pelicula> listaPelicula;


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
        ColNombre.setCellValueFactory(
                cellData -> cellData.getValue()
                        .nombreProperty());
        ColDuracion.setCellValueFactory(
                cellData -> cellData.getValue()
                        .duracionProperty());
        ColCategoria.setCellValueFactory(
                cellData -> cellData.getValue()
                        .categoriaProperty());
        ColClasificacion.setCellValueFactory(
                cellData -> cellData.getValue()
                        .clasificacionProperty());
        //llamar al metodo
        cargarDatos();
    }
    private void cargarDatos(){
        listaPelicula = FXCollections.observableArrayList(peliculaDao.listAll());
        tablePelicula.setItems(listaPelicula);
    }
    @FXML
    private void handleDelete(){
        Pelicula selectId = tablePelicula.getSelectionModel().getSelectedItem();
        if (selectId !=null){
            Alert confirm = utilAlert.mostrarAlerts("Confirmar eliminacion",
                    "Estas seguro de eliminar la pelicula: "+selectId.getNombre()+"?",
                    "CONFIRMATION");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK){
                if (peliculaDao.delete(selectId)){
                    listaPelicula.remove(selectId);

                }
            }else{
                Alert warning =  utilAlert.mostrarAlerts("Seleccion requerida",
                        "Seleccione una pelicula para eliminar","WARNING");
                warning.showAndWait();
            }
        }
    }

    @FXML
    private void handleNew(){
        // System.out.println("abriendo el formulario");
        showForm(null);

    }
    @FXML
    private void handleEdit(){
        Pelicula selectClient = tablePelicula.getSelectionModel().getSelectedItem();
        if (selectClient !=null){
           Pelicula peliculaFull = peliculaDao.searchById(selectClient.getidpelicula());
            showForm(peliculaFull);
        }else{
            Alert alert = utilAlert.mostrarAlerts("Seleccion requerida",
                    "","CONFIRMATION");
            alert.show();
        }
    }
    private void showForm(Pelicula p){
        try{
            FXMLLoader loader = new FXMLLoader(HelloApplication.class
                    .getResource("/pe/edu/upeu/gestioo_de_cine/views/pelicula-form.fxml"));
            Parent root = loader.load();
            PeliculaFormController controller = loader.getController();
            controller.setPelicula(p); //pasamos el cliente null si es nuevo
            Stage stage = new Stage();
            stage.setTitle(p==null ? "Nueva Pelicula": "Editar Pelicula");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene( new Scene(root));
            stage.showAndWait();
            if (controller.isSaved()){
                Pelicula peliculaProcesado = controller.getPelicula();
                if (p == null){
                    boolean result =  peliculaDao.insert(peliculaProcesado);
                    if (result){
                        Alert alert = utilAlert.mostrarAlerts("Success",
                                "Registro satisfactorio!!!","CONFIRMATION");
                        alert.show();
                    }else{
                        Alert alert = utilAlert.mostrarAlerts("Error",
                                "Error al insertar!!!","ERROR");
                        alert.show();
                    }
                }else{
                    boolean result =  peliculaDao.update(peliculaProcesado);
                    if (result){
                        Alert alert = utilAlert.mostrarAlerts("Update",
                                "Registro actualizado!!!","WARNING");
                        alert.showAndWait();
                    }else{
                        Alert alert = utilAlert.mostrarAlerts("Error",
                                "Error al actualizar!!!","ERROR");
                        alert.show();
                    }
                }
                cargarDatos();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
