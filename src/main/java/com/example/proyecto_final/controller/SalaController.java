
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
import com.example.proyecto_final.implementacion.SalaDaoImpl;
import com.example.proyecto_final.interfaces.ISalaDao;
import com.example.proyecto_final.modelo.Sala;
import com.example.proyecto_final.utils.UtilAlerts;

import java.util.Optional;

public class SalaController {

    private final UtilAlerts utilAlert = new UtilAlerts();

    @FXML
    private TableView<Sala> tableSala;

    @FXML
    private TableColumn<Sala, Number> ColNum;

    @FXML
    private TableColumn<Sala, Integer> ColId;

    @FXML
    private TableColumn<Sala, String> ColNombre;

    @FXML
    private TableColumn<Sala, String> ColTipo;

    @FXML
    private TableColumn<Sala, Integer> ColCapacidad;

    private final ISalaDao salaDao = new SalaDaoImpl();
    private ObservableList<Sala> listaSala;

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
                cellData.getValue().idsalaProperty().asObject());
        ColNombre.setCellValueFactory(cellData ->
                cellData.getValue().nombreProperty());
        ColTipo.setCellValueFactory(cellData ->
                cellData.getValue().tipoProperty());
        ColCapacidad.setCellValueFactory(cellData ->
                cellData.getValue().capacidadProperty().asObject());
        //llamar al metodo
        cargarDatos();
    }

    private void cargarDatos() {
        listaSala = FXCollections.observableArrayList(
                salaDao.listAll());

        tableSala.setItems(listaSala);
    }

    @FXML
    private void handleDelete() {

        Sala selectId =
                tableSala.getSelectionModel().getSelectedItem();

        if (selectId != null) {

            Alert confirm = utilAlert.mostrarAlerts(
                    "Confirmar eliminacion",
                    "Estas seguro de eliminar la sala: "
                            + selectId.getNombre() + "?",
                    "CONFIRMATION");

            Optional<ButtonType> result = confirm.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (salaDao.delete(selectId)) {
                    listaSala.remove(selectId);
                }
            }
        } else {
            Alert warning = utilAlert.mostrarAlerts(
                    "Seleccion requerida",
                    "Seleccione una sala para eliminar",
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
        Sala seleccionado =
                tableSala.getSelectionModel().getSelectedItem();

        if (seleccionado != null) {

            Sala registroCompleto = salaDao.searchById(
                    seleccionado.getIdsala());

            showForm(registroCompleto);

        } else {
            Alert alert = utilAlert.mostrarAlerts(
                    "Seleccion requerida",
                    "",
                    "CONFIRMATION");

            alert.show();
        }
    }

    private void showForm(Sala s) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    HelloApplication.class.getResource(
                            "/com/example/proyecto_final/views/sala-form.fxml"));

            Parent root = loader.load();

            SalaFormController controller = loader.getController();

            controller.setSala(s);

            Stage stage = new Stage();

            stage.setTitle(s == null ?
                    "Nueva Sala" :
                    "Editar Sala");

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            if (controller.isSaved()) {

                Sala salaProcesada = controller.getSala();

                if (s == null) {

                    boolean result = salaDao.insert(salaProcesada);

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

                    boolean result = salaDao.update(salaProcesada);

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
