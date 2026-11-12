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
import pe.edu.upeu.gestioo_de_cine.implementacion.SalaDaoImpl;
import pe.edu.upeu.gestioo_de_cine.interfaces.ISalaDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Sala;
import pe.edu.upeu.gestioo_de_cine.utils.UtilAlerts;

import java.util.Optional;

public class SalaController {
    private static UtilAlerts utilAlert = new UtilAlerts();
    //definimos la tabla
    @FXML
    private TableView<Sala> tableSala;

    //variable correlativa
    @FXML
    private TableColumn<Sala, Number> ColNum;
    @FXML
    private TableColumn<Sala, Integer> ColNumero;
    @FXML
    private TableColumn<Sala,Integer> ColId;
    @FXML
    private TableColumn<Sala,String> ColNombre;
    @FXML
    private TableColumn<Sala,String> Coltipo;
    @FXML
    private TableColumn<Sala,Integer> ColCapacidad;
    @FXML
    private TableColumn<Sala,String> ColEstado;

    private final ISalaDao salaDao = new SalaDaoImpl();
    private ObservableList<Sala> listaSala;

    @FXML
    public void initialize() {
        ColNum.setCellFactory(colum -> new TableCell<>(){
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
        ColNumero.setCellValueFactory(cellData -> cellData.getValue()
                .numeroProperty().asObject());
        ColId.setCellValueFactory(
                cellData -> cellData.getValue()
                        .idsalaProperty().asObject());
        ColNombre.setCellValueFactory(
                cellData -> cellData.getValue()
                        .nombreProperty());
        Coltipo.setCellValueFactory(
                cellData -> cellData.getValue()
                        .tipoProperty());
        ColCapacidad.setCellValueFactory(
                cellData -> cellData.getValue()
                        .capacidadProperty().asObject());
        ColEstado.setCellValueFactory(
                cellData -> cellData.getValue()
                        .estadoProperty());
        //llamar al metodo
        cargarDatos();
    }

private void cargarDatos(){
    listaSala = FXCollections.observableArrayList(salaDao.listAll());
    tableSala.setItems(listaSala);
}
@FXML
private void handleDelete(){
   Sala selectId = tableSala.getSelectionModel().getSelectedItem();
    if (selectId !=null){
        Alert confirm = utilAlert.mostrarAlerts("Confirmar eliminacion",
                "Estas seguro de eliminar la sala: "+selectId.getNombre()+"?",
                "CONFIRMATION");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            if (salaDao.delete(selectId)){
                listaSala.remove(selectId);

            }
        }else{
            Alert warning =  utilAlert.mostrarAlerts("Seleccion requerida",
                    "Seleccione una sala para eliminar","WARNING");
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
        Sala selectClient = tableSala.getSelectionModel().getSelectedItem();
        if (selectClient !=null){
            Sala salaFull = salaDao.searchById(selectClient.getIdsala());
            showForm(salaFull);
        }else{
            Alert alert = utilAlert.mostrarAlerts("Seleccion requerida",
                    "","CONFIRMATION");
            alert.show();
        }
    }
    private void showForm(Sala s){
        try{
            FXMLLoader loader = new FXMLLoader(HelloApplication.class
                    .getResource("/pe/edu/upeu/gestioo_de_cine/views/sala-form.fxml"));
            Parent root = loader.load();
            SalaFormController controller = loader.getController();
            controller.setSala(s); //pasamos el cliente null si es nuevo
            Stage stage = new Stage();
            stage.setTitle(s==null ? "Nueva sala": "Editar sala");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene( new Scene(root));
            stage.showAndWait();
            if (controller.isSaved()){
                Sala salaProcesado = controller.getSala();
                if (s == null){
                    boolean result =  salaDao.insert(salaProcesado);
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
                    boolean result =  salaDao.update(salaProcesado);
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
