
package pe.edu.upeu.project_final.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pe.edu.upeu.project_final.HelloApplication;
import pe.edu.upeu.project_final.implementacion.VentaDaoImpl;
import pe.edu.upeu.project_final.interfaces.IVentaDao;
import pe.edu.upeu.project_final.modelo.Venta;
import pe.edu.upeu.project_final.utils.UtilAlerts;

import java.io.IOException;
import java.util.Optional;

// CORREGIDO: el listado usa el mismo patrón de tabla y formulario modal del hotel.
public class VentaController {

    @FXML private TableView<Venta> tablaVentas;
    @FXML private TextField txtId;
    @FXML private TableColumn<Venta, Integer> colId;
    @FXML private TableColumn<Venta, Integer> colFuncion;
    @FXML private TableColumn<Venta, String> colCliente;
    @FXML private TableColumn<Venta, Integer> colCantidad;
    @FXML private TableColumn<Venta, Double> colTotal;
    @FXML private TableColumn<Venta, String> colFecha;

    private final IVentaDao dao = new VentaDaoImpl();
    private ObservableList<Venta> lista;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> c.getValue().idProperty().asObject());
        colFuncion.setCellValueFactory(c -> c.getValue().idFuncionProperty().asObject());
        colCliente.setCellValueFactory(c -> c.getValue().clienteProperty());
        colCantidad.setCellValueFactory(c -> c.getValue().cantidadAsientosProperty().asObject());
        colTotal.setCellValueFactory(c -> c.getValue().totalPagarProperty().asObject());
        colFecha.setCellValueFactory(c -> c.getValue().fechaVentaProperty());
        cargarDatos();
    }

    private void cargarDatos() {
        try {
            lista = FXCollections.observableArrayList(dao.listAll());
            tablaVentas.setItems(lista);
        } catch (RuntimeException e) {
            UtilAlerts.mostrarAlerts("Error al listar", e.getMessage(), "ERROR").show();
        }
    }

    @FXML
    private void handleListAll() {
        txtId.clear();
        cargarDatos();
    }

    @FXML
    private void handleSearchById() {
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            Venta encontrado = dao.searchById(id);
            lista = FXCollections.observableArrayList();

            if (encontrado != null) {
                lista.add(encontrado);
            } else {
                UtilAlerts.mostrarAlerts("Sin resultado",
                        "No existe una venta con ese ID.", "WARNING").show();
            }

            tablaVentas.setItems(lista);
        } catch (NumberFormatException e) {
            UtilAlerts.mostrarAlerts("ID inválido",
                    "Ingrese un ID usando un número entero.", "ERROR").show();
        } catch (RuntimeException e) {
            UtilAlerts.mostrarAlerts("Error al buscar", e.getMessage(), "ERROR").show();
        }
    }

    @FXML
    private void handleNew() {
        showForm(null);
    }

    @FXML
    private void handleEdit() {
        Venta seleccionado = tablaVentas.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            UtilAlerts.mostrarAlerts("Selección requerida",
                    "Seleccione una venta para editar.", "WARNING").show();
            return;
        }

        try {
            Venta completo = dao.searchById(seleccionado.getId());

            if (completo != null) {
                showForm(completo);
            } else {
                UtilAlerts.mostrarAlerts("Sin resultado",
                        "El registro seleccionado ya no existe.", "WARNING").show();
                cargarDatos();
            }
        } catch (RuntimeException e) {
            UtilAlerts.mostrarAlerts("Error al buscar", e.getMessage(), "ERROR").show();
        }
    }

    @FXML
    private void handleDelete() {
        Venta seleccionado = tablaVentas.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            UtilAlerts.mostrarAlerts("Selección requerida",
                    "Seleccione una venta para eliminar.", "WARNING").show();
            return;
        }

        Alert confirmacion = UtilAlerts.mostrarAlerts("Confirmar eliminación",
                "¿Desea eliminar la venta seleccionada?", "CONFIRMATION");
        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                boolean flag = dao.delete(seleccionado);

                if (flag) {
                    cargarDatos();
                    UtilAlerts.mostrarAlerts("Eliminación",
                            "Registro eliminado correctamente.", "INFORMATION").show();
                } else {
                    UtilAlerts.mostrarAlerts("Eliminación",
                            "No se encontró el registro que desea eliminar.", "WARNING").show();
                }
            } catch (RuntimeException e) {
                UtilAlerts.mostrarAlerts("Error al eliminar", e.getMessage(), "ERROR").show();
            }
        }
    }

    private void showForm(Venta modelo) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource(
                    "/pe/edu/upeu/project_final/views/venta-form.fxml"));
            Parent root = loader.load();
            VentaFormController controller = loader.getController();
            controller.setVenta(modelo);

            Stage stage = new Stage();
            stage.setTitle(modelo == null ? "Nueva venta" : "Editar venta");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(tablaVentas.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // AGREGADO: el formulario entrega los datos y el listado decide insertar o actualizar.
            if (controller.isSaved()) {
                boolean flag;

                if (modelo == null) {
                    flag = dao.insert(controller.getVenta());
                } else {
                    flag = dao.update(controller.getVenta());
                }

                if (flag) {
                    cargarDatos();
                    UtilAlerts.mostrarAlerts("Guardado",
                            "Registro guardado correctamente.", "INFORMATION").show();
                } else {
                    UtilAlerts.mostrarAlerts("Guardado",
                            "No se pudo guardar el registro.", "ERROR").show();
                }
            }
        } catch (IOException e) {
            UtilAlerts.mostrarAlerts("Error de formulario", e.getMessage(), "ERROR").show();
        } catch (RuntimeException e) {
            UtilAlerts.mostrarAlerts("Error al guardar", e.getMessage(), "ERROR").show();
        }
    }
}