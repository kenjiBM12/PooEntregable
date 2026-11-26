package pe.edu.upeu.gestioo_de_cine.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pe.edu.upeu.gestioo_de_cine.HelloApplication;
import pe.edu.upeu.gestioo_de_cine.implementacion.EstadisticaDaoImpl;
import pe.edu.upeu.gestioo_de_cine.implementacion.ReporteDaoImpl;
import pe.edu.upeu.gestioo_de_cine.interfaces.IEstadisticaDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Estadistica;
import pe.edu.upeu.gestioo_de_cine.implementacion.VentaDaoImpl;
import pe.edu.upeu.gestioo_de_cine.interfaces.IReporteDao;
import pe.edu.upeu.gestioo_de_cine.interfaces.IVentaDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Reporte;
import pe.edu.upeu.gestioo_de_cine.modelo.ResumenPago;
import pe.edu.upeu.gestioo_de_cine.modelo.Venta;
import pe.edu.upeu.gestioo_de_cine.utils.Bitacora;
import pe.edu.upeu.gestioo_de_cine.utils.ConexionDB;
import pe.edu.upeu.gestioo_de_cine.utils.RespaldoDB;
import pe.edu.upeu.gestioo_de_cine.utils.UtilAlerts;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * MEJORA 3: pantalla de Reportes / cierre de caja.
 *
 * El administrador elige un rango de fechas y ve:
 *   - cuantos boletos se vendieron (activos)
 *   - cuantos se anularon
 *   - cuanto dinero se cobro
 *   - cuanto dinero se anulo
 *   - MEJORA 2: cuanto entro por cada forma de pago
 *   - el detalle de los boletos de ese rango
 *
 * Los totales los calcula el ReporteDaoImpl con SUM/CASE en SQL; la tabla se
 * llena con la lista de ventas que ya existe (VentaDaoImpl.listAll) filtrada
 * por fecha en Java.
 *
 * Ademas esta pantalla tiene las acciones de MEJORA 7 (exportar el cierre a
 * CSV) y MEJORA 8 (respaldar y restaurar la base de datos).
 */
public class ReporteController {

    private static UtilAlerts utilAlert = new UtilAlerts();

    @FXML
    private DatePicker dpDesde;
    @FXML
    private DatePicker dpHasta;

    @FXML
    private Label lblActivos;
    @FXML
    private Label lblAnulados;
    @FXML
    private Label lblIngresos;
    @FXML
    private Label lblAnulado;
    @FXML
    private Label lblNeto;

    //MEJORA 2: tabla con el desglose por forma de pago
    @FXML
    private TableView<ResumenPago> tablePago;
    @FXML
    private TableColumn<ResumenPago, String> ColPagoMetodo;
    @FXML
    private TableColumn<ResumenPago, Integer> ColPagoBoletos;
    @FXML
    private TableColumn<ResumenPago, Double> ColPagoMonto;

    @FXML
    private TableView<Venta> tableReporte;
    @FXML
    private TableColumn<Venta, String> ColCompra;
    @FXML
    private TableColumn<Venta, String> ColCliente;
    @FXML
    private TableColumn<Venta, String> ColPelicula;
    @FXML
    private TableColumn<Venta, String> ColFecha;
    @FXML
    private TableColumn<Venta, String> ColMetodoPago;
    @FXML
    private TableColumn<Venta, Double> ColMonto;
    @FXML
    private TableColumn<Venta, String> ColEstado;

    //MEJORA 3: graficos del rango (ventas por dia y top de peliculas)
    @FXML
    private BarChart<String, Number> chartDias;
    @FXML
    private BarChart<String, Number> chartPeliculas;

    private final IReporteDao reporteDao = new ReporteDaoImpl();
    private final IVentaDao ventaDao = new VentaDaoImpl();
    private final IEstadisticaDao estadisticaDao = new EstadisticaDaoImpl();

    //MEJORA 7: detalle que se esta viendo, para poder exportarlo a CSV
    private final List<Venta> ultimoDetalle = new ArrayList<>();

    @FXML
    public void initialize() {
        //columnas de la tabla de detalle
        ColCompra.setCellValueFactory(
                cellData -> cellData.getValue().codigoCompraProperty());
        ColCliente.setCellValueFactory(
                cellData -> cellData.getValue().clienteProperty());
        ColPelicula.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFuncion() != null
                        && cellData.getValue().getFuncion().getPelicula() != null
                        ? cellData.getValue().getFuncion().getPelicula().getTitulo() : ""));
        //CORREGIDO: la columna Fecha muestra la fecha de VENTA, que es la que
        //se usa para el cierre de caja (no la fecha de la funcion).
        ColFecha.setCellValueFactory(cellData -> new SimpleStringProperty(
                fechaCierre(cellData.getValue())));
        ColMetodoPago.setCellValueFactory(
                cellData -> cellData.getValue().metodoPagoProperty());
        ColMonto.setCellValueFactory(
                cellData -> cellData.getValue().totalPagarProperty().asObject());
        ColEstado.setCellValueFactory(
                cellData -> cellData.getValue().estadoProperty());

        //MEJORA 2: columnas del desglose por forma de pago (propiedades del modelo)
        ColPagoMetodo.setCellValueFactory(celda -> celda.getValue().metodoProperty());
        ColPagoBoletos.setCellValueFactory(
                celda -> celda.getValue().boletosProperty().asObject());
        ColPagoMonto.setCellValueFactory(
                celda -> celda.getValue().montoProperty().asObject());

        //por defecto: desde el primer dia del mes hasta hoy
        dpDesde.setValue(LocalDate.now().withDayOfMonth(1));
        dpHasta.setValue(LocalDate.now());
        handleGenerar();
    }

    @FXML
    private void handleGenerar() {
        LocalDate desde = dpDesde.getValue();
        LocalDate hasta = dpHasta.getValue();

        if (desde == null || hasta == null) {
            utilAlert.mostrarAlerts("Faltan fechas",
                    "Elija la fecha de inicio y la fecha de fin.", "WARNING").showAndWait();
            return;
        }
        if (desde.isAfter(hasta)) {
            utilAlert.mostrarAlerts("Rango invalido",
                    "La fecha 'desde' no puede ser mayor que la fecha 'hasta'.",
                    "WARNING").showAndWait();
            return;
        }

        Reporte reporte;
        try {
            reporte = reporteDao.generar(desde, hasta);
        } catch (RuntimeException e) {
            System.out.println("Error al generar el reporte: " + e.getMessage());
            utilAlert.mostrarAlerts("Error",
                    "No se pudo generar el reporte.\nDetalle: " + e.getMessage(),
                    "ERROR").showAndWait();
            return;
        }

        //se muestran los totales con dos decimales
        lblActivos.setText(String.valueOf(reporte.getBoletosActivos()));
        lblAnulados.setText(String.valueOf(reporte.getBoletosAnulados()));
        lblIngresos.setText("S/ " + String.format("%.2f", reporte.getIngresos()));
        lblAnulado.setText("S/ " + String.format("%.2f", reporte.getMontoAnulado()));
        lblNeto.setText("S/ " + String.format("%.2f", reporte.getTotalNeto()));

        //MEJORA 2: se llena la tabla del desglose por forma de pago
        tablePago.setItems(FXCollections.observableArrayList(reporte.getPorMetodoPago()));

        //detalle: se recorre la lista completa y se queda con las ventas del rango
        //CORREGIDO: el filtro tambien usa la fecha de venta, igual que los totales
        List<Venta> detalle = new ArrayList<>();
        try {
            for (Venta v : ventaDao.listAll()) {
                String fecha = fechaCierre(v);
                if (fecha.isEmpty()) {
                    continue;
                }
                if (fecha.compareTo(desde.toString()) >= 0
                        && fecha.compareTo(hasta.toString()) <= 0) {
                    detalle.add(v);
                }
            }
        } catch (RuntimeException e) {
            System.out.println("Error al listar el detalle: " + e.getMessage());
        }
        //MEJORA 7: se guarda lo mostrado para que el CSV sea igual a la pantalla
        ultimoDetalle.clear();
        ultimoDetalle.addAll(detalle);
        ObservableList<Venta> lista = FXCollections.observableArrayList(detalle);
        tableReporte.setItems(lista);

        //MEJORA 3: se actualizan los dos gráficos con el mismo rango
        cargarGraficos(desde, hasta);
    }

    /**
     * MEJORA 3: llena el gráfico de ventas por día y el de películas más
     * vendidas. Si una consulta falla, el reporte de tablas sigue funcionando.
     */
    private void cargarGraficos(LocalDate desde, LocalDate hasta) {
        if (chartDias == null || chartPeliculas == null) {
            return;
        }
        try {
            XYChart.Series<String, Number> serieDias = new XYChart.Series<>();
            serieDias.setName("Boletos por día");
            for (Estadistica e : estadisticaDao.ventasPorDia(desde, hasta)) {
                serieDias.getData().add(new XYChart.Data<>(e.getEtiqueta(), e.getCantidad()));
            }
            chartDias.getData().setAll(serieDias);

            XYChart.Series<String, Number> seriePeliculas = new XYChart.Series<>();
            seriePeliculas.setName("Boletos");
            for (Estadistica e : estadisticaDao.topPeliculas(5)) {
                seriePeliculas.getData().add(new XYChart.Data<>(e.getEtiqueta(), e.getCantidad()));
            }
            chartPeliculas.getData().setAll(seriePeliculas);
        } catch (RuntimeException e) {
            System.out.println("No se pudieron cargar los gráficos: " + e.getMessage());
        }
    }

    /**
     * MEJORA 1: abre la pantalla de cierre de caja con arqueo.
     * Se abre como ventana modal sobre Reportes y, al cerrarla, se regenera el
     * reporte porque pudo cambiar algún dato.
     */
    @FXML
    private void handleCerrarCaja() {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class
                    .getResource("/pe/edu/upeu/gestioo_de_cine/views/cierre-caja.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Cierre de caja");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            handleGenerar();
        } catch (Exception e) {
            System.out.println("No se pudo abrir el cierre de caja: " + e.getMessage());
            utilAlert.mostrarAlerts("Error",
                    "No se pudo abrir el cierre de caja.\nDetalle: " + e.getMessage(),
                    "ERROR").showAndWait();
        }
    }

    //limpia el rango y vuelve al mes actual
    @FXML
    private void handleLimpiar() {
        dpDesde.setValue(LocalDate.now().withDayOfMonth(1));
        dpHasta.setValue(LocalDate.now());
        handleGenerar();
    }

    /**
     * MEJORA 7: exporta el cierre que se esta viendo a un archivo CSV.
     *
     * Se usa punto y coma como separador porque asi lo abre Excel en espanol.
     * El archivo queda en C:\\Users\\<usuario>\\SistemaCineUPeU\\reportes\\
     * y se escribe con BOM para que Excel muestre bien las tildes.
     * Si no se puede escribir, se avisa y no se pierde nada.
     */
    @FXML
    private void handleExportarCsv() {
        if (dpDesde.getValue() == null || dpHasta.getValue() == null) {
            utilAlert.mostrarAlerts("Faltan fechas",
                    "Genere primero el cierre con sus fechas.", "WARNING").showAndWait();
            return;
        }
        try {
            File carpeta = new File(ConexionDB.archivoBase().getParentFile(), "reportes");
            if (!carpeta.exists()) {
                carpeta.mkdirs();
            }
            File archivo = new File(carpeta, "cierre_"
                    + dpDesde.getValue() + "_" + dpHasta.getValue() + ".csv");

            StringBuilder csv = new StringBuilder();
            //BOM para que Excel reconozca el archivo como UTF-8
            csv.append('\uFEFF');
            csv.append("Compra;Cliente;DNI;Pelicula;Fecha venta;Forma de pago;Monto;Estado\n");
            for (Venta v : ultimoDetalle) {
                csv.append(csv(v.getCodigoCompra())).append(';')
                        .append(csv(v.getCliente())).append(';')
                        .append(csv(v.getDniCliente())).append(';')
                        .append(csv(tituloPelicula(v))).append(';')
                        .append(csv(fechaCierre(v))).append(';')
                        .append(csv(v.getMetodoPago())).append(';')
                        .append(String.format("%.2f", v.getTotalPagar())).append(';')
                        .append(csv(v.getEstado())).append('\n');
            }
            Files.writeString(archivo.toPath(), csv.toString(), StandardCharsets.UTF_8);
            Bitacora.registrar("REPORTE", "Cierre exportado a CSV: " + archivo.getName());
            utilAlert.mostrarAlerts("Cierre exportado",
                    "El archivo se guardo en:\n" + archivo.getAbsolutePath(),
                    "INFORMATION").showAndWait();
        } catch (Exception e) {
            System.out.println("No se pudo exportar el cierre: " + e.getMessage());
            utilAlert.mostrarAlerts("Error",
                    "No se pudo guardar el archivo CSV.\nDetalle: " + e.getMessage(),
                    "ERROR").showAndWait();
        }
    }

    /**
     * MEJORA 8: crea un respaldo de la base en el momento.
     * Es el mismo metodo que se ejecuta al abrir el sistema, pero aqui lo
     * puede pedir el administrador antes de hacer un cambio delicado.
     */
    @FXML
    private void handleRespaldar() {
        RespaldoDB.respaldar(ConexionDB.archivoBase());
        Bitacora.registrar("RESPALDO", "Respaldo manual de la base de datos");
        utilAlert.mostrarAlerts("Respaldo creado",
                "Se guardo una copia de la base en la carpeta 'respaldos'.",
                "INFORMATION").showAndWait();
    }

    /**
     * MEJORA 8: restaura una copia anterior de la base.
     *
     * Se pide elegir el archivo y confirmar. Antes de reemplazar se cierra
     * la conexion (Windows no deja sobreescribir un archivo abierto) y se
     * guarda un respaldo del estado actual, por si la restauracion no era
     * lo que se queria. Despues hay que cerrar sesion y volver a entrar.
     */
    @FXML
    private void handleRestaurar() {
        File base = ConexionDB.archivoBase();
        List<File> respaldos = RespaldoDB.listar(base);
        if (respaldos.isEmpty()) {
            utilAlert.mostrarAlerts("Sin respaldos",
                    "Todavia no hay copias guardadas de la base de datos.",
                    "WARNING").showAndWait();
            return;
        }
        List<String> nombres = new ArrayList<>();
        for (File f : respaldos) {
            nombres.add(f.getName());
        }
        ChoiceDialog<String> dialogo = new ChoiceDialog<>(nombres.get(0), nombres);
        dialogo.setTitle("Restaurar respaldo");
        dialogo.setHeaderText("Elija la copia que desea restaurar");
        dialogo.setContentText("Copia:");
        Optional<String> elegido = dialogo.showAndWait();
        if (!elegido.isPresent()) {
            return;
        }

        Alert confirmar = utilAlert.mostrarAlerts("Confirmar restauracion",
                "Se reemplazara la base actual por la copia " + elegido.get()
                        + ".\nSe guardara antes un respaldo del estado actual.\n"
                        + "Despues debera cerrar sesion y volver a entrar.",
                "CONFIRMATION");
        Optional<ButtonType> respuesta = confirmar.showAndWait();
        if (!respuesta.isPresent() || respuesta.get() != ButtonType.OK) {
            return;
        }

        //1. respaldo de seguridad de la base actual
        RespaldoDB.respaldar(base);
        //2. se busca el archivo elegido y se copia sobre la base
        File copia = null;
        for (File f : respaldos) {
            if (f.getName().equals(elegido.get())) {
                copia = f;
                break;
            }
        }
        if (RespaldoDB.restaurar(base, copia)) {
            Bitacora.registrar("RESTAURACION", "Base restaurada desde " + elegido.get());
            utilAlert.mostrarAlerts("Base restaurada",
                    "La copia se restauro correctamente.\nCierre sesion y vuelva a entrar "
                            + "para trabajar con los datos restaurados.", "INFORMATION").showAndWait();
        } else {
            utilAlert.mostrarAlerts("Error",
                    "No se pudo restaurar la copia. Cierre el programa y vuelva a intentarlo.",
                    "ERROR").showAndWait();
        }
    }

    /**
     * Fecha que usa el cierre de caja: la de VENTA (cuando se cobro el boleto).
     * Si una venta muy vieja no la tiene guardada, se usa la fecha de la funcion
     * para que no desaparezca del reporte.
     */
    private String fechaCierre(Venta v) {
        if (v.getFechaVenta() != null) {
            return v.getFechaVenta().toString();
        }
        if (v.getFuncion() != null && v.getFuncion().getFecha() != null) {
            return soloFecha(v.getFuncion().getFecha());
        }
        return "";
    }

    //de "2026-11-20 18:30" toma solo "2026-11-20"
    private String soloFecha(String fecha) {
        String texto = fecha.trim();
        return texto.length() > 10 ? texto.substring(0, 10) : texto;
    }

    //titulo de la pelicula de la venta (cadena vacia si no tiene funcion)
    private String tituloPelicula(Venta v) {
        if (v.getFuncion() != null && v.getFuncion().getPelicula() != null) {
            return v.getFuncion().getPelicula().getTitulo();
        }
        return "";
    }

    /**
     * Prepara un valor para el CSV: se encierran los textos entre comillas y
     * las comillas internas se duplican, asi una coma o un punto y coma dentro
     * del nombre de una pelicula no rompe las columnas.
     */
    private String csv(String valor) {
        if (valor == null) {
            return "\"\"";
        }
        return "\"" + valor.replace("\"", "\"\"") + "\"";
    }
}
