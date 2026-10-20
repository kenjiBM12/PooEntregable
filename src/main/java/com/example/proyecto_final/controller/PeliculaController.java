package com.example.proyecto_final.controller;

import com.example.proyecto_final.HelloApplication;
import com.example.proyecto_final.implementacion.PeliculaDaoImpl;
import com.example.proyecto_final.interfaces.IPeliculaDao;
import com.example.proyecto_final.modelo.Pelicula;
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

public class PeliculaController {
    private final UtilAlerts utilAlerts = new UtilAlerts();
    private final IPeliculaDao dao = new PeliculaDaoImpl();
    private ObservableList<Pelicula> lista;

    @FXML private TableView<Pelicula> tabla;
    @FXML private TableColumn<Pelicula, Number> colNumero;
    @FXML private TableColumn<Pelicula, Integer> colId;
    @FXML private TableColumn<Pelicula, String> colTitulo;
    @FXML private TableColumn<Pelicula, String> colGenero;
    @FXML private TableColumn<Pelicula, Integer> colDuracion;
    @FXML private TableColumn<Pelicula, String> colClasificacion;

    @FXML
    public void initialize() {
        colNumero.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });
        colId.setCellValueFactory(cellData -> cellData.getValue().idpeliculaProperty().asObject());
        colTitulo.setCellValueFactory(cellData -> cellData.getValue().tituloProperty());
        colGenero.setCellValueFactory(cellData -> cellData.getValue().generoProperty());
        colDuracion.setCellValueFactory(cellData -> cellData.getValue().duracionProperty().asObject());
        colClasificacion.setCellValueFactory(cellData -> cellData.getValue().clasificacionProperty());
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
        Pelicula seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            utilAlerts.mostrarAlerts("Selección requerida",
                    "Seleccione un registro para editar", "WARNING").showAndWait();
            return;
        }
        Pelicula completo = dao.searchById(seleccionado.getIdpelicula());
        if (completo == null) {
            utilAlerts.mostrarAlerts("Error", "No se pudo recuperar el registro", "ERROR").showAndWait();
            return;
        }
        showForm(completo);
    }

    @FXML
    private void handleDelete() {
        Pelicula seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            utilAlerts.mostrarAlerts("Selección requerida",
                    "Seleccione un registro para eliminar", "WARNING").showAndWait();
            return;
        }
        Alert confirm = utilAlerts.mostrarAlerts("Confirmar eliminación",
                "¿Eliminar el registro " + seleccionado.getIdpelicula() + "?", "CONFIRMATION");
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

    private void showForm(Pelicula registro) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource(
                    "/com/example/proyecto_final/views/pelicula-form.fxml"));
            Parent root = loader.load();
            PeliculaFormController controller = loader.getController();
            controller.setPelicula(registro);

            Stage stage = new Stage();
            stage.setTitle(registro == null ? "Nuevo registro" : "Editar registro");
            stage.initOwner(tabla.getScene().getWindow());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            if (controller.isSaved()) {
                Pelicula procesado = controller.getPelicula();
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
