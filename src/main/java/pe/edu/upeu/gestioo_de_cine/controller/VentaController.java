package pe.edu.upeu.gestioo_de_cine.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pe.edu.upeu.gestioo_de_cine.HelloApplication;
import pe.edu.upeu.gestioo_de_cine.implementacion.VentaDaoImpl;
import pe.edu.upeu.gestioo_de_cine.interfaces.IVentaDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Venta;
import pe.edu.upeu.gestioo_de_cine.utils.Sesion;
import pe.edu.upeu.gestioo_de_cine.utils.UtilAlerts;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
    private TableColumn<Venta, String> ColCodigo;
    @FXML
    private TableColumn<Venta, String> ColPelicula;
    @FXML
    private TableColumn<Venta, String> ColSala;
    @FXML
    private TableColumn<Venta, String> ColHorario;
    @FXML
    private TableColumn<Venta, String> ColCliente;
    @FXML
    private TableColumn<Venta, String> ColDni;
    @FXML
    private TableColumn<Venta, String> ColButaca;
    @FXML
    private TableColumn<Venta, String> ColEmpleado;
    @FXML
    private TableColumn<Venta, Double> ColTotal;
    @FXML
    private TableColumn<Venta, String> ColFecha;
    @FXML
    private TableColumn<Venta, String> ColEstado;

    //controles de busqueda
    @FXML
    private TextField txtBuscar;
    @FXML
    private ComboBox<String> cbEstado;
    @FXML
    private DatePicker dpFecha;
    @FXML
    private Label lblResumen;

    private final IVentaDao ventaDao = new VentaDaoImpl();
    private ObservableList<Venta> listaVenta;
    //lista completa para poder filtrar sin volver a consultar la base
    private List<Venta> todasLasVentas = new ArrayList<>();

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
        ColCodigo.setCellValueFactory(
                cellData -> cellData.getValue().codigoCompraProperty());
        ColPelicula.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFuncion() != null
                        && cellData.getValue().getFuncion().getPelicula() != null
                        ? cellData.getValue().getFuncion().getPelicula().getTitulo() : ""));
        ColSala.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFuncion() != null
                        && cellData.getValue().getFuncion().getSala() != null
                        ? cellData.getValue().getFuncion().getSala().getEtiqueta() : ""));
        ColHorario.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFuncion() != null
                        ? cellData.getValue().getFuncion().getHorario() : ""));
        ColCliente.setCellValueFactory(
                cellData -> cellData.getValue().clienteProperty());
        ColDni.setCellValueFactory(
                cellData -> cellData.getValue().dniClienteProperty());
        ColButaca.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getButaca() != null
                        ? letraFila(cellData.getValue().getButaca().getFila())
                        + cellData.getValue().getButaca().getNumero() : ""));
        ColEmpleado.setCellValueFactory(
                cellData -> cellData.getValue().empleadoProperty());
        ColTotal.setCellValueFactory(
                cellData -> cellData.getValue().totalPagarProperty().asObject());
        ColFecha.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFuncion() != null
                        && cellData.getValue().getFuncion().getFecha() != null
                        ? cellData.getValue().getFuncion().getFecha() : ""));
        ColEstado.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toUpperCase());
                getStyleClass().removeAll("estado-activa", "estado-anulada");
                if (!empty && item != null) {
                    getStyleClass().add("anulada".equalsIgnoreCase(item)
                            ? "estado-anulada" : "estado-activa");
                }
            }
        });
        ColEstado.setCellValueFactory(
                cellData -> cellData.getValue().estadoProperty());

        //las ventas se pueden filtrar por estado y por fecha
        cbEstado.getItems().addAll("Todas", "Activa", "Anulada");
        cbEstado.setValue("Todas");
        cbEstado.valueProperty().addListener((obs, viejo, nuevo) -> filtrar());
        dpFecha.valueProperty().addListener((obs, viejo, nuevo) -> filtrar());

        cargarDatos();
    }

    //se avisa una sola vez por sesion si la base todavía no tiene la columna codigo_compra
    private static boolean avisoBase = false;

    private void cargarDatos() {
        try {
            todasLasVentas = ventaDao.listAll();
        } catch (RuntimeException e) {
            //no se cae la pantalla: se muestra la tabla vacía y un aviso claro
            todasLasVentas = new ArrayList<>();
            System.out.println("Error al listar ventas: " + e.getMessage());
            if (!avisoBase) {
                avisoBase = true;
                utilAlert.mostrarAlerts("Error de base de datos",
                        "No se pudo leer la lista de ventas.\nDetalle: " + e.getMessage(),
                        "ERROR").showAndWait();
            }
        }
        filtrar();
    }

    @FXML
    private void handleNew() {
        showForm();
    }

    @FXML
    private void handleBuscar() {
        filtrar();
    }

    //limpia los filtros y vuelve a mostrar todo
    @FXML
    private void handleLimpiar() {
        if (txtBuscar != null) {
            txtBuscar.clear();
        }
        if (cbEstado != null) {
            cbEstado.setValue("Todas");
        }
        if (dpFecha != null) {
            dpFecha.setValue(null);
        }
        filtrar();
    }

    //filtra la lista con un for normal, comparando en minusculas
    private void filtrar() {
        String texto = txtBuscar != null ? txtBuscar.getText().trim().toLowerCase() : "";
        String estado = cbEstado != null && cbEstado.getValue() != null ? cbEstado.getValue() : "Todas";
        LocalDate fecha = dpFecha != null ? dpFecha.getValue() : null;

        List<Venta> filtradas = new ArrayList<>();
        for (Venta v : todasLasVentas) {
            if (!"Todas".equals(estado)
                    && !estado.equalsIgnoreCase(v.getEstado())) {
                continue;
            }
            if (fecha != null) {
                String fechaFuncion = v.getFuncion() != null ? v.getFuncion().getFecha() : null;
                if (fechaFuncion == null || !fechaFuncion.startsWith(fecha.toString())) {
                    continue;
                }
            }
            if (!texto.isEmpty() && !coincide(v, texto)) {
                continue;
            }
            filtradas.add(v);
        }
        listaVenta = FXCollections.observableArrayList(filtradas);
        tableVenta.setItems(listaVenta);
        mostrarResumen(filtradas);
    }

    private boolean coincide(Venta v, String texto) {
        if (contiene(v.getCodigoCompra(), texto) || contiene(v.getCliente(), texto)
                || contiene(v.getDniCliente(), texto) || contiene(v.getEmpleado(), texto)
                || contiene(v.getEstado(), texto)) {
            return true;
        }
        if (v.getFuncion() != null) {
            if (contiene(v.getFuncion().getHorario(), texto)
                    || contiene(v.getFuncion().getFecha(), texto)) {
                return true;
            }
            if (v.getFuncion().getPelicula() != null
                    && contiene(v.getFuncion().getPelicula().getTitulo(), texto)) {
                return true;
            }
            if (v.getFuncion().getSala() != null
                    && contiene(v.getFuncion().getSala().getEtiqueta(), texto)) {
                return true;
            }
        }
        return v.getFechaVenta() != null && contiene(v.getFechaVenta().toString(), texto);
    }

    private boolean contiene(String valor, String texto) {
        return valor != null && valor.toLowerCase().contains(texto);
    }

    //debajo de la tabla: boletos activos y dinero (las anuladas no suman)
    private void mostrarResumen(List<Venta> ventas) {
        if (lblResumen == null) {
            return;
        }
        int activos = 0;
        double total = 0;
        for (Venta v : ventas) {
            if ("activa".equalsIgnoreCase(v.getEstado())) {
                activos++;
                total = total + v.getTotalPagar();
            }
        }
        String texto = "Boletos activos: " + activos + "   ·   Total: S/ " + total;
        //el resumen de dinero es informacion del historial: solo el admin lo ve
        lblResumen.setText(Sesion.esAdmin() ? texto : "Boletos activos: " + activos);
    }

    //anula un solo boleto: no borra la fila, solo le cambia el estado a 'anulada'.
    //como deja de estar activa, la butaca vuelve a quedar libre
    @FXML
    private void handleDelete() {
        Venta selectId = tableVenta.getSelectionModel().getSelectedItem();
        if (selectId == null) {
            utilAlert.mostrarAlerts("Seleccion requerida",
                    "Seleccione un boleto para anular", "WARNING").showAndWait();
            return;
        }
        if ("anulada".equalsIgnoreCase(selectId.getEstado())) {
            utilAlert.mostrarAlerts("Boleto anulado",
                    "Este boleto ya está anulado.", "WARNING").showAndWait();
            return;
        }
        Alert confirm = utilAlert.mostrarAlerts("Confirmar anulación",
                "¿Anular el boleto " + selectId.getCodigoCompra() + " de "
                        + selectId.getCliente() + "?", "CONFIRMATION");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (ventaDao.anular(selectId)) {
                    //se recarga la tabla para ver el estado 'anulada'
                    cargarDatos();
                } else {
                    utilAlert.mostrarAlerts("Error",
                            "No se pudo anular el boleto", "ERROR").showAndWait();
                }
            } catch (RuntimeException e) {
                System.out.println("Error al anular el boleto:" + e.getMessage());
                utilAlert.mostrarAlerts("Error",
                        "No se pudo anular el boleto.", "ERROR").showAndWait();
            }
        }
    }

    //anula todos los boletos de la misma compra (mismo codigo)
    @FXML
    private void handleAnularCompra() {
        Venta selectId = tableVenta.getSelectionModel().getSelectedItem();
        if (selectId == null) {
            utilAlert.mostrarAlerts("Seleccion requerida",
                    "Seleccione un boleto de la compra que desea anular", "WARNING").showAndWait();
            return;
        }
        Alert confirm = utilAlert.mostrarAlerts("Confirmar anulación de compra",
                "¿Anular TODOS los boletos de la compra " + selectId.getCodigoCompra()
                        + " de " + selectId.getCliente() + "?", "CONFIRMATION");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (ventaDao.anularCompra(selectId.getCodigoCompra())) {
                    cargarDatos();
                } else {
                    utilAlert.mostrarAlerts("Sin cambios",
                            "Esa compra ya estaba anulada.", "WARNING").showAndWait();
                }
            } catch (RuntimeException e) {
                System.out.println("Error al anular la compra:" + e.getMessage());
                utilAlert.mostrarAlerts("Error",
                        "No se pudo anular la compra.", "ERROR").showAndWait();
            }
        }
    }

    private String letraFila(int fila) {
        return String.valueOf((char) ('A' + fila - 1));
    }

    private void showForm() {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class
                    .getResource("/pe/edu/upeu/gestioo_de_cine/views/venta-form.fxml"));
            Parent root = loader.load();
            VentaFormController controller = loader.getController();
            Stage stage = new Stage();
            stage.setTitle("Nueva venta");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            if (controller.isSaved()) {
                cargarDatos();
            }
        } catch (Exception e) {
            //se imprime la excepcion completa para saber la causa real
            //(por ejemplo: una columna que no existe en la base de datos)
            System.out.println("Error al abrir el formulario de venta: " + e.getMessage());
            e.printStackTrace();
            Throwable causa = e.getCause() != null ? e.getCause() : e;
            utilAlert.mostrarAlerts("Error",
                    "No se pudo abrir el formulario de venta.\n" +
                            "Detalle: " + causa.getMessage(), "ERROR").showAndWait();
        }
    }
}
