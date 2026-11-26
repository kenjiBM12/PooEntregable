package pe.edu.upeu.gestioo_de_cine.controller;

import java.util.Optional;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pe.edu.upeu.gestioo_de_cine.HelloApplication;
import pe.edu.upeu.gestioo_de_cine.implementacion.FuncionDaoImpl;
import pe.edu.upeu.gestioo_de_cine.implementacion.VentaDaoImpl;
import pe.edu.upeu.gestioo_de_cine.interfaces.IFuncionDao;
import pe.edu.upeu.gestioo_de_cine.interfaces.IVentaDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Funcion;
import pe.edu.upeu.gestioo_de_cine.modelo.UserSession;
import pe.edu.upeu.gestioo_de_cine.utils.UtilAlerts;

import java.util.ArrayList;
import java.util.List;

public class FuncionController {

    private static final UtilAlerts utilAlert = new UtilAlerts();

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
    @FXML
    private TableColumn<Funcion, String> ColFecha;
    @FXML
    private TableColumn<Funcion, String> ColFin;
    @FXML
    private TableColumn<Funcion, String> ColEstado;

    @FXML
    private TextField txtBuscar;

    private final IFuncionDao funcionDao = new FuncionDaoImpl();
    private final IVentaDao ventaDao = new VentaDaoImpl();
    private ObservableList<Funcion> listaFuncion;
    private List<Funcion> todasLasFunciones = new ArrayList<>();

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
                        ? cellData.getValue().getPelicula().getTitulo() : "Sin pelicula"));
        ColSala.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getSala() != null
                        ? cellData.getValue().getSala().getEtiqueta() : "Sin sala"));
        ColFecha.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFecha() != null ? cellData.getValue().getFecha() : ""));
        ColHorario.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getHorario() != null
                        ? cellData.getValue().getHorario() : ""));
        ColPrecio.setCellValueFactory(
                cellData -> cellData.getValue().precioProperty().asObject());
        ColFin.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getHoraFin()));
        ColEstado.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().isCancelada() ? "CANCELADA" : "ACTIVA"));
        ColEstado.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item);
                getStyleClass().removeAll("estado-activa", "estado-anulada");
                if (!empty && item != null) {
                    getStyleClass().add("CANCELADA".equals(item)
                            ? "estado-anulada" : "estado-activa");
                }
            }
        });
        cargarDatos();
    }

    private void cargarDatos() {
        todasLasFunciones = funcionDao.listAll();
        listaFuncion = FXCollections.observableArrayList();
        tableFuncion.setItems(listaFuncion);
        filtrar();
    }

    @FXML
    private void handleBuscar() {
        filtrar();
    }

    private void filtrar() {
        String texto = txtBuscar != null ? txtBuscar.getText().trim().toLowerCase() : "";
        listaFuncion.clear();
        for (Funcion f : todasLasFunciones) {
            if (texto.isEmpty() || contiene(f.getPelicula() != null
                    ? f.getPelicula().getTitulo() : null, texto)
                    || contiene(f.getSala() != null ? f.getSala().getEtiqueta() : null, texto)
                    || contiene(f.getFecha(), texto) || contiene(f.getHorario(), texto)) {
                listaFuncion.add(f);
            }
        }
    }

    private boolean contiene(String valor, String texto) {
        return valor != null && valor.toLowerCase().contains(texto);
    }

    private boolean sinPermiso() {
        if (UserSession.esAdmin()) {
            return false;
        }
        utilAlert.mostrarAlerts("Sin permiso",
                "Solo el administrador puede administrar funciones.", "WARNING").showAndWait();
        return true;
    }

    @FXML
    private void handleDelete() {
        if (sinPermiso()) return;
        Funcion selectId = tableFuncion.getSelectionModel().getSelectedItem();
        if (selectId != null) {
            Alert confirm = utilAlert.mostrarAlerts("Confirmar eliminacion",
                    "Estas seguro de eliminar esta funcion?", "CONFIRMATION");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    if (funcionDao.delete(selectId)) {
                        cargarDatos();
                    } else {
                        utilAlert.mostrarAlerts("Error",
                                "No se pudo eliminar: la funcion tiene ventas registradas", "ERROR").showAndWait();
                    }
                } catch (RuntimeException e) {
                    System.out.println("Error al eliminar funcion: " + e.getMessage());
                    utilAlert.mostrarAlerts("No se pudo eliminar",
                            e.getMessage(), "ERROR").showAndWait();
                }
            }
        } else {
            utilAlert.mostrarAlerts("Seleccion requerida",
                    "Seleccione una funcion para eliminar", "WARNING").showAndWait();
        }
    }

    @FXML
    private void handleCancelar() {
        if (sinPermiso()) return;
        Funcion seleccion = tableFuncion.getSelectionModel().getSelectedItem();
        if (seleccion == null) {
            utilAlert.mostrarAlerts("Seleccion requerida",
                    "Seleccione la función que desea cancelar.", "WARNING").showAndWait();
            return;
        }
        if (seleccion.isCancelada()) {
            utilAlert.mostrarAlerts("Ya está cancelada",
                    "Esa función ya fue cancelada.", "WARNING").showAndWait();
            return;
        }
        Alert confirmar = utilAlert.mostrarAlerts("Cancelar función",
                "Se cancelará la función de "
                        + (seleccion.getPelicula() != null
                        ? seleccion.getPelicula().getTitulo() : "") + " del "
                        + seleccion.getFecha() + " " + seleccion.getHorario() + ".\n"
                        + "Todos sus boletos activos se anularán para devolver el dinero.\n"
                        + "¿Continuar?",
                "CONFIRMATION");
        Optional<ButtonType> respuesta = confirmar.showAndWait();
        if (!respuesta.isPresent() || respuesta.get() != ButtonType.OK) {
            return;
        }
        try {
            int anulados = 0;
            String motivo = "Función cancelada";
            if (funcionDao.cancelar(seleccion.getIdfuncion())) {
                anulados = ventaDao.anularPorFuncion(seleccion.getIdfuncion(),
                        UserSession.getUsuario() != null ? UserSession.getUsuario().getIdusuario() : 0,
                        motivo);
                cargarDatos();
                utilAlert.mostrarAlerts("Función cancelada",
                        "La función quedó cancelada y se anularon " + anulados
                                + " boleto(s).\nLos clientes afectados se pueden ver en Ventas "
                                + "filtrando por estado Anulada.",
                        "INFORMATION").showAndWait();
            } else {
                utilAlert.mostrarAlerts("Sin cambios",
                        "No se pudo cancelar la función (puede que ya estuviera cancelada).",
                        "WARNING").showAndWait();
                cargarDatos();
            }
        } catch (RuntimeException e) {
            System.out.println("Error al cancelar la función: " + e.getMessage());
            utilAlert.mostrarAlerts("Error",
                    "No se pudo cancelar la función.\nDetalle: " + e.getMessage(),
                    "ERROR").showAndWait();
        }
    }

    @FXML
    private void handleNew() {
        if (sinPermiso()) return;
        showForm(null);
    }

    @FXML
    private void handleEdit() {
        if (sinPermiso()) return;
        Funcion selectFuncion = tableFuncion.getSelectionModel().getSelectedItem();
        if (selectFuncion != null) {
            Funcion funcionFull = funcionDao.searchById(selectFuncion.getIdfuncion());
            if (funcionFull == null) {
                utilAlert.mostrarAlerts("Funcion no encontrada",
                        "La función ya no existe en la base de datos.",
                        "WARNING").showAndWait();
                cargarDatos();
                return;
            }
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
                try {
                    boolean result;
                    if (f == null) {
                        result = funcionDao.insert(funcionProcesado);
                    } else {
                        funcionProcesado.setIdfuncion(f.getIdfuncion());
                        result = funcionDao.update(funcionProcesado);
                    }
                    if (result) {
                        utilAlert.mostrarAlerts("Correcto",
                                "Los datos de la funcion se guardaron.", "CONFIRMATION").showAndWait();
                    } else {
                        utilAlert.mostrarAlerts("Error",
                                "No se pudieron guardar los datos de la funcion.", "ERROR").showAndWait();
                    }
                } catch (RuntimeException e) {
                    System.out.println("Error al guardar funcion: " + e.getMessage());
                    utilAlert.mostrarAlerts("No se pudo guardar",
                            "Revise los datos de la funcion.\nDetalle: " + e.getMessage(),
                            "ERROR").showAndWait();
                }
                cargarDatos();
            }
        } catch (Exception e) {
            System.out.println("Error al abrir el formulario de funcion: " + e.getMessage());
            utilAlert.mostrarAlerts("Error",
                    "No se pudo abrir el formulario de función.", "ERROR").showAndWait();
        }
    }
}
