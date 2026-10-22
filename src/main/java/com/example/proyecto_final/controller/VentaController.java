package com.example.proyecto_final.controller;

import com.example.proyecto_final.HelloApplication;
import com.example.proyecto_final.implementacion.VentaDaoImpl;
import com.example.proyecto_final.interfaces.IVentaDao;
import com.example.proyecto_final.modelo.Venta;
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

public class VentaController {
    private final UtilAlerts utilAlerts = new UtilAlerts();

    private final IVentaDao dao = new VentaDaoImpl();
    private ObservableList<Venta> lista;

    @FXML private TableView<Venta> tabla;

    @FXML private TableColumn<Venta, Number> colNumero;

    @FXML private TableColumn<Venta, Integer> colId;

    @FXML private TableColumn<Venta, String> colCliente;

    @FXML private TableColumn<Venta, String> colPelicula;

    @FXML private TableColumn<Venta, Integer> colCantidad;

    @FXML private TableColumn<Venta, Double> colTotal;

    @FXML private TableColumn<Venta, String> colFecha;

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

                cellData.getValue().idventaProperty().asObject());
        colCliente.setCellValueFactory(cellData ->
                cellData.getValue().clienteProperty());
        colPelicula.setCellValueFactory(cellData ->
                cellData.getValue().getFuncion().getPelicula().tituloProperty());
        colCantidad.setCellValueFactory(cellData ->
                cellData.getValue().cantidadAsientosProperty().asObject());
        colTotal.setCellValueFactory(cellData ->
                cellData.getValue().totalPagarProperty().asObject());
        colFecha.setCellValueFactory(cellData ->
                cellData.getValue().fechaVentaProperty().asString());
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

        Venta seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            utilAlerts.mostrarAlerts("Selección requerida",
                    "Seleccione un registro para editar", "WARNING").showAndWait();
            return;
        }
        Venta completo = dao.searchById(seleccionado.getIdventa());
        if (completo == null) {
            utilAlerts.mostrarAlerts("Error", "No se pudo recuperar el registro", "ERROR").showAndWait();
            return;
        }
        showForm(completo);
    }

    @FXML

    private void handleDelete() {
        Venta seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            utilAlerts.mostrarAlerts("Selección requerida",
                    "Seleccione un registro para eliminar", "WARNING").showAndWait();
            return;
        }
        Alert confirm = utilAlerts.mostrarAlerts("Confirmar eliminación",
                "¿Eliminar el registro " + seleccionado.getIdventa() + "?", "CONFIRMATION");

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

    private void showForm(Venta registro) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource(
                    "/com/example/proyecto_final/views/venta-form.fxml"));
            Parent root = loader.load();
            VentaFormController controller = loader.getController();
            controller.setVenta(registro);

            Stage stage = new Stage();
            stage.setTitle(registro == null ? "Nuevo registro" : "Editar registro");
            stage.initOwner(tabla.getScene().getWindow());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            stage.showAndWait();

            if (controller.isSaved()) {
                Venta procesado = controller.getVenta();
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
