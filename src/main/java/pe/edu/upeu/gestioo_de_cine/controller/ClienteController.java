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
import pe.edu.upeu.gestioo_de_cine.utils.UtilAlerts;

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

    private final IClienteDao clienteDao = new ClienteDaoImpl();
    private ObservableList<Cliente> listaClientes;

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
        //llamar al metodo
        cargarDatos();
    }

    private void cargarDatos() {
        listaClientes = FXCollections.observableArrayList(clienteDao.listAll());
        tableCliente.setItems(listaClientes);
    }

    @FXML
    private void handleDelete() {
        Cliente selectId = tableCliente.getSelectionModel().getSelectedItem();
        if (selectId != null) {
            Alert confirm = utilAlert.mostrarAlerts("Confirmar eliminacion",
                    "Estas seguro de eliminar el cliente: " + selectId.getNombre() + "?",
                    "CONFIRMATION");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (clienteDao.delete(selectId)) {
                    listaClientes.remove(selectId);
                } else {
                    utilAlert.mostrarAlerts("Error",
                            "No se pudo eliminar el cliente", "ERROR").showAndWait();
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
        showForm(null);
    }

    @FXML
    private void handleEdit() {
        Cliente selectClient = tableCliente.getSelectionModel().getSelectedItem();
        if (selectClient != null) {
            Cliente clientFull = clienteDao.searchById(selectClient.getIdcliente());
            showForm(clientFull);
        } else {
            Alert alert = utilAlert.mostrarAlerts("Seleccion requerida",
                    "Seleccione un cliente de la tabla para editar.", "WARNING");
            alert.showAndWait();
        }
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
                    boolean result = clienteDao.insert(clienteProcesado);
                    if (result) {
                        utilAlert.mostrarAlerts("Success",
                                "Registro satisfactorio!!!", "CONFIRMATION").show();
                    } else {
                        utilAlert.mostrarAlerts("Error",
                                "Error al insertar!!!", "ERROR").show();
                    }
                } else {
                    clienteProcesado.setIdcliente(c.getIdcliente());
                    boolean result = clienteDao.update(clienteProcesado);
                    if (result) {
                        utilAlert.mostrarAlerts("Update",
                                "Registro actualizado!!!", "WARNING").showAndWait();
                    } else {
                        utilAlert.mostrarAlerts("Error",
                                "Error al actualizar!!!", "ERROR").show();
                    }
                }
                cargarDatos();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
