
package com.example.proyecto_final.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.example.proyecto_final.HelloApplication;
import com.example.proyecto_final.implementacion.FuncionDaoImpl;
import com.example.proyecto_final.interfaces.IFuncionDao;
import com.example.proyecto_final.modelo.Funcion;
import com.example.proyecto_final.utils.UtilAlerts;

import java.util.Optional;

public class FuncionController {

    private final UtilAlerts utilAlert = new UtilAlerts();

    @FXML
    private TableView<Funcion> tableFuncion;

    @FXML
    private TableColumn<Funcion, Number> ColNum;

    @FXML
    private TableColumn<Funcion, Integer> ColId;

    @FXML
    private TableColumn<Funcion, String> ColPelicula;

    @FXML
    private TableColumn<Funcion, String> ColSala;

    @FXML
    private TableColumn<Funcion, String> ColHorario;

    @FXML
    private TableColumn<Funcion, Double> ColPrecio;

    private final IFuncionDao funcionDao = new FuncionDaoImpl();
    private ObservableList<Funcion> listaFuncion;

    @FXML
    public void initialize() {
        ColNum.setCellFactory(colum ->
                new TableCell<>(){
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

        ColId.setCellValueFactory(cellData ->
                cellData.getValue().idfuncionProperty().asObject());
        ColPelicula.setCellValueFactory(cellData ->
                cellData.getValue().getPelicula().tituloProperty());
        ColSala.setCellValueFactory(cellData ->
                cellData.getValue().getSala().nombreProperty());
        ColHorario.setCellValueFactory(cellData ->
                cellData.getValue().horarioProperty());
        ColPrecio.setCellValueFactory(cellData ->
                cellData.getValue().precioProperty().asObject());
        //llamar al metodo
        cargarDatos();
    }

    private void cargarDatos() {
        listaFuncion = FXCollections.observableArrayList(
                funcionDao.listAll());

        tableFuncion.setItems(listaFuncion);
    }

    @FXML
    private void handleDelete() {

        Funcion selectId =
                tableFuncion.getSelectionModel().getSelectedItem();

        if (selectId != null) {

            Alert confirm = utilAlert.mostrarAlerts(
                    "Confirmar eliminacion",
                    "Estas seguro de eliminar la funcion: "
                            + selectId.getHorario() + "?",
                    "CONFIRMATION");

            Optional<ButtonType> result = confirm.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (funcionDao.delete(selectId)) {
                    listaFuncion.remove(selectId);
                }
            }
        } else {
            Alert warning = utilAlert.mostrarAlerts(
                    "Seleccion requerida",
                    "Seleccione una funcion para eliminar",
                    "WARNING");

            warning.showAndWait();
        }
    }
    @FXML
    private void handleNew() {
        showForm(null);
    }

    @FXML
    private void handleEdit() {
        Funcion seleccionado =
                tableFuncion.getSelectionModel().getSelectedItem();

        if (seleccionado != null) {

            Funcion registroCompleto = funcionDao.searchById(
                    seleccionado.getIdfuncion());

            showForm(registroCompleto);

        } else {
            Alert alert = utilAlert.mostrarAlerts(
                    "Seleccion requerida",
                    "",
                    "CONFIRMATION");

            alert.show();
        }
    }

    private void showForm(Funcion f) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    HelloApplication.class.getResource(
                            "/com/example/proyecto_final/views/funcion-form.fxml"));

            Parent root = loader.load();

            FuncionFormController controller = loader.getController();

            controller.setFuncion(f);

            Stage stage = new Stage();

            stage.setTitle(f == null ?
                    "Nueva Funcion" :
                    "Editar Funcion");

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            if (controller.isSaved()) {

                Funcion funcionProcesada = controller.getFuncion();

                if (f == null) {

                    boolean result = funcionDao.insert(funcionProcesada);

                    if (result) {
                        Alert alert = utilAlert.mostrarAlerts(
                                "Success",
                                "Registro satisfactorio!!!",
                                "CONFIRMATION");
                        alert.show();

                    } else {
                        Alert alert = utilAlert.mostrarAlerts(
                                "Error",
                                "Error al insertar!!!",
                                "ERROR");
                        alert.show();
                    }

                } else {

                    boolean result = funcionDao.update(funcionProcesada);

                    if (result) {
                        Alert alert = utilAlert.mostrarAlerts(
                                "Update",
                                "Registro actualizado!!!",
                                "WARNING");
                        alert.show();

                    } else {
                        Alert alert = utilAlert.mostrarAlerts(
                                "Error",
                                "Error al actualizar!!!",
                                "ERROR");
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
