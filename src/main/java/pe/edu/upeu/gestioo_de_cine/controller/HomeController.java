package pe.edu.upeu.gestioo_de_cine.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import pe.edu.upeu.gestioo_de_cine.implementacion.MetricaDaoImpl;
import pe.edu.upeu.gestioo_de_cine.interfaces.IMetricaDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Metrica;
import pe.edu.upeu.gestioo_de_cine.utils.Sesion;

/**
 * MEJORA 10: pantalla de Inicio con los numeros del dia.
 *
 * Antes el Inicio era solo una imagen. Ahora muestra cuantas peliculas,
 * salas y clientes hay, cuantas funciones quedan para hoy y cuanto se vendio
 * hoy. Todo se calcula con una sola consulta (MetricaDaoImpl).
 *
 * Los numeros de dinero solo se muestran al administrador; el empleado ve
 * los contadores, igual que en la pantalla de ventas.
 */
public class HomeController {

    @FXML
    private Label lblPeliculas;
    @FXML
    private Label lblSalas;
    @FXML
    private Label lblClientes;
    @FXML
    private Label lblFuncionesHoy;
    @FXML
    private Label lblBoletosHoy;
    @FXML
    private Label lblIngresosHoy;
    @FXML
    private Label lblSaludo;

    private final IMetricaDao metricaDao = new MetricaDaoImpl();

    @FXML
    public void initialize() {
        if (Sesion.getUsuario() != null) {
            lblSaludo.setText("Hola, " + Sesion.getUsuario().getNombre());
        }
        try {
            Metrica m = metricaDao.obtener();
            lblPeliculas.setText(String.valueOf(m.getPeliculas()));
            lblSalas.setText(m.getSalasActivas() + " activas de " + m.getSalas());
            lblClientes.setText(String.valueOf(m.getClientes()));
            lblFuncionesHoy.setText(String.valueOf(m.getFuncionesHoy()));
            lblBoletosHoy.setText(String.valueOf(m.getBoletosHoy()));
            //el dinero es informacion sensible: solo el admin lo ve
            if (Sesion.esAdmin()) {
                lblIngresosHoy.setText("S/ " + String.format("%.2f", m.getIngresosHoy()));
            } else {
                lblIngresosHoy.setText("-");
            }
        } catch (RuntimeException e) {
            //si la base falla no se cae el Inicio: se deja el guion
            System.out.println("No se pudieron cargar las metricas: " + e.getMessage());
            lblPeliculas.setText("-");
            lblSalas.setText("-");
            lblClientes.setText("-");
            lblFuncionesHoy.setText("-");
            lblBoletosHoy.setText("-");
            lblIngresosHoy.setText("-");
        }
    }
}
