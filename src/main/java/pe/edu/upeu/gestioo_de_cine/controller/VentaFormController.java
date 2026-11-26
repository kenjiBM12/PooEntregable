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
import pe.edu.upeu.gestioo_de_cine.utils.Bitacora;
import pe.edu.upeu.gestioo_de_cine.utils.Comprobante;
import pe.edu.upeu.gestioo_de_cine.utils.Sesion;
import pe.edu.upeu.gestioo_de_cine.utils.UtilAlerts;
import pe.edu.upeu.gestioo_de_cine.utils.Validaciones;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VentaFormController {

    public static UtilAlerts utilAlerts = new UtilAlerts();

    //limite de boletos que se pueden vender en una sola compra
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
    //CORREGIDO: se agrego el genero al formulario de venta; el combo usa textos
    //igual que el formulario de clientes (Masculino / Femenino)
    @FXML
    private ComboBox<String> cbgenero;
    //MEJORA 6: forma de pago de la compra
    @FXML
    private ComboBox<String> cbMetodoPago;

    //se pueden elegir varias butacas en una misma venta
    private final List<Butaca> butacasSeleccionadas = new ArrayList<>();
    //CORREGIDO: columnas de la sala. Antes el mapa mostraba el numero de la
    //butaca dentro de su fila y volvia a empezar en 1 en cada fila; con las
    //columnas se calcula el numero real, del 1 hasta la capacidad de la sala
    private int columnasSala = 0;
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
        //CORREGIDO: el combo de funciones muestra TODAS las funciones de la
        //pelicula. Antes se escondian las que ya habian empezado o las de una
        //sala inactiva, y si eran las unicas el combo quedaba vacio y parecia
        //que la pelicula no tenia funciones. Ahora se ven marcadas y el
        //sistema impide venderlas con un aviso claro.
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
        //opciones de genero, las mismas que en el alta de cliente
        cbgenero.getItems().addAll("Masculino", "Femenino");
        //MEJORA 6: formas de pago simples para el cierre de caja
        cbMetodoPago.getItems().addAll("Efectivo", "Tarjeta", "Yape");
        cbMetodoPago.setValue("Efectivo");
        //paso 3: cuando el DNI tiene 8 numeros se busca al cliente
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
        //se cuentan tambien las funciones de fechas pasadas, solo para poder
        //explicar la diferencia en el aviso de abajo
        boolean tieneFunciones = false;
        for (Funcion f : funcionDao.listAll()) {
            //CORREGIDO: se agregan todas las funciones de la pelicula, sin
            //filtrar por estado de la sala. El filtro se aplica al elegir la
            //funcion (puedeVenderse), asi el usuario ve que existen funciones
            //y entiende por que no puede vender esa.
            if (f.getPelicula() == null
                    || !String.valueOf(f.getPelicula().getidpelicula())
                    .equals(String.valueOf(pelicula.getidpelicula()))) {
                continue;
            }
            //la pelicula si tiene funciones programadas (de cualquier fecha)
            tieneFunciones = true;
            //MEJORA 7: una funcion cancelada ya no se ofrece para vender
            if (f.isCancelada()) {
                continue;
            }
            //solo se ofrecen de hoy en adelante: las de fechas ya pasadas no
            //sirven para vender y solo alargaban la lista
            if (esDeHoyOEnAdelante(f)) {
                cboFuncion.getItems().add(f);
            }
        }
        if (cboFuncion.getItems().isEmpty()) {
            //CORREGIDO: el aviso ya no dice "no tiene funciones" cuando en
            //realidad la pelicula solo tiene funciones de fechas pasadas
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

    /**
     * MEJORA 5b: revisa si una funcion es de hoy o de una fecha futura.
     * Las de fechas pasadas no se listan para vender; las de hoy si, aunque
     * ya hayan empezado (esas se muestran marcadas y se bloquean al elegirlas).
     */
    private boolean esDeHoyOEnAdelante(Funcion f) {
        if (f == null || f.getFecha() == null || f.getFecha().isBlank()) {
            //sin fecha guardada no hay nada que comparar: se deja ver
            return true;
        }
        String fecha = f.getFecha().trim();
        if (fecha.length() > 10) {
            fecha = fecha.substring(0, 10);
        }
        return fecha.compareTo(LocalDate.now().toString()) >= 0;
    }

    /**
     * MEJORA 5b: revisa si una funcion ya comenzo.
     * Se compara la fecha como texto (yyyy-MM-dd) y, si es el dia de hoy,
     * tambien la hora. Una funcion pasada ya no se puede vender.
     */
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
        //es hoy: se compara la hora de inicio con la hora actual
        try {
            LocalTime hora = LocalTime.parse(f.getHorario().trim());
            return !hora.isAfter(LocalTime.now());
        } catch (Exception e) {
            //si la hora guardada no tiene un formato entendible, se deja pasar
            return false;
        }
    }

    //paso 2: solo se pueden elegir butacas libres
    private void cargarButacas() {
        gridButacas.getChildren().clear();
        butacasSeleccionadas.clear();
        lblButaca.setText("Butaca: -");
        //CORREGIDO: el precio se limpia al cambiar de funcion; antes quedaba
        //el de la funcion anterior aunque la nueva no se pudiera vender
        lblPrecio.setText("Precio: -");
        //se limpia el aviso anterior: al cambiar de funcion el mensaje viejo
        //(por ejemplo "esa funcion ya empezo") ya no corresponde
        lblMensaje.setText("");
        Funcion f = cboFuncion.getValue();
        if (f == null || f.getSala() == null) {
            return;
        }
        //CORREGIDO: si la funcion ya empezo o su sala esta inactiva se avisa
        //aqui mismo, en vez de dejar el mapa de butacas vacio sin motivo
        if (!puedeVenderse(f)) {
            lblMensaje.setText(mensajeNoVendible(f));
            actualizarEstadoSiguiente();
            return;
        }
        lblPrecio.setText("Precio: S/ " + f.getPrecio() + " c/u  ·  " + f.getSala().getEtiqueta());
        //ancho de la sala: las columnas guardadas al crearla
        //(se usan solo para numerar las butacas, no para la grilla)
        columnasSala = f.getSala().getColumnas();

        Set<Integer> ocupadas = new HashSet<>();
        for (Butaca b : butacaDao.listarOcupadas(f.getIdfuncion())) {
            ocupadas.add(b.getIdbutaca());
        }

        List<Butaca> butacas = butacaDao.listarPorSala(f.getSala().getIdsala());
        //CORREGIDO: si la sala todavia no tiene butacas la grilla quedaba
        //vacia sin explicar el motivo; ahora se avisa y no se puede seguir
        if (butacas.isEmpty()) {
            lblMensaje.setText("Esta sala todavía no tiene butacas. Pida al administrador que las genere.");
            actualizarEstadoSiguiente();
            return;
        }
        //si la sala es vieja y no guardo columnas, se deducen del numero mas
        //alto para no romper la numeracion
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
            //CORREGIDO: el texto del boton es el numero global (1..capacidad)
            //y no el numero de la butaca dentro de la fila
            Button boton = new Button(String.valueOf(numeroVisible(b)));
            boton.getStyleClass().add("butaca");
            //CORREGIDO: tamaño minimo en vez de fijo, para que un numero
            //de 3 digitos (por ejemplo 100) no se corte
            boton.setMinSize(36, 36);
            if (ocupadas.contains(b.getIdbutaca())) {
                boton.getStyleClass().add("butaca-ocupada");
                boton.setText("✕");
                boton.setDisable(true);
            } else {
                boton.setOnAction(e -> seleccionarButaca(b, boton));
            }
            //la posicion en la grilla sigue igual: columna y fila de la butaca
            gridButacas.add(boton, b.getNumero() - 1, b.getFila() - 1);
        }
        actualizarEstadoSiguiente();
    }

    //se marca y se desmarca cada butaca, asi se pueden juntar varias
    private void seleccionarButaca(Butaca b, Button boton) {
        if (butacasSeleccionadas.contains(b)) {
            butacasSeleccionadas.remove(b);
            boton.getStyleClass().remove("butaca-elegida");
            //al desmarcar se vuelve a poner el numero global, no el de la fila
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
        //CORREGIDO: la busqueda se hace desde un listener del DNI; si la base
        //fallaba, la excepcion llegaba hasta JavaFX y rompia la ventana
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
        //CORREGIDO: el telefono no es obligatorio en la tabla cliente ni en el
        //formulario de clientes; aqui si se exigia y un cliente ya registrado
        //sin telefono no podia comprar. Ahora solo se piden los datos clave.
        if (cboFuncion.getValue() == null || butacasSeleccionadas.isEmpty()
                || txtDni.getText().isBlank() || txtNombre.getText().isBlank()
                || txtApellidos.getText().isBlank()) {
            utilAlerts.mostrarAlerts("Datos incompletos",
                    "Complete la función, las butacas, el DNI, los nombres y los apellidos.",
                    "ERROR").showAndWait();
            return false;
        }
        //CORREGIDO: aunque la funcion se vea en el combo, no se puede vender
        //si ya empezo o si su sala esta inactiva
        if (!puedeVenderse(cboFuncion.getValue())) {
            utilAlerts.mostrarAlerts("Función no disponible",
                    mensajeNoVendible(cboFuncion.getValue()), "WARNING").showAndWait();
            return false;
        }
        String dni = txtDni.getText().trim();
        //MEJORA 14: la regla del DNI vive en Validaciones (compartida y probada)
        if (!Validaciones.dni(dni)) {
            utilAlerts.mostrarAlerts("DNI invalido",
                    "El DNI debe tener 8 números.", "WARNING").showAndWait();
            return false;
        }
        //MEJORA 14: email y telefono validos cuando se escriben
        if (!Validaciones.email(txtEmail.getText())) {
            utilAlerts.mostrarAlerts("Correo invalido",
                    "Escriba un correo válido, por ejemplo nombre@correo.com.",
                    "WARNING").showAndWait();
            return false;
        }
        if (!txtTelefono.getText().isBlank() && !Validaciones.telefono(txtTelefono.getText())) {
            utilAlerts.mostrarAlerts("Teléfono invalido",
                    "El teléfono debe tener entre 6 y 15 números.", "WARNING").showAndWait();
            return false;
        }
        //MEJORA 6: siempre debe haber una forma de pago
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

        //MEJORA 4b: el cliente se arma SIEMPRE con lo que esta escrito en el
        //formulario. Si su DNI ya existe se conserva su id, y el DAO actualiza
        //sus datos dentro de la misma transaccion de la venta.
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
        //CORREGIDO: el genero elegido tambien se guarda en el cliente nuevo
        cliente.setGenero(cbgenero.getValue());
        cliente.setTelefono(txtTelefono.getText().trim());

        int idUsuario = Sesion.getUsuario() != null ? Sesion.getUsuario().getIdusuario() : 0;
        String codigo;
        try {
            //el DAO guarda al cliente (si es nuevo) y una venta por butaca en una sola transaccion
            codigo = ventaDao.registrarVentas(f, cliente, butacasSeleccionadas, idUsuario,
                    cbMetodoPago.getValue());
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

        //MEJORA 9: la venta queda registrada en la bitacora de auditoria
        Bitacora.registrar("VENTA", "Compra " + codigo + " de "
                + butacasSeleccionadas.size() + " boleto(s) - S/ "
                + (f.getPrecio() * butacasSeleccionadas.size())
                + " - " + cbMetodoPago.getValue());

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
            //en el comprobante va el mismo numero que se ve en el mapa
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

        //MEJORA 9: ademas de mostrarlo, el comprobante se guarda como archivo de
        //texto (.txt), que se abre con el Bloc de notas para imprimirlo
        File archivo = Comprobante.guardar(codigo, texto);
        if (archivo != null) {
            texto = texto + "\n\nComprobante guardado en:\n" + archivo.getAbsolutePath();
        } else {
            texto = texto + "\n\nNo se pudo guardar el comprobante en disco.";
        }
        Alert alert = utilAlerts.mostrarAlerts("Comprobante de venta", texto, "INFORMATION");
        alert.showAndWait();
    }

    //CORREGIDO: numero visible de la butaca. Se cuentan las butacas de las
    //filas anteriores y se suma la posicion dentro de su fila. La butaca sigue
    //guardada como fila + numero; el numero global solo se calcula al mostrar.
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
            //mismo numero global que el boton de la butaca
            texto.append(numeroVisible(b));
        }
        return texto.toString();
    }
    //muestra una de las dos paginas del formulario
    private void mostrarPaso(int paso) {
        boolean enDos = (paso == 1);
        panelPaso12.setVisible(enDos);
        panelPaso12.setManaged(enDos);
        panelPaso3.setVisible(!enDos);
        panelPaso3.setManaged(!enDos);
        //la ventana se ajusta al contenido nuevo
        Stage stage = (Stage) cboPelicula.getScene().getWindow();
        stage.sizeToScene();
    }
    //el boton SIGUIENTE solo se activa cuando hay una funcion vendible y butacas
    private void actualizarEstadoSiguiente() {
        boolean listo = puedeVenderse(cboFuncion.getValue())
                && !butacasSeleccionadas.isEmpty();
        btnSiguiente.setDisable(!listo);
    }

    /**
     * MEJORA 5b y MEJORA 6: una funcion se puede vender solo si todavia no
     * empezo y si su sala no esta inactiva.
     */
    private boolean puedeVenderse(Funcion f) {
        //MEJORA 7: tampoco se vende una funcion cancelada
        return f != null && !yaPaso(f) && !salaInactiva(f) && !f.isCancelada();
    }

    //MEJORA 6: la sala esta en mantenimiento (estado INACTIVA)
    private boolean salaInactiva(Funcion f) {
        if (f == null || f.getSala() == null || f.getSala().getEstado() == null) {
            return false;
        }
        return "INACTIVA".equalsIgnoreCase(f.getSala().getEstado().trim());
    }

    //explica por que una funcion aparece en la lista pero no se puede vender
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

    //paso 1 y 2 listos: se pasa a los datos del cliente
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

    //regresa a elegir pelicula, funcion y butacas (se mantiene lo elegido)
    @FXML
    private void handleVolver() {
        mostrarPaso(1);
    }
}
