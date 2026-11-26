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
import javafx.scene.control.TextInputDialog;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pe.edu.upeu.gestioo_de_cine.HelloApplication;
import pe.edu.upeu.gestioo_de_cine.implementacion.VentaDaoImpl;
import pe.edu.upeu.gestioo_de_cine.interfaces.IVentaDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Venta;
import pe.edu.upeu.gestioo_de_cine.utils.Bitacora;
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
    //fecha en que se cobro el boleto: la misma que usa el cierre de caja
    @FXML
    private TableColumn<Venta, String> ColFechaVenta;
    @FXML
    private TableColumn<Venta, String> ColEstado;
    //MEJORA 6: columna que muestra quien anulo y por que
    @FXML
    private TableColumn<Venta, String> ColAnulacion;
    //MEJORA 2: forma de pago con la que se cobro el boleto
    @FXML
    private TableColumn<Venta, String> ColMetodoPago;

    //controles de busqueda
    @FXML
    private TextField txtBuscar;
    @FXML
    private ComboBox<String> cbEstado;
    @FXML
    private DatePicker dpFecha;
    //MEJORA 9b: segunda fecha para buscar un rango (desde / hasta)
    @FXML
    private DatePicker dpFechaHasta;
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
        //CORREGIDO: la columna Butaca muestra el numero global (1..capacidad),
        //igual que el mapa de butacas y el comprobante, no "A1", "B2"...
        ColButaca.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getButaca() != null
                        ? String.valueOf(numeroVisible(cellData.getValue())) : ""));
        ColEmpleado.setCellValueFactory(
                cellData -> cellData.getValue().empleadoProperty());
        ColTotal.setCellValueFactory(
                cellData -> cellData.getValue().totalPagarProperty().asObject());
        ColFecha.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFuncion() != null
                        && cellData.getValue().getFuncion().getFecha() != null
                        ? cellData.getValue().getFuncion().getFecha() : ""));
        //la fecha de venta se guarda como LocalDate, por eso se pasa a texto
        ColFechaVenta.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFechaVenta() != null
                        ? cellData.getValue().getFechaVenta().toString() : ""));
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
        ColAnulacion.setCellValueFactory(
                cellData -> cellData.getValue().anulacionProperty());
        //MEJORA 2: se muestra la forma de pago de cada boleto
        ColMetodoPago.setCellValueFactory(
                cellData -> cellData.getValue().metodoPagoProperty());

        //las ventas se pueden filtrar por estado y por un rango de fechas
        cbEstado.getItems().addAll("Todas", "Activa", "Anulada");
        cbEstado.setValue("Todas");
        cbEstado.valueProperty().addListener((obs, viejo, nuevo) -> filtrar());
        dpFecha.valueProperty().addListener((obs, viejo, nuevo) -> filtrar());
        dpFechaHasta.valueProperty().addListener((obs, viejo, nuevo) -> filtrar());

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
        if (dpFechaHasta != null) {
            dpFechaHasta.setValue(null);
        }
        filtrar();
    }

    //filtra la lista con un for normal, comparando en minusculas
    private void filtrar() {
        String texto = txtBuscar != null ? txtBuscar.getText().trim().toLowerCase() : "";
        String estado = cbEstado != null && cbEstado.getValue() != null ? cbEstado.getValue() : "Todas";
        //MEJORA 9b: rango de fechas. Cualquiera de las dos puede quedar vacia
        LocalDate fechaDesde = dpFecha != null ? dpFecha.getValue() : null;
        LocalDate fechaHasta = dpFechaHasta != null ? dpFechaHasta.getValue() : null;

        List<Venta> filtradas = new ArrayList<>();
        for (Venta v : todasLasVentas) {
            if (!"Todas".equals(estado)
                    && !estado.equalsIgnoreCase(v.getEstado())) {
                continue;
            }
            String fechaFuncion = "";
            if (v.getFuncion() != null && v.getFuncion().getFecha() != null) {
                fechaFuncion = soloFecha(v.getFuncion().getFecha());
            }
            if (fechaDesde != null && fechaFuncion.compareTo(fechaDesde.toString()) < 0) {
                continue;
            }
            if (fechaHasta != null && fechaFuncion.compareTo(fechaHasta.toString()) > 0) {
                continue;
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
                || contiene(v.getEstado(), texto) || contiene(v.getMetodoPago(), texto)) {
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

    //de "2026-11-20" o "2026-11-20 18:30" toma solo los 10 primeros caracteres
    private String soloFecha(String fecha) {
        String texto = fecha.trim();
        return texto.length() > 10 ? texto.substring(0, 10) : texto;
    }

    //id del usuario conectado (0 si por algun motivo no hay sesion)
    private int idUsuarioActual() {
        return Sesion.getUsuario() != null ? Sesion.getUsuario().getIdusuario() : 0;
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
        //MEJORA 6: se pide el motivo de la anulacion y queda guardado con
        //el nombre del usuario que la hizo. Un solo dialogo hace las dos cosas.
        TextInputDialog dialogo = new TextInputDialog("Sin motivo");
        dialogo.setTitle("Anular boleto");
        dialogo.setHeaderText("¿Anular el boleto " + selectId.getCodigoCompra()
                + " de " + selectId.getCliente() + "?");
        dialogo.setContentText("Motivo de la anulación:");
        Optional<String> result = dialogo.showAndWait();
        if (result.isPresent()) {
            String motivo = result.get().trim();
            if (motivo.isEmpty()) {
                motivo = "Sin motivo";
            }
            try {
                if (ventaDao.anular(selectId, idUsuarioActual(), motivo)) {
                    //MEJORA 9: se deja constancia en la auditoria
                    Bitacora.registrar("ANULACION", "Boleto " + selectId.getCodigoCompra()
                            + " anulado. Motivo: " + motivo);
                    //se recarga la tabla para ver el estado 'anulada'
                    cargarDatos();
                } else {
                    //el DAO solo anula si el boleto seguia 'activa'
                    utilAlert.mostrarAlerts("Sin cambios",
                            "Ese boleto ya estaba anulado.", "WARNING").showAndWait();
                    cargarDatos();
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
        //MEJORA 6: igual que el boleto individual, la compra guarda motivo y usuario
        TextInputDialog dialogo = new TextInputDialog("Sin motivo");
        dialogo.setTitle("Anular compra");
        dialogo.setHeaderText("¿Anular TODOS los boletos de la compra "
                + selectId.getCodigoCompra() + " de " + selectId.getCliente() + "?");
        dialogo.setContentText("Motivo de la anulación:");
        Optional<String> result = dialogo.showAndWait();
        if (result.isPresent()) {
            String motivo = result.get().trim();
            if (motivo.isEmpty()) {
                motivo = "Sin motivo";
            }
            try {
                if (ventaDao.anularCompra(selectId.getCodigoCompra(), idUsuarioActual(), motivo)) {
                    //MEJORA 9: se deja constancia en la auditoria
                    Bitacora.registrar("ANULACION", "Compra " + selectId.getCodigoCompra()
                            + " anulada completa. Motivo: " + motivo);
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

    //CORREGIDO: mismo calculo que en el mapa de butacas. Se usan las columnas
    //de la sala de la funcion; si la sala es vieja y no las tiene, se deja el
    //numero que esta guardado para no perder el dato.
    private int numeroVisible(Venta v) {
        if (v.getButaca() == null) {
            return 0;
        }
        int columnas = 0;
        if (v.getFuncion() != null && v.getFuncion().getSala() != null) {
            columnas = v.getFuncion().getSala().getColumnas();
        }
        if (columnas <= 0) {
            return v.getButaca().getNumero();
        }
        return (v.getButaca().getFila() - 1) * columnas + v.getButaca().getNumero();
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
