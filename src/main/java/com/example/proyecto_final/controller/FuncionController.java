package com.example.proyecto_final.controller;

import com.example.proyecto_final.HelloApplication;
import com.example.proyecto_final.implementacion.FuncionDaoImpl;
import com.example.proyecto_final.interfaces.IFuncionDao;
import com.example.proyecto_final.modelo.Funcion;
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

public class FuncionController {
    private final UtilAlerts utilAlerts = new UtilAlerts();
    private final IFuncionDao dao = new FuncionDaoImpl();
    private ObservableList<Funcion> lista;

    @FXML private TableView<Funcion> tabla;
    @FXML private TableColumn<Funcion, Number> colNumero;
    @FXML private TableColumn<Funcion, Integer> colId;
    @FXML private TableColumn<Funcion, String> colPelicula;
    @FXML private TableColumn<Funcion, String> colSala;
    @FXML private TableColumn<Funcion, String> colHorario;
    @FXML private TableColumn<Funcion, Double> colPrecio;

    @FXML
    public void initialize() {
        colNumero.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });
        colId.setCellValueFactory(cellData -> cellData.getValue().idfuncionProperty().asObject());
        colPelicula.setCellValueFactory(cellData -> cellData.getValue().getPelicula().tituloProperty());
        colSala.setCellValueFactory(cellData -> cellData.getValue().getSala().nombreProperty());
        colHorario.setCellValueFactory(cellData -> cellData.getValue().horarioProperty());
        colPrecio.setCellValueFactory(cellData -> cellData.getValue().precioProperty().asObject());
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
        Funcion seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            utilAlerts.mostrarAlerts("Selección requerida",
                    "Seleccione un registro para editar", "WARNING").showAndWait();
            return;
        }
        Funcion completo = dao.searchById(seleccionado.getIdfuncion());
        if (completo == null) {
            utilAlerts.mostrarAlerts("Error", "No se pudo recuperar el registro", "ERROR").showAndWait();
            return;
        }
        showForm(completo);
    }

    @FXML
    private void handleDelete() {
        Funcion seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            utilAlerts.mostrarAlerts("Selección requerida",
                    "Seleccione un registro para eliminar", "WARNING").showAndWait();
            return;
        }
        Alert confirm = utilAlerts.mostrarAlerts("Confirmar eliminación",
                "¿Eliminar el registro " + seleccionado.getIdfuncion() + "?", "CONFIRMATION");
        Optional<ButtonType> respuesta = confirm.showAndWait();
        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            if (dao.delete(seleccionado)) {
                cargarDatos();
            } else {
                utilAlerts.mostrarAlerts("Error",
                        "No se pudo eliminar. Revise las relaciones y la consola", "ERROR").showAndWait();
            }
        }
    }

    private void showForm(Funcion registro) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource(
                    "/com/example/proyecto_final/views/funcion-form.fxml"));
            Parent root = loader.load();
            FuncionFormController controller = loader.getController();
            controller.setFuncion(registro);

            Stage stage = new Stage();
            stage.setTitle(registro == null ? "Nuevo registro" : "Editar registro");
            stage.initOwner(tabla.getScene().getWindow());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            if (controller.isSaved()) {
                Funcion procesado = controller.getFuncion();
                boolean resultado = registro == null ? dao.insert(procesado) : dao.update(procesado);
                utilAlerts.mostrarAlerts(resultado ? "Registro" : "Error",
                        resultado ? "Operación realizada correctamente" : "No se pudo guardar. Revise la consola",
                        resultado ? "INFORMATION" : "ERROR").showAndWait();
                cargarDatos();
            }
        } catch (IOException e) {
            e.printStackTrace();
            utilAlerts.mostrarAlerts("Error", "No se pudo abrir el formulario", "ERROR").showAndWait();
        }
    }
}
