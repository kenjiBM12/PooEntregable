
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
import com.example.proyecto_final.implementacion.VentaDaoImpl;
import com.example.proyecto_final.interfaces.IVentaDao;
import com.example.proyecto_final.modelo.Venta;
import com.example.proyecto_final.utils.UtilAlerts;

import java.util.Optional;
import java.time.LocalDate;

public class VentaController {

    private final UtilAlerts utilAlert = new UtilAlerts();

    @FXML
    private TableView<Venta> tableVenta;

    @FXML
    private TableColumn<Venta, Number> ColNum;

    @FXML
    private TableColumn<Venta, Integer> ColId;

    @FXML
    private TableColumn<Venta, String> ColCliente;

    @FXML
    private TableColumn<Venta, String> ColPelicula;

    @FXML
    private TableColumn<Venta, Integer> ColCantidad;

    @FXML
    private TableColumn<Venta, Double> ColTotal;

    @FXML
    private TableColumn<Venta, LocalDate> ColFecha;

    private final IVentaDao ventaDao = new VentaDaoImpl();
    private ObservableList<Venta> listaVenta;

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
                cellData.getValue().idventaProperty().asObject());
        ColCliente.setCellValueFactory(cellData ->
                cellData.getValue().clienteProperty());
        ColPelicula.setCellValueFactory(cellData ->
                cellData.getValue().getFuncion().getPelicula().tituloProperty());
        ColCantidad.setCellValueFactory(cellData ->
                cellData.getValue().cantidadAsientosProperty().asObject());
        ColTotal.setCellValueFactory(cellData ->
                cellData.getValue().totalPagarProperty().asObject());
        ColFecha.setCellValueFactory(cellData ->
                cellData.getValue().fechaVentaProperty());
        //llamar al metodo
        cargarDatos();
    }

    private void cargarDatos() {
        listaVenta = FXCollections.observableArrayList(
                ventaDao.listAll());

        tableVenta.setItems(listaVenta);
    }

    @FXML
    private void handleDelete() {

        Venta selectId =
                tableVenta.getSelectionModel().getSelectedItem();

        if (selectId != null) {

            Alert confirm = utilAlert.mostrarAlerts(
                    "Confirmar eliminacion",
                    "Estas seguro de eliminar la venta: "
                            + selectId.getCliente() + "?",
                    "CONFIRMATION");

            Optional<ButtonType> result = confirm.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (ventaDao.delete(selectId)) {
                    listaVenta.remove(selectId);
                }
            }
        } else {
            Alert warning = utilAlert.mostrarAlerts(
                    "Seleccion requerida",
                    "Seleccione una venta para eliminar",
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
        Venta seleccionado =
                tableVenta.getSelectionModel().getSelectedItem();

        if (seleccionado != null) {

            Venta registroCompleto = ventaDao.searchById(
                    seleccionado.getIdventa());

            showForm(registroCompleto);

        } else {
            Alert alert = utilAlert.mostrarAlerts(
                    "Seleccion requerida",
                    "",
                    "CONFIRMATION");

            alert.show();
        }
    }

    private void showForm(Venta v) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    HelloApplication.class.getResource(
                            "/com/example/proyecto_final/views/venta-form.fxml"));

            Parent root = loader.load();

            VentaFormController controller = loader.getController();

            controller.setVenta(v);

            Stage stage = new Stage();

            stage.setTitle(v == null ?
                    "Nueva Venta" :
                    "Editar Venta");

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            if (controller.isSaved()) {

                Venta ventaProcesada = controller.getVenta();

                if (v == null) {

                    boolean result = ventaDao.insert(ventaProcesada);

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

                    boolean result = ventaDao.update(ventaProcesada);

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
