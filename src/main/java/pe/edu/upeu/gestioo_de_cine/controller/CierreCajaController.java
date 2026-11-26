package pe.edu.upeu.gestioo_de_cine.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import pe.edu.upeu.gestioo_de_cine.implementacion.CierreCajaDaoImpl;
import pe.edu.upeu.gestioo_de_cine.interfaces.ICierreCajaDao;
import pe.edu.upeu.gestioo_de_cine.modelo.CierreCaja;
import pe.edu.upeu.gestioo_de_cine.modelo.ResumenPago;
import pe.edu.upeu.gestioo_de_cine.utils.Bitacora;
import pe.edu.upeu.gestioo_de_cine.utils.Sesion;
import pe.edu.upeu.gestioo_de_cine.utils.UtilAlerts;
import pe.edu.upeu.gestioo_de_cine.utils.Validaciones;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * MEJORA 1: cierre de caja con arqueo.
 *
 * El administrador elige el dia, ve lo que deberia haber por cada forma de
 * pago (cobrado y devuelto), escribe el monto inicial y el efectivo contado,
 * y el sistema calcula la diferencia. Al guardar queda quien cerro y cuando,
 * y el cierre puede corregirse volviendo a guardar el mismo dia.
 */
public class CierreCajaController {

    private static final UtilAlerts utilAlert = new UtilAlerts();

    @FXML
    private DatePicker dpFecha;
    @FXML
    private TableView<ResumenPago> tableMetodo;
    @FXML
    private TableColumn<ResumenPago, String> ColMetodo;
    @FXML
    private TableColumn<ResumenPago, Integer> ColBoletos;
    @FXML
    private TableColumn<ResumenPago, Double> ColCobrado;
    @FXML
    private TableColumn<ResumenPago, Double> ColDevuelto;
    @FXML
    private TableColumn<ResumenPago, Double> ColNeto;

    @FXML
    private TextField txtMontoInicial;
    @FXML
    private TextField txtContado;
    @FXML
    private TextField txtObservacion;
    @FXML
    private Label lblEsperado;
    @FXML
    private Label lblDiferencia;

    @FXML
    private TableView<CierreCaja> tableCierres;
    @FXML
    private TableColumn<CierreCaja, String> ColCierreFecha;
    @FXML
    private TableColumn<CierreCaja, Double> ColCierreEsperado;
    @FXML
    private TableColumn<CierreCaja, Double> ColCierreContado;
    @FXML
    private TableColumn<CierreCaja, Double> ColCierreDiferencia;
    @FXML
    private TableColumn<CierreCaja, String> ColCierreUsuario;
    @FXML
    private TableColumn<CierreCaja, String> ColCierreObservacion;

    private final ICierreCajaDao cierreDao = new CierreCajaDaoImpl();

    //totales del dia que se esta revisando
    private double efectivoNeto;
    private double tarjetaNeto;
    private double yapeNeto;
    private double otrosNeto;
    private double devueltoTotal;

    @FXML
    public void initialize() {
        //los modelos ya exponen sus propiedades: no hay que crearlas al vuelo
        ColMetodo.setCellValueFactory(c -> c.getValue().metodoProperty());
        ColBoletos.setCellValueFactory(c -> c.getValue().boletosProperty().asObject());
        ColCobrado.setCellValueFactory(c -> c.getValue().montoProperty().asObject());
        ColDevuelto.setCellValueFactory(c -> c.getValue().devueltoProperty().asObject());
        ColNeto.setCellValueFactory(c -> c.getValue().netoProperty());

        ColCierreFecha.setCellValueFactory(c -> c.getValue().fechaProperty());
        ColCierreEsperado.setCellValueFactory(
                c -> c.getValue().totalEsperadoProperty().asObject());
        ColCierreContado.setCellValueFactory(
                c -> c.getValue().totalContadoProperty().asObject());
        ColCierreDiferencia.setCellValueFactory(
                c -> c.getValue().diferenciaProperty().asObject());
        ColCierreUsuario.setCellValueFactory(c -> c.getValue().usuarioProperty());
        ColCierreObservacion.setCellValueFactory(c -> c.getValue().observacionProperty());

        dpFecha.setValue(LocalDate.now());
        //la diferencia se recalcula mientras se escribe el contado
        txtContado.textProperty().addListener((obs, viejo, nuevo) -> actualizarDiferencia());
        txtMontoInicial.textProperty().addListener((obs, viejo, nuevo) -> actualizarDiferencia());

        handleCalcular();
        cargarHistorial();
    }

    /** Calcula lo esperado del dia elegido y lo muestra en la tabla y los labels. */
    @FXML
    private void handleCalcular() {
        LocalDate fecha = dpFecha.getValue() != null ? dpFecha.getValue() : LocalDate.now();
        List<ResumenPago> filas;
        try {
            filas = cierreDao.calcularDia(fecha.toString());
        } catch (RuntimeException e) {
            utilAlert.mostrarAlerts("Error",
                    "No se pudo calcular el cierre del día.\nDetalle: " + e.getMessage(),
                    "ERROR").showAndWait();
            return;
        }
        efectivoNeto = 0;
        tarjetaNeto = 0;
        yapeNeto = 0;
        otrosNeto = 0;
        devueltoTotal = 0;
        for (ResumenPago r : filas) {
            devueltoTotal = devueltoTotal + r.getDevuelto();
            String metodo = r.getMetodo() == null ? "" : r.getMetodo().toUpperCase();
            if (metodo.contains("EFECTIVO")) {
                efectivoNeto = efectivoNeto + r.getNeto();
            } else if (metodo.contains("TARJETA")) {
                tarjetaNeto = tarjetaNeto + r.getNeto();
            } else if (metodo.contains("YAPE")) {
                yapeNeto = yapeNeto + r.getNeto();
            } else {
                otrosNeto = otrosNeto + r.getNeto();
            }
        }
        tableMetodo.setItems(FXCollections.observableArrayList(filas));
        actualizarDiferencia();

        //si ya existe un cierre de ese dia se cargan sus datos para corregirlo
        CierreCaja guardado = cierreDao.buscarPorFecha(fecha.toString());
        if (guardado != null) {
            txtMontoInicial.setText(String.format("%.2f", guardado.getMontoInicial()));
            txtContado.setText(String.format("%.2f", guardado.getTotalContado()));
            txtObservacion.setText(guardado.getObservacion());
        }
    }

    /** Efectivo esperado = monto inicial + ventas en efectivo - devoluciones en efectivo. */
    private double efectivoEsperado() {
        return leer(txtMontoInicial.getText()) + efectivoNeto;
    }

    private void actualizarDiferencia() {
        double esperado = efectivoEsperado();
        double contado = leer(txtContado.getText());
        double diferencia = contado - esperado;
        lblEsperado.setText("S/ " + String.format("%.2f", esperado));
        lblDiferencia.setText("S/ " + String.format("%.2f", diferencia));
        //rojo si falta dinero, verde si sobra (o queda exacto)
        lblDiferencia.setStyle(diferencia < -0.009 ? "-fx-text-fill:#e06c75;"
                : "-fx-text-fill:#7ec699;");
    }

    /** Lee un monto escrito con coma o punto; si no es numero devuelve 0. */
    private double leer(String texto) {
        if (texto == null || texto.isBlank()) {
            return 0;
        }
        try {
            return Double.parseDouble(texto.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @FXML
    private void handleGuardar() {
        LocalDate fecha = dpFecha.getValue() != null ? dpFecha.getValue() : LocalDate.now();
        if (!Validaciones.precio(leer(txtContado.getText()) + 0.0001)
                && leer(txtContado.getText()) <= 0 && leer(txtMontoInicial.getText()) <= 0) {
            utilAlert.mostrarAlerts("Datos incompletos",
                    "Escriba el monto inicial y el efectivo contado (pueden ser 0).",
                    "WARNING").showAndWait();
            return;
        }
        double esperado = efectivoEsperado();
        double contado = leer(txtContado.getText());
        CierreCaja cierre = new CierreCaja();
        cierre.setFecha(fecha.toString());
        cierre.setMontoInicial(leer(txtMontoInicial.getText()));
        cierre.setEfectivo(efectivoNeto);
        cierre.setTarjeta(tarjetaNeto);
        cierre.setYape(yapeNeto);
        cierre.setOtros(otrosNeto);
        cierre.setDevuelto(devueltoTotal);
        cierre.setTotalEsperado(esperado);
        cierre.setTotalContado(contado);
        cierre.setDiferencia(contado - esperado);
        cierre.setObservacion(txtObservacion.getText() == null ? "" : txtObservacion.getText().trim());
        if (Sesion.getUsuario() != null) {
            cierre.setIdUsuario(Sesion.getUsuario().getIdusuario());
            cierre.setUsuario(Sesion.getUsuario().getUsuario());
        } else {
            cierre.setUsuario("sistema");
        }
        try {
            if (cierreDao.registrar(cierre)) {
                Bitacora.registrar("CIERRE CAJA", "Cierre del " + cierre.getFecha()
                        + " · esperado S/ " + String.format("%.2f", esperado)
                        + " · contado S/ " + String.format("%.2f", contado)
                        + " · diferencia S/ " + String.format("%.2f", cierre.getDiferencia()));
                utilAlert.mostrarAlerts("Cierre guardado",
                        "El cierre del " + cierre.getFecha() + " quedó registrado.\n"
                                + "Diferencia: S/ " + String.format("%.2f", cierre.getDiferencia()),
                        "INFORMATION").showAndWait();
                cargarHistorial();
            }
        } catch (RuntimeException e) {
            utilAlert.mostrarAlerts("Error",
                    "No se pudo guardar el cierre.\nDetalle: " + e.getMessage(),
                    "ERROR").showAndWait();
        }
    }

    private void cargarHistorial() {
        List<CierreCaja> lista = new ArrayList<>();
        try {
            lista = cierreDao.listar(30);
        } catch (RuntimeException e) {
            System.out.println("No se pudo cargar el historial de cierres: " + e.getMessage());
        }
        tableCierres.setItems(FXCollections.observableArrayList(lista));
    }
}
