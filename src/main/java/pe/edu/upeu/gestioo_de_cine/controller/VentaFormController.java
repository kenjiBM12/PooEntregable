package pe.edu.upeu.gestioo_de_cine.controller;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
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
import pe.edu.upeu.gestioo_de_cine.modelo.UserSession;
import pe.edu.upeu.gestioo_de_cine.utils.UtilAlerts;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VentaFormController {

    public static UtilAlerts utilAlerts = new UtilAlerts();

    private static final int MAX_BOLETOS = 10;
    @FXML
    private VBox panelPaso12, panelPaso3;
    @FXML
    private Button btnSiguiente;
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
    @FXML
    private ComboBox<String> cbgenero;
    @FXML
    private ComboBox<String> cbMetodoPago;

    private final List<Butaca> butacasSeleccionadas = new ArrayList<>();
    private int columnasSala = 0;
    private boolean saved = false;

    private final IPeliculaDao peliculaDao = new PeliculaDaoImpl();
    private final IFuncionDao funcionDao = new FuncionDaoImpl();
    private final IButacaDao butacaDao = new ButacaDaoImpl();
    private final IClienteDao clienteDao = new ClienteDaoImpl();
    private final IVentaDao ventaDao = new VentaDaoImpl();

    @FXML
    public void initialize() {
        cboPelicula.getItems().setAll(peliculaDao.listAll());
        cboPelicula.valueProperty().addListener((obs, viejo, nuevo) -> cargarFunciones());
        cboFuncion.valueProperty().addListener((obs, viejo, nuevo) -> cargarButacas());
        cboFuncion.setConverter(new StringConverter<>() {
            @Override
            public String toString(Funcion f) {
                if (f == null) {
                    return "";
                }
                String texto = f.toString();
                if (yaPaso(f)) {
                    texto = texto + "   (ya empezó)";
                }
                if (salaInactiva(f)) {
                    texto = texto + "   (sala inactiva)";
                }
                return texto;
            }

            @Override
            public Funcion fromString(String texto) {
                return null;
            }
        });
        cbgenero.getItems().addAll("Masculino", "Femenino");
        cbMetodoPago.getItems().addAll("Efectivo", "Tarjeta", "Yape");
        cbMetodoPago.setValue("Efectivo");
        txtDni.textProperty().addListener((obs, viejo, nuevo) -> {
            if (nuevo != null && nuevo.trim().length() == 8) {
                buscarCliente(nuevo.trim());
            }
        });
        actualizarEstadoSiguiente();
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
        boolean tieneFunciones = false;
        for (Funcion f : funcionDao.listAll()) {
            if (f.getPelicula() == null
                    || !String.valueOf(f.getPelicula().getidpelicula())
                    .equals(String.valueOf(pelicula.getidpelicula()))) {
                continue;
            }
            tieneFunciones = true;
            if (f.isCancelada()) {
                continue;
            }
            if (esDeHoyOEnAdelante(f)) {
                cboFuncion.getItems().add(f);
            }
        }
        if (cboFuncion.getItems().isEmpty()) {
            if (tieneFunciones) {
                lblMensaje.setText("Esa película no tiene funciones de hoy en "
                        + "adelante. Cree una nueva en el menú Funciones.");
            } else {
                lblMensaje.setText("Esa película todavía no tiene funciones. "
                        + "Cree una en el menú Funciones.");
            }
        }
        actualizarEstadoSiguiente();
    }

    private boolean esDeHoyOEnAdelante(Funcion f) {
        if (f == null || f.getFecha() == null || f.getFecha().isBlank()) {
            return true;
        }
        String fecha = f.getFecha().trim();
        if (fecha.length() > 10) {
            fecha = fecha.substring(0, 10);
        }
        return fecha.compareTo(LocalDate.now().toString()) >= 0;
    }

    private boolean yaPaso(Funcion f) {
        if (f == null || f.getFecha() == null || f.getFecha().isBlank()) {
            return false;
        }
        String fecha = f.getFecha().trim();
        if (fecha.length() > 10) {
            fecha = fecha.substring(0, 10);
        }
        String hoy = LocalDate.now().toString();
        if (fecha.compareTo(hoy) < 0) {
            return true;
        }
        if (fecha.compareTo(hoy) > 0) {
            return false;
        }
        try {
            LocalTime hora = LocalTime.parse(f.getHorario().trim());
            return !hora.isAfter(LocalTime.now());
        } catch (Exception e) {
            return false;
        }
    }

    private void cargarButacas() {
        gridButacas.getChildren().clear();
        butacasSeleccionadas.clear();
        lblButaca.setText("Butaca: -");
        lblPrecio.setText("Precio: -");
        lblMensaje.setText("");
        Funcion f = cboFuncion.getValue();
        if (f == null || f.getSala() == null) {
            return;
        }
        if (!puedeVenderse(f)) {
            lblMensaje.setText(mensajeNoVendible(f));
            actualizarEstadoSiguiente();
            return;
        }
        lblPrecio.setText("Precio: S/ " + f.getPrecio() + " c/u  ·  " + f.getSala().getEtiqueta());
        columnasSala = f.getSala().getColumnas();

        Set<Integer> ocupadas = new HashSet<>();
        for (Butaca b : butacaDao.listarOcupadas(f.getIdfuncion())) {
            ocupadas.add(b.getIdbutaca());
        }

        List<Butaca> butacas = butacaDao.listarPorSala(f.getSala().getIdsala());
        if (butacas.isEmpty()) {
            lblMensaje.setText("Esta sala todavía no tiene butacas. Pida al administrador que las genere.");
            actualizarEstadoSiguiente();
            return;
        }
        if (columnasSala <= 0) {
            for (Butaca b : butacas) {
                if (b.getNumero() > columnasSala) {
                    columnasSala = b.getNumero();
                }
            }
        }
        if (columnasSala <= 0) {
            columnasSala = 1;
        }
        for (Butaca b : butacas) {
            Button boton = new Button(String.valueOf(numeroVisible(b)));
            boton.getStyleClass().add("butaca");
            boton.setMinSize(36, 36);
            if (ocupadas.contains(b.getIdbutaca())) {
                boton.getStyleClass().add("butaca-ocupada");
                boton.setText("✕");
                boton.setDisable(true);
            } else {
                boton.setOnAction(e -> seleccionarButaca(b, boton));
            }
            gridButacas.add(boton, b.getNumero() - 1, b.getFila() - 1);
        }
        actualizarEstadoSiguiente();
    }

    private void seleccionarButaca(Butaca b, Button boton) {
        if (butacasSeleccionadas.contains(b)) {
            butacasSeleccionadas.remove(b);
            boton.getStyleClass().remove("butaca-elegida");
            boton.setText(String.valueOf(numeroVisible(b)));
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
        actualizarEstadoSiguiente();
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
        Cliente cliente;
        try {
            cliente = clienteDao.searchByDni(dni);
        } catch (RuntimeException e) {
            System.out.println("Error al buscar el cliente: " + e.getMessage());
            lblMensaje.setText("No se pudo buscar el DNI. Puede escribir los datos a mano.");
            return;
        }
        if (cliente != null) {
            txtNombre.setText(cliente.getNombre());
            txtApellidos.setText(cliente.getApellidos() != null ? cliente.getApellidos() : "");
            txtEmail.setText(cliente.getEmail() != null ? cliente.getEmail() : "");
            cbgenero.setValue(cliente.getGenero());
            txtTelefono.setText(cliente.getTelefono() != null ? cliente.getTelefono() : "");
            lblMensaje.setText("Cliente encontrado: " + cliente.getNombre());
        } else {
            txtNombre.setText("");
            txtApellidos.setText("");
            txtEmail.setText("");
            cbgenero.setValue(null);
            txtTelefono.setText("");
            lblMensaje.setText("Cliente nuevo: se registrará al vender.");
        }
    }

    private boolean validate() {
        if (cboFuncion.getValue() == null || butacasSeleccionadas.isEmpty()
                || txtDni.getText().isBlank() || txtNombre.getText().isBlank()
                || txtApellidos.getText().isBlank()) {
            utilAlerts.mostrarAlerts("Datos incompletos",
                    "Complete la función, las butacas, el DNI, los nombres y los apellidos.",
                    "ERROR").showAndWait();
            return false;
        }
        if (!puedeVenderse(cboFuncion.getValue())) {
            utilAlerts.mostrarAlerts("Función no disponible",
                    mensajeNoVendible(cboFuncion.getValue()), "WARNING").showAndWait();
            return false;
        }
        String dni = txtDni.getText().trim();
        if (!dni.matches("[0-9]{8}")) {
            utilAlerts.mostrarAlerts("DNI invalido",
                    "El DNI debe tener 8 números.", "WARNING").showAndWait();
            return false;
        }
        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !email.matches("[^@\\s]+@[^@\\s]+\\.[A-Za-z]{2,}")) {
            utilAlerts.mostrarAlerts("Correo invalido",
                    "Escriba un correo válido, por ejemplo nombre@correo.com.",
                    "WARNING").showAndWait();
            return false;
        }
        String telefono = txtTelefono.getText().trim().replaceAll("[^0-9]", "");
        if (!telefono.isEmpty() && !telefono.matches("[0-9]{6,15}")) {
            utilAlerts.mostrarAlerts("Teléfono invalido",
                    "El teléfono debe tener entre 6 y 15 números.", "WARNING").showAndWait();
            return false;
        }
        if (cbMetodoPago.getValue() == null) {
            utilAlerts.mostrarAlerts("Falta la forma de pago",
                    "Elija cómo está pagando el cliente.", "WARNING").showAndWait();
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

        String dni = txtDni.getText().trim();
        Cliente cliente;
        try {
            cliente = clienteDao.searchByDni(dni);
        } catch (RuntimeException e) {
            System.out.println("Error al buscar el cliente: " + e.getMessage());
            utilAlerts.mostrarAlerts("Error",
                    "No se pudo revisar el DNI. Intente otra vez.", "ERROR").showAndWait();
            return;
        }
        if (cliente == null) {
            cliente = new Cliente();
        }
        cliente.setNombre(txtNombre.getText().trim());
        cliente.setApellidos(txtApellidos.getText().trim());
        cliente.setEmail(txtEmail.getText().trim());
        cliente.setDni(dni);
        cliente.setGenero(cbgenero.getValue());
        cliente.setTelefono(txtTelefono.getText().trim());

        int idUsuario = UserSession.getUsuario() != null ? UserSession.getUsuario().getIdusuario() : 0;
        String codigo;
        try {
            codigo = ventaDao.registrarVentas(f, cliente, butacasSeleccionadas, idUsuario,
                    cbMetodoPago.getValue());
        } catch (RuntimeException e) {
            System.out.println("Error al registrar la venta:" + e.getMessage());
            utilAlerts.mostrarAlerts("Error",
                    "No se pudo registrar la venta. Revise los datos e intente otra vez.", "ERROR").showAndWait();
            return;
        }
        if (codigo == null) {
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

    private void mostrarComprobante(Cliente cliente, Funcion f, String codigo) {
        String nombreEmpleado = UserSession.getUsuario() != null ? UserSession.getUsuario().getNombre() : "-";
        double total = f.getPrecio() * butacasSeleccionadas.size();

        StringBuilder boletos = new StringBuilder();
        for (Butaca b : butacasSeleccionadas) {
            boletos.append("  · Butaca ").append(numeroVisible(b))
                    .append("  -  S/ ").append(f.getPrecio()).append("\n");
        }

        String texto =
                "Código de compra: " + codigo + "\n" +
                        "Cliente: " + cliente.getNombre() + " " + cliente.getApellidos() + "\n" +
                        "Correo: " + cliente.getEmail() + "\n" +
                        "DNI: " + cliente.getDni() + "\n" +
                        "Género: " + (cliente.getGenero() != null ? cliente.getGenero() : "-") + "\n" +
                        "Teléfono: " + cliente.getTelefono() + "\n" +
                        "Película: " + f.getPelicula().getTitulo() + "\n" +
                        "Sala: " + f.getSala().getEtiqueta() + "\n" +
                        "Función: " + f.getFecha() + "  " + f.getHorario() + "\n" +
                        "Boletos:\n" + boletos +
                        "Entradas: " + butacasSeleccionadas.size() + "\n" +
                        "Forma de pago: " + cbMetodoPago.getValue() + "\n" +
                        "TOTAL: S/ " + total + "\n" +
                        "Atendido por: " + nombreEmpleado + "\n" +
                        "Fecha de venta: " + LocalDate.now() + "\n" +
                        "Estado: activa";

        Alert alert = utilAlerts.mostrarAlerts("Comprobante de venta", texto, "INFORMATION");
        alert.showAndWait();
    }

    private int numeroVisible(Butaca b) {
        return (b.getFila() - 1) * columnasSala + b.getNumero();
    }

    private String textoButacas() {
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < butacasSeleccionadas.size(); i++) {
            Butaca b = butacasSeleccionadas.get(i);
            if (i > 0) {
                texto.append(", ");
            }
            texto.append(numeroVisible(b));
        }
        return texto.toString();
    }
    private void mostrarPaso(int paso) {
        boolean enDos = (paso == 1);
        panelPaso12.setVisible(enDos);
        panelPaso12.setManaged(enDos);
        panelPaso3.setVisible(!enDos);
        panelPaso3.setManaged(!enDos);
        Stage stage = (Stage) cboPelicula.getScene().getWindow();
        stage.sizeToScene();
    }
    private void actualizarEstadoSiguiente() {
        boolean listo = puedeVenderse(cboFuncion.getValue())
                && !butacasSeleccionadas.isEmpty();
        btnSiguiente.setDisable(!listo);
    }

    private boolean puedeVenderse(Funcion f) {
        return f != null && !yaPaso(f) && !salaInactiva(f) && !f.isCancelada();
    }

    private boolean salaInactiva(Funcion f) {
        if (f == null || f.getSala() == null || f.getSala().getEstado() == null) {
            return false;
        }
        return "INACTIVA".equalsIgnoreCase(f.getSala().getEstado().trim());
    }

    private String mensajeNoVendible(Funcion f) {
        if (f != null && f.isCancelada()) {
            return "Esa función fue cancelada: elija otra función u otra fecha.";
        }
        if (salaInactiva(f)) {
            return "La " + f.getSala().getEtiqueta() + " está inactiva. "
                    + "Actívela en el menú Salas para vender entradas.";
        }
        return "Esa función ya empezó: elija otra función u otra fecha.";
    }

    @FXML
    private void handleSiguiente() {
        if (cboFuncion.getValue() == null) {
            utilAlerts.mostrarAlerts("Falta la función",
                    "Elija la película y la función.", "WARNING").showAndWait();
            return;
        }
        if (butacasSeleccionadas.isEmpty()) {
            utilAlerts.mostrarAlerts("Faltan butacas",
                    "Marque al menos una butaca libre.", "WARNING").showAndWait();
            return;
        }
        lblMensaje.setText("");
        mostrarPaso(2);
    }

    @FXML
    private void handleVolver() {
        mostrarPaso(1);
    }
}
