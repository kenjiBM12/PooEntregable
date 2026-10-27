
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
import pe.edu.upeu.project_final.implementacion.PeliculaDaoImpl;
import pe.edu.upeu.project_final.interfaces.IPeliculaDao;
import pe.edu.upeu.project_final.modelo.Pelicula;
import pe.edu.upeu.project_final.utils.UtilAlerts;

import java.io.IOException;
import java.util.Optional;

// CORREGIDO: el listado usa el mismo patrón de tabla y formulario modal del hotel.
public class PeliculaController {

    @FXML private TableView<Pelicula> tablaPeliculas;
    @FXML private TextField txtId;
    @FXML private TableColumn<Pelicula, Integer> colId;
    @FXML private TableColumn<Pelicula, String> colTitulo;
    @FXML private TableColumn<Pelicula, String> colGenero;
    @FXML private TableColumn<Pelicula, Integer> colDuracion;
    @FXML private TableColumn<Pelicula, String> colClasificacion;

    private final IPeliculaDao dao = new PeliculaDaoImpl();
    private ObservableList<Pelicula> lista;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> c.getValue().idProperty().asObject());
        colTitulo.setCellValueFactory(c -> c.getValue().tituloProperty());
        colGenero.setCellValueFactory(c -> c.getValue().generoProperty());
        colDuracion.setCellValueFactory(c -> c.getValue().duracionMinProperty().asObject());
        colClasificacion.setCellValueFactory(c -> c.getValue().clasificacionProperty());
        cargarDatos();
    }

    private void cargarDatos() {
        try {
            lista = FXCollections.observableArrayList(dao.listAll());
            tablaPeliculas.setItems(lista);
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
            Pelicula encontrado = dao.searchById(id);
            lista = FXCollections.observableArrayList();

            if (encontrado != null) {
                lista.add(encontrado);
            } else {
                UtilAlerts.mostrarAlerts("Sin resultado",
                        "No existe una película con ese ID.", "WARNING").show();
            }

            tablaPeliculas.setItems(lista);
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
        Pelicula seleccionado = tablaPeliculas.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            UtilAlerts.mostrarAlerts("Selección requerida",
                    "Seleccione una película para editar.", "WARNING").show();
            return;
        }

        try {
            Pelicula completo = dao.searchById(seleccionado.getId());

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
        Pelicula seleccionado = tablaPeliculas.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            UtilAlerts.mostrarAlerts("Selección requerida",
                    "Seleccione una película para eliminar.", "WARNING").show();
            return;
        }

        Alert confirmacion = UtilAlerts.mostrarAlerts("Confirmar eliminación",
                "¿Desea eliminar la película seleccionada?", "CONFIRMATION");
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

    private void showForm(Pelicula modelo) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource(
                    "/pe/edu/upeu/project_final/views/pelicula-form.fxml"));
            Parent root = loader.load();
            PeliculaFormController controller = loader.getController();
            controller.setPelicula(modelo);

            Stage stage = new Stage();
            stage.setTitle(modelo == null ? "Nueva película" : "Editar película");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(tablaPeliculas.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // AGREGADO: el formulario entrega los datos y el listado decide insertar o actualizar.
            if (controller.isSaved()) {
                boolean flag;

                if (modelo == null) {
                    flag = dao.insert(controller.getPelicula());
                } else {
                    flag = dao.update(controller.getPelicula());
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