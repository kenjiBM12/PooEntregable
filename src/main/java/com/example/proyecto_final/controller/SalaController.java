package com.example.proyecto_final.controller;

import com.example.proyecto_final.HelloApplication;
import com.example.proyecto_final.implementacion.SalaDaoImpl;
import com.example.proyecto_final.interfaces.ISalaDao;
import com.example.proyecto_final.modelo.Sala;
import com.example.proyecto_final.utils.UtilAlerts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Optional;

public class SalaController {
    private final UtilAlerts utilAlerts = new UtilAlerts();

    private final ISalaDao dao = new SalaDaoImpl();
    private ObservableList<Sala> lista;

    @FXML private TableView<Sala> tabla;

    @FXML private TableColumn<Sala, Number> colNumero;

    @FXML private TableColumn<Sala, Integer> colId;

    @FXML private TableColumn<Sala, String> colNombre;

    @FXML private TableColumn<Sala, String> colTipo;

    @FXML private TableColumn<Sala, Integer> colCapacidad;

    @FXML

    public void initialize() {
        colNumero.setCellFactory(column -> new TableCell<>() {
            @Override

            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });

        colId.setCellValueFactory(cellData ->

                cellData.getValue().idsalaProperty().asObject());
        colNombre.setCellValueFactory(cellData ->
                cellData.getValue().nombreProperty());
        colTipo.setCellValueFactory(cellData ->
                cellData.getValue().tipoProperty());
        colCapacidad.setCellValueFactory(cellData ->
                cellData.getValue().capacidadProperty().asObject());
        cargarDatos();
    }

    private void cargarDatos() {

        lista = FXCollections.observableArrayList(dao.listAll());
        tabla.setItems(lista);
    }

    @FXML

    private void handleNew() {
        showForm(null);
    }

    @FXML

    private void handleEdit() {

        Sala seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            utilAlerts.mostrarAlerts("Selección requerida",
                    "Seleccione un registro para editar", "WARNING").showAndWait();
            return;
        }
        Sala completo = dao.searchById(seleccionado.getIdsala());
        if (completo == null) {
            utilAlerts.mostrarAlerts("Error",
                    "No se pudo recuperar el registro", "ERROR").showAndWait();
            return;
        }
        showForm(completo);
    }

    @FXML

    private void handleDelete() {
        Sala seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            utilAlerts.mostrarAlerts("Selección requerida",
                    "Seleccione un registro para eliminar", "WARNING").showAndWait();
            return;
        }
        Alert confirm = utilAlerts.mostrarAlerts("Confirmar eliminación",
                "¿Eliminar el registro " + seleccionado.getIdsala() + "?", "CONFIRMATION");

        Optional<ButtonType> respuesta = confirm.showAndWait();
        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            if (dao.delete(seleccionado)) {
                cargarDatos();
            } else {
                utilAlerts.mostrarAlerts("Error",
                        "No se pudo eliminar. Revise las relaciones y la consola",
                        "ERROR").showAndWait();
            }
        }
    }

    private void showForm(Sala registro) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource(
                    "/com/example/proyecto_final/views/sala-form.fxml"));
            Parent root = loader.load();
            SalaFormController controller = loader.getController();
            controller.setSala(registro);

            Stage stage = new Stage();
            stage.setTitle(registro == null ? "Nuevo registro" : "Editar registro");
            stage.initOwner(tabla.getScene().getWindow());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            stage.showAndWait();

            if (controller.isSaved()) {
                Sala procesado = controller.getSala();
                boolean resultado = registro == null ? dao.insert(procesado) : dao.update(procesado);
                utilAlerts.mostrarAlerts(resultado ? "Registro" : "Error",
                        resultado ? "Operación realizada correctamente" :
                                "No se pudo guardar. Revise la consola",
                        resultado ? "INFORMATION" : "ERROR").showAndWait();
                cargarDatos();
            }
        } catch (IOException e) {
            e.printStackTrace();
            utilAlerts.mostrarAlerts("Error",
                    "No se pudo abrir el formulario", "ERROR").showAndWait();
        }
    }
}
