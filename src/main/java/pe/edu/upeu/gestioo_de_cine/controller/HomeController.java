package pe.edu.upeu.gestioo_de_cine.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import pe.edu.upeu.gestioo_de_cine.implementacion.ClienteDaoImpl;
import pe.edu.upeu.gestioo_de_cine.implementacion.FuncionDaoImpl;
import pe.edu.upeu.gestioo_de_cine.implementacion.PeliculaDaoImpl;
import pe.edu.upeu.gestioo_de_cine.implementacion.SalaDaoImpl;
import pe.edu.upeu.gestioo_de_cine.implementacion.VentaDaoImpl;
import pe.edu.upeu.gestioo_de_cine.interfaces.IClienteDao;
import pe.edu.upeu.gestioo_de_cine.interfaces.IFuncionDao;
import pe.edu.upeu.gestioo_de_cine.interfaces.IPeliculaDao;
import pe.edu.upeu.gestioo_de_cine.interfaces.ISalaDao;
import pe.edu.upeu.gestioo_de_cine.interfaces.IVentaDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Sala;
import pe.edu.upeu.gestioo_de_cine.modelo.Venta;
import pe.edu.upeu.gestioo_de_cine.modelo.UserSession;

import java.time.LocalDate;

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

    private final IPeliculaDao peliculaDao = new PeliculaDaoImpl();
    private final ISalaDao salaDao = new SalaDaoImpl();
    private final IClienteDao clienteDao = new ClienteDaoImpl();
    private final IFuncionDao funcionDao = new FuncionDaoImpl();
    private final IVentaDao ventaDao = new VentaDaoImpl();

    @FXML
    public void initialize() {
        if (UserSession.getUsuario() != null) {
            lblSaludo.setText("Hola, " + UserSession.getUsuario().getNombre());
        }
        try {
            String hoy = LocalDate.now().toString();

            lblPeliculas.setText(String.valueOf(peliculaDao.listAll().size()));
            lblClientes.setText(String.valueOf(clienteDao.listAll().size()));

            int salas = 0;
            int activas = 0;
            for (Sala s : salaDao.listAll()) {
                salas++;
                if (s.getEstado() == null || !"INACTIVA".equalsIgnoreCase(s.getEstado().trim())) {
                    activas++;
                }
            }
            lblSalas.setText(activas + " activas de " + salas);

            int funcionesHoy = 0;
            for (pe.edu.upeu.gestioo_de_cine.modelo.Funcion f : funcionDao.listAll()) {
                if (f.getFecha() != null && f.getFecha().startsWith(hoy)) {
                    funcionesHoy++;
                }
            }
            lblFuncionesHoy.setText(String.valueOf(funcionesHoy));

            int boletos = 0;
            double ingresos = 0;
            for (Venta v : ventaDao.listAll()) {
                if (v.getFechaVenta() != null && v.getFechaVenta().toString().startsWith(hoy)
                        && !"anulada".equalsIgnoreCase(v.getEstado())) {
                    boletos++;
                    ingresos = ingresos + v.getTotalPagar();
                }
            }
            lblBoletosHoy.setText(String.valueOf(boletos));
            if (UserSession.esAdmin()) {
                lblIngresosHoy.setText("S/ " + String.format("%.2f", ingresos));
            } else {
                lblIngresosHoy.setText("-");
            }
        } catch (RuntimeException e) {
            System.out.println("No se pudieron cargar los contadores: " + e.getMessage());
            lblPeliculas.setText("-");
            lblSalas.setText("-");
            lblClientes.setText("-");
            lblFuncionesHoy.setText("-");
            lblBoletosHoy.setText("-");
            lblIngresosHoy.setText("-");
        }
    }
}
