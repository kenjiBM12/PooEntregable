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
import pe.edu.upeu.gestioo_de_cine.implementacion.FuncionDaoImpl;
import pe.edu.upeu.gestioo_de_cine.interfaces.IFuncionDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Funcion;
import pe.edu.upeu.gestioo_de_cine.utils.UtilAlerts;

import java.util.Optional;

public class FuncionController {

    private static UtilAlerts utilAlert = new UtilAlerts();

    @FXML
    private TableView<Funcion> tableFuncion;
    @FXML
    private TableColumn<Funcion, Number> ColNumero;
    @FXML
    private TableColumn<Funcion, Integer> ColId;
    @FXML
    private TableColumn<Funcion, String> ColPelicula;
    @FXML
    private TableColumn<Funcion, String> ColSala;
    @FXML
    private TableColumn<Funcion, String> ColHorario;
    @FXML
    private TableColumn<Funcion, Double> ColPrecio;

    private final IFuncionDao funcionDao = new FuncionDaoImpl();
    private ObservableList<Funcion> listaFuncion;

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
                cellData -> cellData.getValue().idfuncionProperty().asObject());
        ColPelicula.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getPelicula() != null
                        ? cellData.getValue().getPelicula().getNombre() : "Sin pelicula"));
        ColSala.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getSala() != null
                        ? cellData.getValue().getSala().getNombre() : "Sin sala"));
        ColHorario.setCellValueFactory(
                cellData -> cellData.getValue().horarioProperty());
        ColPrecio.setCellValueFactory(
                cellData -> cellData.getValue().precioProperty().asObject());
        cargarDatos();
    }

    private void cargarDatos() {
        listaFuncion = FXCollections.observableArrayList(funcionDao.listAll());
        tableFuncion.setItems(listaFuncion);
    }

    @FXML
    private void handleDelete() {
        Funcion selectId = tableFuncion.getSelectionModel().getSelectedItem();
        if (selectId != null) {
            Alert confirm = utilAlert.mostrarAlerts("Confirmar eliminacion",
                    "Estas seguro de eliminar esta funcion?", "CONFIRMATION");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (funcionDao.delete(selectId)) {
                    listaFuncion.remove(selectId);
                } else {
                    utilAlert.mostrarAlerts("Error",
                            "No se pudo eliminar: la funcion tiene ventas registradas", "ERROR").showAndWait();
                }
            }
        } else {
            utilAlert.mostrarAlerts("Seleccion requerida",
                    "Seleccione una funcion para eliminar", "WARNING").showAndWait();
        }
    }

    @FXML
    private void handleNew() {
        showForm(null);
    }

    @FXML
    private void handleEdit() {
        Funcion selectFuncion = tableFuncion.getSelectionModel().getSelectedItem();
        if (selectFuncion != null) {
            Funcion funcionFull = funcionDao.searchById(selectFuncion.getIdfuncion());
            showForm(funcionFull);
        } else {
            utilAlert.mostrarAlerts("Seleccion requerida",
                    "Seleccione una funcion de la tabla para editar.", "WARNING").showAndWait();
        }
    }

    private void showForm(Funcion f) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class
                    .getResource("/pe/edu/upeu/gestioo_de_cine/views/funcion-form.fxml"));
            Parent root = loader.load();
            FuncionFormController controller = loader.getController();
            controller.setFuncion(f);
            Stage stage = new Stage();
            stage.setTitle(f == null ? "Nueva Funcion" : "Editar Funcion");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            if (controller.isSaved()) {
                Funcion funcionProcesado = controller.getFuncion();
                if (f == null) {
                    boolean result = funcionDao.insert(funcionProcesado);
                    if (result) {
                        utilAlert.mostrarAlerts("Success",
                                "Registro satisfactorio!!!", "CONFIRMATION").show();
                    } else {
                        utilAlert.mostrarAlerts("Error",
                                "Error al insertar!!!", "ERROR").show();
                    }
                } else {
                    funcionProcesado.setIdfuncion(f.getIdfuncion());
                    boolean result = funcionDao.update(funcionProcesado);
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
