package pe.edu.upeu.gestioo_de_cine.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pe.edu.upeu.gestioo_de_cine.HelloApplication;
import pe.edu.upeu.gestioo_de_cine.implementacion.ClienteDaoImpl;
import pe.edu.upeu.gestioo_de_cine.interfaces.IClienteDao;
import pe.edu.upeu.gestioo_de_cine.modelo.Cliente;
import pe.edu.upeu.gestioo_de_cine.utils.Sesion;
import pe.edu.upeu.gestioo_de_cine.utils.UtilAlerts;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteController {


        private static UtilAlerts utilAlert = new UtilAlerts();

        //definimos la tabla
        @FXML
        private TableView<Cliente> tableCliente;
        //variable correlativa
        @FXML
        private TableColumn<Cliente, Number> ColNumero;
        @FXML
        private TableColumn<Cliente, Integer> ColId;
        @FXML
        private TableColumn<Cliente, String> ColNombre;
        @FXML
        private TableColumn<Cliente, String> ColApellidos;
        @FXML
        private TableColumn<Cliente, String> ColDni;
        @FXML
        private TableColumn<Cliente, String> ColEmail;
        @FXML
        private TableColumn<Cliente, String> ColGenero;
        @FXML
        private TableColumn<Cliente, String> ColTelefono;

        private final IClienteDao clienteDao = new ClienteDaoImpl();
        private ObservableList<Cliente> listaClientes;
        //texto de busqueda y lista completa para poder filtrar
        @FXML
        private TextField txtBuscar;
        private List<Cliente> todosLosClientes = new ArrayList<>();

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
                    cellData -> cellData.getValue().idclienteProperty().asObject());
            ColNombre.setCellValueFactory(
                    cellData -> cellData.getValue().nombreProperty());
            ColApellidos.setCellValueFactory(
                    cellData -> cellData.getValue().apellidosProperty());
            ColDni.setCellValueFactory(
                    cellData -> cellData.getValue().dniProperty());
            ColEmail.setCellValueFactory(
                    cellData -> cellData.getValue().emailProperty());
            ColGenero.setCellValueFactory(
                    cellData -> cellData.getValue().generoProperty());
            ColTelefono.setCellValueFactory(
                    cellData -> cellData.getValue().telefonoProperty());
            //llamar al metodo
            cargarDatos();
        }

        private void cargarDatos() {
            todosLosClientes = clienteDao.listAll();
            listaClientes = FXCollections.observableArrayList();
            tableCliente.setItems(listaClientes);
            filtrar();
        }

        @FXML
        private void handleBuscar() {
            filtrar();
        }

        //filtra la lista por nombres, apellidos y DNI con un for normal
        private void filtrar() {
            String texto = txtBuscar != null ? txtBuscar.getText().trim().toLowerCase() : "";
            listaClientes.clear();
            for (Cliente c : todosLosClientes) {
                if (texto.isEmpty() || contiene(c.getNombre(), texto)
                        || contiene(c.getApellidos(), texto) || contiene(c.getDni(), texto)) {
                    listaClientes.add(c);
                }
            }
        }

        private boolean contiene(String valor, String texto) {
            return valor != null && valor.toLowerCase().contains(texto);
        }

        //el admin puede todo; el empleado solo trabaja en ventas
        private boolean sinPermiso() {
            if (Sesion.esAdmin()) {
                return false;
            }
            utilAlert.mostrarAlerts("Sin permiso",
                    "Solo el administrador puede administrar clientes.", "WARNING").showAndWait();
            return true;
        }

        @FXML
        private void handleDelete() {
            if (sinPermiso()) return;
            Cliente selectId = tableCliente.getSelectionModel().getSelectedItem();
            if (selectId != null) {
                Alert confirm = utilAlert.mostrarAlerts("Confirmar eliminacion",
                        "Estas seguro de eliminar el cliente: " + selectId.getNombre() + "?",
                        "CONFIRMATION");
                Optional<ButtonType> result = confirm.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    //CORREGIDO: si la base rechaza el borrado la excepcion salia
                    //sin aviso; ahora se captura y se muestra el motivo
                    try {
                        if (clienteDao.delete(selectId)) {
                            cargarDatos();
                        } else {
                            utilAlert.mostrarAlerts("Error",
                                    "No se pudo eliminar el cliente", "ERROR").showAndWait();
                        }
                    } catch (RuntimeException e) {
                        System.out.println("Error al eliminar cliente: " + e.getMessage());
                        utilAlert.mostrarAlerts("No se pudo eliminar",
                                e.getMessage(), "ERROR").showAndWait();
                    }
                }
            } else {
                Alert warning = utilAlert.mostrarAlerts("Seleccion requerida",
                        "Seleccione un cliente para eliminar", "WARNING");
                warning.showAndWait();
            }
        }

        @FXML
        private void handleNew() {
            if (sinPermiso()) return;
            showForm(null);
        }

        @FXML
        private void handleEdit() {
            if (sinPermiso()) return;
            Cliente selectClient = tableCliente.getSelectionModel().getSelectedItem();
            if (selectClient != null) {
                Cliente clientFull = clienteDao.searchById(selectClient.getIdcliente());
                //CORREGIDO: si el cliente ya no existe se abria el formulario
                //como si fuera nuevo; ahora se avisa y se recarga la tabla
                if (clientFull == null) {
                    utilAlert.mostrarAlerts("Cliente no encontrado",
                            "El cliente ya no existe en la base de datos.",
                            "WARNING").showAndWait();
                    cargarDatos();
                    return;
                }
                showForm(clientFull);
            } else {
                Alert alert = utilAlert.mostrarAlerts("Seleccion requerida",
                        "Seleccione un cliente de la tabla para editar.", "WARNING");
                alert.showAndWait();
            }
        }

        /**
         * MEJORA 3: abre una ventana con todas las compras del cliente elegido.
         * Se reutiliza la consulta de ventas (VentaDaoImpl.listarPorCliente).
         */
        @FXML
        private void handleHistorial() {
            if (sinPermiso()) return;
            Cliente seleccionado = tableCliente.getSelectionModel().getSelectedItem();
            if (seleccionado == null) {
                utilAlert.mostrarAlerts("Seleccion requerida",
                        "Seleccione un cliente para ver sus compras.", "WARNING").showAndWait();
                return;
            }
            try {
                FXMLLoader loader = new FXMLLoader(HelloApplication.class
                        .getResource("/pe/edu/upeu/gestioo_de_cine/views/cliente-historial.fxml"));
                Parent root = loader.load();
                ClienteHistorialController controller = loader.getController();
                controller.setCliente(seleccionado);
                Stage stage = new Stage();
                stage.setTitle("Historial de compras - " + seleccionado.getNombre());
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(new Scene(root));
                stage.showAndWait();
            } catch (Exception e) {
                System.out.println("Error al abrir el historial del cliente: " + e.getMessage());
                utilAlert.mostrarAlerts("Error",
                        "No se pudo abrir el historial del cliente.", "ERROR").showAndWait();
            }
        }

        //arma el texto del aviso: si SQLite responde "database is locked" el problema
        //no es el DNI sino otra ventana del programa que tiene la base abierta
        private String mensajeError(RuntimeException e, String mensajeNormal) {
            String detalle = e.getMessage() != null ? e.getMessage() : "";
            String minuscula = detalle.toLowerCase();
            if (minuscula.contains("locked") || minuscula.contains("busy")) {
                return "Otra ventana del sistema tiene la base de datos abierta.\n"
                        + "Cierre todas las ventanas del programa y vuelva a intentarlo.";
            }
            return mensajeNormal + "\n" + detalle;
        }

        private void showForm(Cliente c) {
            try {
                FXMLLoader loader = new FXMLLoader(HelloApplication.class
                        .getResource("/pe/edu/upeu/gestioo_de_cine/views/cliente-form.fxml"));
                Parent root = loader.load();
                ClienteFormController controller = loader.getController();
                controller.setCliente(c); //pasamos el cliente null si es nuevo
                Stage stage = new Stage();
                stage.setTitle(c == null ? "Nuevo Cliente" : "Editar Cliente");
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(new Scene(root));
                stage.showAndWait();
                if (controller.isSaved()) {
                    Cliente clienteProcesado = controller.getCliente();
                    if (c == null) {
                        boolean result;
                        try {
                            result = clienteDao.insert(clienteProcesado);
                        } catch (RuntimeException e) {
                            //por ejemplo: el DNI ya esta registrado en otro cliente
                            //o la base esta tomada por otra ventana del sistema
                            utilAlert.mostrarAlerts("No se pudo guardar",
                                    mensajeError(e, "El cliente no se registró. Revise si el DNI ya existe."),
                                    "ERROR").showAndWait();
                            return;
                        }
                        if (result) {
                            utilAlert.mostrarAlerts("Success",
                                    "Registro satisfactorio!!!", "CONFIRMATION").showAndWait();
                        } else {
                            utilAlert.mostrarAlerts("Error",
                                    "Error al insertar!!!", "ERROR").showAndWait();
                        }
                    } else {
                        clienteProcesado.setIdcliente(c.getIdcliente());
                        boolean result;
                        try {
                            result = clienteDao.update(clienteProcesado);
                        } catch (RuntimeException e) {
                            //por ejemplo: el DNI ya esta registrado en otro cliente
                            //o la base esta tomada por otra ventana del sistema
                            utilAlert.mostrarAlerts("No se pudo actualizar",
                                    mensajeError(e, "El cliente no se actualizó. Revise si el DNI ya existe."),
                                    "ERROR").showAndWait();
                            return;
                        }
                        if (result) {
                            utilAlert.mostrarAlerts("Update",
                                    "Registro actualizado!!!", "WARNING").showAndWait();
                        } else {
                            utilAlert.mostrarAlerts("Error",
                                    "Error al actualizar!!!", "ERROR").showAndWait();
                        }
                    }
                    cargarDatos();
                }
            } catch (Exception e) {
                System.out.println("Error al abrir el formulario de cliente: " + e.getMessage());
                utilAlert.mostrarAlerts("Error",
                        "No se pudo abrir el formulario de cliente.", "ERROR").showAndWait();
            }
        }
    }

