
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
import pe.edu.upeu.project_final.implementacion.FuncionDaoImpl;
import pe.edu.upeu.project_final.interfaces.IFuncionDao;
import pe.edu.upeu.project_final.modelo.Funcion;
import pe.edu.upeu.project_final.utils.UtilAlerts;

import java.io.IOException;
import java.util.Optional;

// CORREGIDO: el listado usa el mismo patrón de tabla y formulario modal del hotel.
public class FuncionController {

    @FXML private TableView<Funcion> tablaFunciones;
    @FXML private TextField txtId;
    @FXML private TableColumn<Funcion, Integer> colId;
    @FXML private TableColumn<Funcion, Integer> colPelicula;
    @FXML private TableColumn<Funcion, Integer> colSala;
    @FXML private TableColumn<Funcion, String> colHorario;
    @FXML private TableColumn<Funcion, Double> colPrecio;

    private final IFuncionDao dao = new FuncionDaoImpl();
    private ObservableList<Funcion> lista;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> c.getValue().idProperty().asObject());
        colPelicula.setCellValueFactory(c -> c.getValue().idPeliculaProperty().asObject());
        colSala.setCellValueFactory(c -> c.getValue().idSalaProperty().asObject());
        colHorario.setCellValueFactory(c -> c.getValue().horarioProperty());
        colPrecio.setCellValueFactory(c -> c.getValue().precioProperty().asObject());
        cargarDatos();
    }

    private void cargarDatos() {
        try {
            lista = FXCollections.observableArrayList(dao.listAll());
            tablaFunciones.setItems(lista);
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
            Funcion encontrado = dao.searchById(id);
            lista = FXCollections.observableArrayList();

            if (encontrado != null) {
                lista.add(encontrado);
            } else {
                UtilAlerts.mostrarAlerts("Sin resultado",
                        "No existe una función con ese ID.", "WARNING").show();
            }

            tablaFunciones.setItems(lista);
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
        Funcion seleccionado = tablaFunciones.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            UtilAlerts.mostrarAlerts("Selección requerida",
                    "Seleccione una función para editar.", "WARNING").show();
            return;
        }

        try {
            Funcion completo = dao.searchById(seleccionado.getId());

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
        Funcion seleccionado = tablaFunciones.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            UtilAlerts.mostrarAlerts("Selección requerida",
                    "Seleccione una función para eliminar.", "WARNING").show();
            return;
        }

        Alert confirmacion = UtilAlerts.mostrarAlerts("Confirmar eliminación",
                "¿Desea eliminar la función seleccionada?", "CONFIRMATION");
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

    private void showForm(Funcion modelo) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource(
                    "/pe/edu/upeu/project_final/views/funcion-form.fxml"));
            Parent root = loader.load();
            FuncionFormController controller = loader.getController();
            controller.setFuncion(modelo);

            Stage stage = new Stage();
            stage.setTitle(modelo == null ? "Nueva función" : "Editar función");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(tablaFunciones.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // AGREGADO: el formulario entrega los datos y el listado decide insertar o actualizar.
            if (controller.isSaved()) {
                boolean flag;

                if (modelo == null) {
                    flag = dao.insert(controller.getFuncion());
                } else {
                    flag = dao.update(controller.getFuncion());
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
