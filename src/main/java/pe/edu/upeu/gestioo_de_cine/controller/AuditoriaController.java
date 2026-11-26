package pe.edu.upeu.gestioo_de_cine.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import pe.edu.upeu.gestioo_de_cine.implementacion.AuditoriaDaoImpl;
import pe.edu.upeu.gestioo_de_cine.interfaces.IAuditoriaDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Auditoria;
import pe.edu.upeu.gestioo_de_cine.utils.UtilAlerts;

import java.util.List;

/**
 * MEJORA 9: pantalla de auditoria (solo administrador).
 *
 * Muestra los ultimos movimientos guardados por Bitacora: ingresos al
 * sistema, ventas, anulaciones, exportaciones y restauraciones de respaldo.
 * Es una pantalla de solo lectura: no se puede editar ni borrar nada.
 */
public class AuditoriaController {

    private static UtilAlerts utilAlert = new UtilAlerts();

    //cuantos movimientos se traen (los mas nuevos)
    private static final int LIMITE = 300;

    @FXML
    private TableView<Auditoria> tableAuditoria;
    @FXML
    private TableColumn<Auditoria, String> ColFecha;
    @FXML
    private TableColumn<Auditoria, String> ColUsuario;
    @FXML
    private TableColumn<Auditoria, String> ColAccion;
    @FXML
    private TableColumn<Auditoria, String> ColDetalle;
    @FXML
    private Label lblCantidad;

    private final IAuditoriaDao auditoriaDao = new AuditoriaDaoImpl();

    @FXML
    public void initialize() {
        ColFecha.setCellValueFactory(
                celda -> celda.getValue().fechaProperty());
        ColUsuario.setCellValueFactory(
                celda -> celda.getValue().usuarioProperty());
        ColAccion.setCellValueFactory(
                celda -> celda.getValue().accionProperty());
        ColDetalle.setCellValueFactory(
                celda -> celda.getValue().detalleProperty());
        cargar();
    }

    @FXML
    private void handleActualizar() {
        cargar();
    }

    private void cargar() {
        try {
            List<Auditoria> lista = auditoriaDao.listar(LIMITE);
            tableAuditoria.setItems(FXCollections.observableArrayList(lista));
            lblCantidad.setText("Mostrando " + lista.size() + " movimiento(s) más reciente(s).");
        } catch (RuntimeException e) {
            System.out.println("Error al cargar la auditoria: " + e.getMessage());
            utilAlert.mostrarAlerts("Error",
                    "No se pudo leer la auditoria.\nDetalle: " + e.getMessage(),
                    "ERROR").showAndWait();
        }
    }
}
