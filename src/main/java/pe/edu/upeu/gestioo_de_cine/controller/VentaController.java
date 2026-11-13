package pe.edu.upeu.gestioo_de_cine.controller;

import javafx.beans.property.SimpleStringProperty;
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
import pe.edu.upeu.gestioo_de_cine.implementacion.VentaDaoImpl;
import pe.edu.upeu.gestioo_de_cine.interfaces.IVentaDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Venta;
import pe.edu.upeu.gestioo_de_cine.utils.UtilAlerts;

import java.time.LocalDate;
import java.util.Optional;

public class VentaController {

    private static UtilAlerts utilAlert = new UtilAlerts();

    @FXML
    private TableView<Venta> tableVenta;
    @FXML
    private TableColumn<Venta, Number> ColNumero;
    @FXML
    private TableColumn<Venta, Integer> ColId;
    @FXML
    private TableColumn<Venta, String> ColPelicula;
    @FXML
    private TableColumn<Venta, String> ColHorario;
    @FXML
    private TableColumn<Venta, String> ColCliente;
    @FXML
    private TableColumn<Venta, Integer> ColAsientos;
    @FXML
    private TableColumn<Venta, Double> ColTotal;
    @FXML
    private TableColumn<Venta, LocalDate> ColFecha;

    private final IVentaDao ventaDao = new VentaDaoImpl();
    private ObservableList<Venta> listaVenta;

    @FXML
    public void initialize() {
        ColNumero.setCellFactory(colum -> new TableCell<>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                }
            }
        });
        ColId.setCellValueFactory(
                cellData -> cellData.getValue().idventaProperty().asObject());
        ColPelicula.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFuncion() != null
                        && cellData.getValue().getFuncion().getPelicula() != null
                        ? cellData.getValue().getFuncion().getPelicula().getNombre() : ""));
        ColHorario.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFuncion() != null
                        ? cellData.getValue().getFuncion().getHorario() : ""));
        ColCliente.setCellValueFactory(
                cellData -> cellData.getValue().clienteProperty());
        ColAsientos.setCellValueFactory(
                cellData -> cellData.getValue().cantidadAsientosProperty().asObject());
        ColTotal.setCellValueFactory(
                cellData -> cellData.getValue().totalPagarProperty().asObject());
        ColFecha.setCellValueFactory(
                cellData -> cellData.getValue().fechaVentaProperty());
        cargarDatos();
    }

    private void cargarDatos() {
        listaVenta = FXCollections.observableArrayList(ventaDao.listAll());
        tableVenta.setItems(listaVenta);
    }

    @FXML
    private void handleDelete() {
        Venta selectId = tableVenta.getSelectionModel().getSelectedItem();
        if (selectId != null) {
            Alert confirm = utilAlert.mostrarAlerts("Confirmar eliminacion",
                    "Estas seguro de anular esta venta de: " + selectId.getCliente() + "?",
                    "CONFIRMATION");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (ventaDao.delete(selectId)) {
                    listaVenta.remove(selectId);
                } else {
                    utilAlert.mostrarAlerts("Error",
                            "No se pudo eliminar la venta", "ERROR").showAndWait();
                }
            }
        } else {
            utilAlert.mostrarAlerts("Seleccion requerida",
                    "Seleccione una venta para eliminar", "WARNING").showAndWait();
        }
    }

    @FXML
    private void handleNew() {
        showForm(null);
    }

    @FXML
    private void handleEdit() {
        Venta selectVenta = tableVenta.getSelectionModel().getSelectedItem();
        if (selectVenta != null) {
            Venta ventaFull = ventaDao.searchById(selectVenta.getIdventa());
            showForm(ventaFull);
        } else {
            utilAlert.mostrarAlerts("Seleccion requerida",
                    "Seleccione una venta de la tabla para editar.", "WARNING").showAndWait();
        }
    }

    private void showForm(Venta v) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class
                    .getResource("/pe/edu/upeu/gestioo_de_cine/views/venta-form.fxml"));
            Parent root = loader.load();
            VentaFormController controller = loader.getController();
            controller.setVenta(v);
            Stage stage = new Stage();
            stage.setTitle(v == null ? "Nueva Venta" : "Editar Venta");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            if (controller.isSaved()) {
                Venta ventaProcesada = controller.getVenta();
                if (v == null) {
                    boolean result = ventaDao.insert(ventaProcesada);
                    if (result) {
                        utilAlert.mostrarAlerts("Success",
                                "Registro satisfactorio!!!", "CONFIRMATION").show();
                    } else {
                        utilAlert.mostrarAlerts("Error",
                                "Error al insertar!!!", "ERROR").show();
                    }
                } else {
                    ventaProcesada.setIdventa(v.getIdventa());
                    boolean result = ventaDao.update(ventaProcesada);
                    if (result) {
                        utilAlert.mostrarAlerts("Update",
                                "Registro actualizado!!!", "WARNING").showAndWait();
                    } else {
                        utilAlert.mostrarAlerts("Error",
                                "Error al actualizar!!!", "ERROR").show();
                    }
                }
                cargarDatos();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
