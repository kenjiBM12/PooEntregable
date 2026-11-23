package pe.edu.upeu.gestioo_de_cine.controller;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import pe.edu.upeu.gestioo_de_cine.implementacion.ButacaDaoImpl;
import pe.edu.upeu.gestioo_de_cine.implementacion.ClienteDaoImpl;
import pe.edu.upeu.gestioo_de_cine.implementacion.FuncionDaoImpl;
import pe.edu.upeu.gestioo_de_cine.implementacion.PeliculaDaoImpl;
import pe.edu.upeu.gestioo_de_cine.implementacion.VentaDaoImpl;
import pe.edu.upeu.gestioo_de_cine.interfaces.IButacaDao;
import pe.edu.upeu.gestioo_de_cine.interfaces.IClienteDao;
import pe.edu.upeu.gestioo_de_cine.interfaces.IFuncionDao;
import pe.edu.upeu.gestioo_de_cine.interfaces.IPeliculaDao;
import pe.edu.upeu.gestioo_de_cine.interfaces.IVentaDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Butaca;
import pe.edu.upeu.gestioo_de_cine.modelo.Cliente;
import pe.edu.upeu.gestioo_de_cine.modelo.Funcion;
import pe.edu.upeu.gestioo_de_cine.modelo.Pelicula;
import pe.edu.upeu.gestioo_de_cine.utils.Sesion;
import pe.edu.upeu.gestioo_de_cine.utils.UtilAlerts;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VentaFormController {

    public static UtilAlerts utilAlerts = new UtilAlerts();

    //limite de boletos que se pueden vender en una sola compra
    private static final int MAX_BOLETOS = 10;

    @FXML
    private ComboBox<Pelicula> cboPelicula;
    @FXML
    private ComboBox<Funcion> cboFuncion;
    @FXML
    private GridPane gridButacas;
    @FXML
    private Label lblButaca;
    @FXML
    private Label lblPrecio;
    @FXML
    private Label lblMensaje;
    @FXML
    private TextField txtDni, txtNombre, txtEmail, txtApellidos, txtTelefono;

    //se pueden elegir varias butacas en una misma venta
    private final List<Butaca> butacasSeleccionadas = new ArrayList<>();
    private boolean saved = false;

    private final IPeliculaDao peliculaDao = new PeliculaDaoImpl();
    private final IFuncionDao funcionDao = new FuncionDaoImpl();
    private final IButacaDao butacaDao = new ButacaDaoImpl();
    private final IClienteDao clienteDao = new ClienteDaoImpl();
    private final IVentaDao ventaDao = new VentaDaoImpl();

    @FXML
    public void initialize() {
        //paso 1: pelicula y funcion
        cboPelicula.getItems().setAll(peliculaDao.listAll());
        cboPelicula.valueProperty().addListener((obs, viejo, nuevo) -> cargarFunciones());
        cboFuncion.valueProperty().addListener((obs, viejo, nuevo) -> cargarButacas());
        //paso 3: cuando el DNI tiene 8 numeros se busca al cliente
        txtDni.textProperty().addListener((obs, viejo, nuevo) -> {
            if (nuevo != null && nuevo.trim().length() == 8) {
                buscarCliente(nuevo.trim());
            }
        });
    }

    public boolean isSaved() {
        return saved;
    }

    private void cargarFunciones() {
        cboFuncion.getItems().clear();
        gridButacas.getChildren().clear();
        butacasSeleccionadas.clear();
        lblButaca.setText("Butaca: -");
        lblPrecio.setText("Precio: -");
        lblMensaje.setText("");
        Pelicula pelicula = cboPelicula.getValue();
        if (pelicula == null) {
            return;
        }
        for (Funcion f : funcionDao.listAll()) {
            if (f.getPelicula() != null
                    && String.valueOf(f.getPelicula().getidpelicula())
                    .equals(String.valueOf(pelicula.getidpelicula()))) {
                cboFuncion.getItems().add(f);
            }
        }
        if (cboFuncion.getItems().isEmpty()) {
            lblMensaje.setText("Esa película no tiene funciones programadas.");
        }
    }

    //paso 2: solo se pueden elegir butacas libres
    private void cargarButacas() {
        gridButacas.getChildren().clear();
        butacasSeleccionadas.clear();
        lblButaca.setText("Butaca: -");
        Funcion f = cboFuncion.getValue();
        if (f == null || f.getSala() == null) {
            return;
        }
        lblPrecio.setText("Precio: S/ " + f.getPrecio() + " c/u  ·  " + f.getSala().getEtiqueta());

        Set<Integer> ocupadas = new HashSet<>();
        for (Butaca b : butacaDao.listarOcupadas(f.getIdfuncion())) {
            ocupadas.add(b.getIdbutaca());
        }

        for (Butaca b : butacaDao.listarPorSala(f.getSala().getIdsala())) {
            Button boton = new Button(String.valueOf(b.getNumero()));
            boton.getStyleClass().add("butaca");
            boton.setPrefSize(36, 36);
            if (ocupadas.contains(b.getIdbutaca())) {
                boton.getStyleClass().add("butaca-ocupada");
                boton.setText("✕");
                boton.setDisable(true);
            } else {
                boton.setOnAction(e -> seleccionarButaca(b, boton));
            }
            gridButacas.add(boton, b.getNumero() - 1, b.getFila() - 1);
        }
    }

    //se marca y se desmarca cada butaca, asi se pueden juntar varias
    private void seleccionarButaca(Butaca b, Button boton) {
        if (butacasSeleccionadas.contains(b)) {
            butacasSeleccionadas.remove(b);
            boton.getStyleClass().remove("butaca-elegida");
            boton.setText(String.valueOf(b.getNumero()));
        } else {
            if (butacasSeleccionadas.size() >= MAX_BOLETOS) {
                utilAlerts.mostrarAlerts("Límite de boletos",
                        "En una sola compra se pueden vender como máximo " + MAX_BOLETOS
                                + " boletos.", "WARNING").showAndWait();
                return;
            }
            butacasSeleccionadas.add(b);
            boton.getStyleClass().add("butaca-elegida");
        }
        actualizarButacas();
    }

    private void actualizarButacas() {
        lblMensaje.setText("");
        if (butacasSeleccionadas.isEmpty()) {
            lblButaca.setText("Butaca: -");
            return;
        }
        lblButaca.setText("Butacas: " + textoButacas()
                + "   (" + butacasSeleccionadas.size() + " boleto(s))");
        Funcion f = cboFuncion.getValue();
        if (f != null) {
            lblPrecio.setText("Total: S/ " + (f.getPrecio() * butacasSeleccionadas.size())
                    + "  ·  S/ " + f.getPrecio() + " c/u");
        }
    }

    private void buscarCliente(String dni) {
        Cliente cliente = clienteDao.searchByDni(dni);
        if (cliente != null) {
            txtNombre.setText(cliente.getNombre());
            txtApellidos.setText(cliente.getApellidos() != null ? cliente.getApellidos() : "");
            txtEmail.setText(cliente.getEmail() != null ? cliente.getEmail() : "");
            txtTelefono.setText(cliente.getTelefono() != null ? cliente.getTelefono() : "");
            lblMensaje.setText("Cliente encontrado: " + cliente.getNombre());
        } else {
            txtNombre.setText("");
            txtApellidos.setText("");
            txtEmail.setText("");
            txtTelefono.setText("");
            lblMensaje.setText("Cliente nuevo: se registrará al vender.");
        }
    }

    private boolean validate() {
        if (cboFuncion.getValue() == null || butacasSeleccionadas.isEmpty()
                || txtDni.getText().isBlank() || txtNombre.getText().isBlank()
                || txtApellidos.getText().isBlank() || txtTelefono.getText().isBlank()) {
            utilAlerts.mostrarAlerts("Datos incompletos",
                    "Complete la función, las butacas, el DNI, los nombres, los apellidos y el teléfono.",
                    "ERROR").showAndWait();
            return false;
        }
        String dni = txtDni.getText().trim();
        if (dni.length() != 8 || !dni.matches("[0-9]+")) {
            utilAlerts.mostrarAlerts("DNI invalido",
                    "El DNI debe tener 8 números.", "WARNING").showAndWait();
            return false;
        }
        return true;
    }

    private void cerrar() {
        Stage stage = (Stage) txtDni.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCancel() {
        cerrar();
    }

    @FXML
    private void handleVender() {
        if (!validate()) {
            return;
        }
        Funcion f = cboFuncion.getValue();

        //no se puede vender una butaca ocupada
        Set<Integer> ocupadas = new HashSet<>();
        for (Butaca ocupada : butacaDao.listarOcupadas(f.getIdfuncion())) {
            ocupadas.add(ocupada.getIdbutaca());
        }
        for (Butaca b : butacasSeleccionadas) {
            if (ocupadas.contains(b.getIdbutaca())) {
                utilAlerts.mostrarAlerts("Butaca ocupada",
                        "Una de las butacas ya fue vendida para esta función.", "WARNING").showAndWait();
                cargarButacas();
                return;
            }
        }

        //si el cliente no existe se arma aqui, pero se guarda dentro de la misma transaccion
        //de la venta: si la venta no se completa, el cliente tampoco queda registrado
        Cliente cliente = clienteDao.searchByDni(txtDni.getText().trim());
        if (cliente == null) {
            cliente = new Cliente();
            cliente.setNombre(txtNombre.getText().trim());
            cliente.setApellidos(txtApellidos.getText().trim());
            cliente.setEmail(txtEmail.getText().trim());
            cliente.setDni(txtDni.getText().trim());
            cliente.setTelefono(txtTelefono.getText().trim());
        }

        int idUsuario = Sesion.getUsuario() != null ? Sesion.getUsuario().getIdusuario() : 0;
        String codigo;
        try {
            //el DAO guarda al cliente (si es nuevo) y una venta por butaca en una sola transaccion
            codigo = ventaDao.registrarVentas(f, cliente, butacasSeleccionadas, idUsuario);
        } catch (RuntimeException e) {
            System.out.println("Error al registrar la venta:" + e.getMessage());
            utilAlerts.mostrarAlerts("Error",
                    "No se pudo registrar la venta. Revise los datos e intente otra vez.", "ERROR").showAndWait();
            return;
        }
        if (codigo == null) {
            //la transaccion se revirtio: no se guardo ni el cliente ni ninguna butaca
            utilAlerts.mostrarAlerts("Butaca ocupada",
                    "Una de las butacas ya fue vendida para esta función. No se registró ninguna venta.",
                    "WARNING").showAndWait();
            cargarButacas();
            return;
        }

        mostrarComprobante(cliente, f, codigo);
        saved = true;
        cerrar();
    }

    //comprobante simple al terminar: codigo, cliente y todos los boletos
    private void mostrarComprobante(Cliente cliente, Funcion f, String codigo) {
        String nombreEmpleado = Sesion.getUsuario() != null ? Sesion.getUsuario().getNombre() : "-";
        double total = f.getPrecio() * butacasSeleccionadas.size();

        StringBuilder boletos = new StringBuilder();
        for (Butaca b : butacasSeleccionadas) {
            boletos.append("  · Butaca ").append(letraFila(b.getFila())).append(b.getNumero())
                    .append("  -  S/ ").append(f.getPrecio()).append("\n");
        }

        String texto =
                "Código de compra: " + codigo + "\n" +
                        "Cliente: " + cliente.getNombre() + " " + cliente.getApellidos() + "\n" +
                        "Correo: " + cliente.getEmail() + "\n" +
                        "DNI: " + cliente.getDni() + "\n" +
                        "Teléfono: " + cliente.getTelefono() + "\n" +
                        "Película: " + f.getPelicula().getTitulo() + "\n" +
                        "Sala: " + f.getSala().getEtiqueta() + "\n" +
                        "Función: " + f.getFecha() + "  " + f.getHorario() + "\n" +
                        "Boletos:\n" + boletos +
                        "Entradas: " + butacasSeleccionadas.size() + "\n" +
                        "TOTAL: S/ " + total + "\n" +
                        "Atendido por: " + nombreEmpleado + "\n" +
                        "Fecha de venta: " + LocalDate.now() + "\n" +
                        "Estado: activa";
        Alert alert = utilAlerts.mostrarAlerts("Comprobante de venta", texto, "INFORMATION");
        alert.showAndWait();
    }

    //letra de la fila: fila 1 = A, fila 2 = B...
    private String letraFila(int fila) {
        return String.valueOf((char) ('A' + fila - 1));
    }

    private String textoButacas() {
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < butacasSeleccionadas.size(); i++) {
            Butaca b = butacasSeleccionadas.get(i);
            if (i > 0) {
                texto.append(", ");
            }
            texto.append(letraFila(b.getFila())).append(b.getNumero());
        }
        return texto.toString();
    }
}
