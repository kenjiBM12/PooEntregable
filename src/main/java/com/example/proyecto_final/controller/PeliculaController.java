
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
import com.example.proyecto_final.implementacion.PeliculaDaoImpl;
import com.example.proyecto_final.interfaces.IPeliculaDao;
import com.example.proyecto_final.modelo.Pelicula;
import com.example.proyecto_final.utils.UtilAlerts;

import java.util.Optional;

public class PeliculaController {

    private final UtilAlerts utilAlert = new UtilAlerts();

    @FXML
    private TableView<Pelicula> tablePelicula;

    @FXML
    private TableColumn<Pelicula, Number> ColNum;

    @FXML
    private TableColumn<Pelicula, Integer> ColId;

    @FXML
    private TableColumn<Pelicula, String> ColTitulo;

    @FXML
    private TableColumn<Pelicula, String> ColGenero;

    @FXML
    private TableColumn<Pelicula, Integer> ColDuracion;

    @FXML
    private TableColumn<Pelicula, String> ColClasificacion;

    private final IPeliculaDao peliculaDao = new PeliculaDaoImpl();
    private ObservableList<Pelicula> listaPelicula;

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
                cellData.getValue().idpeliculaProperty().asObject());
        ColTitulo.setCellValueFactory(cellData ->
                cellData.getValue().tituloProperty());
        ColGenero.setCellValueFactory(cellData ->
                cellData.getValue().generoProperty());
        ColDuracion.setCellValueFactory(cellData ->
                cellData.getValue().duracionProperty().asObject());
        ColClasificacion.setCellValueFactory(cellData ->
                cellData.getValue().clasificacionProperty());
        //llamar al metodo
        cargarDatos();
    }

    private void cargarDatos() {
        listaPelicula = FXCollections.observableArrayList(
                peliculaDao.listAll());

        tablePelicula.setItems(listaPelicula);
    }

    @FXML
    private void handleDelete() {

        Pelicula selectId =
                tablePelicula.getSelectionModel().getSelectedItem();

        if (selectId != null) {

            Alert confirm = utilAlert.mostrarAlerts(
                    "Confirmar eliminacion",
                    "Estas seguro de eliminar la pelicula: "
                            + selectId.getTitulo() + "?",
                    "CONFIRMATION");

            Optional<ButtonType> result = confirm.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (peliculaDao.delete(selectId)) {
                    listaPelicula.remove(selectId);
                }
            }
        } else {
            Alert warning = utilAlert.mostrarAlerts(
                    "Seleccion requerida",
                    "Seleccione una pelicula para eliminar",
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
        Pelicula seleccionado =
                tablePelicula.getSelectionModel().getSelectedItem();

        if (seleccionado != null) {

            Pelicula registroCompleto = peliculaDao.searchById(
                    seleccionado.getIdpelicula());

            showForm(registroCompleto);

        } else {
            Alert alert = utilAlert.mostrarAlerts(
                    "Seleccion requerida",
                    "",
                    "CONFIRMATION");

            alert.show();
        }
    }

    private void showForm(Pelicula p) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    HelloApplication.class.getResource(
                            "/com/example/proyecto_final/views/pelicula-form.fxml"));

            Parent root = loader.load();

            PeliculaFormController controller = loader.getController();

            controller.setPelicula(p);

            Stage stage = new Stage();

            stage.setTitle(p == null ?
                    "Nueva Pelicula" :
                    "Editar Pelicula");

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            if (controller.isSaved()) {

                Pelicula peliculaProcesada = controller.getPelicula();

                if (p == null) {

                    boolean result = peliculaDao.insert(peliculaProcesada);

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

                    boolean result = peliculaDao.update(peliculaProcesada);

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
